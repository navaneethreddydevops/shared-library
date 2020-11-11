#!/usr/bin/groovy
def call() {
    pipeline {
        agent {
            label ('master')
        }
        options {
            buildDiscarder (logRotator(numToKeepStr: '10'))
        }
        parameters {
            string (name : 'environment', defaultValue: 'dev')
        }
        stages {
            stage('Checkout') {
                steps {
                    checkout scm
                }
            }
            /* groovylint-disable-next-line VariableTypeRequired */
            def config = pipelineConfig()
            stage('Prerequistes') {
                steps {
                    serviceName = sh (
                    steps: "echo ${config.SERVICE_NAME}|cut -d '-' -f 1",
                    returnStdout: true
                ).trim()
                }
            }
            stage('ChangeDir') {
                steps {
                    sh 'ls -lrt'
                    sh 'cd cicd/'
                }
            }
            stage('Build & Test') {
                steps {
                    sh 'mvn --version'
                    sh 'ls -lrt'
                    sh "mvn -Ddb_port=${config.DB_PORT} -Dredis_port=${config.REDIS_PORT} clean install"
                }
            }
            stage ('Push Docker Image') {
                steps {
                    docker.withRegistry('https://navaneethreddydevops.com', 'dockerhub') {
                        sh "docker build -t navaneethreddydevops.com/${config.SERVICE_NAME}:${BUILD_NUMBER} ."
                        sh "docker push navaneethreddydevops.com/${config.SERVICE_NAME}:${BUILD_NUMBER}"
                    }
                }
            }
            stage ('Deploy') {
                steps {
                    echo "We are going to deploy ${p.SERVICE_NAME}"
                    sh "kubectl set image deployment/${p.SERVICE_NAME} ${config.SERVICE_NAME}=opstree/${config.SERVICE_NAME}:${BUILD_NUMBER} "
                    sh "kubectl rollout status deployment/${config.SERVICE_NAME} -n ${config.ENVIRONMENT_NAME} "
                }
            }
        }
    }
}
