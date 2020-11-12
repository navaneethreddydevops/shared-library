#!/usr/bin/groovy
/* groovylint-disable LineLength */
import com.utils

def call() {
    def config
    currentBuild.result = 'SUCCESS'
    pipeline {
        agent {
            label ('master')
        }
        options {
            parallelsAlwaysFailFast()
            timeout(time: 1, unit: 'HOURS')
            buildDiscarder (logRotator(numToKeepStr: '10'))
        }
        parameters {
            choice (name: 'Environment', choices: ['MyCustomCloudDev', 'MyCustomCloudQA', 'MyCustomCloudProd'], description: 'Environment')
            choice (name: 'Region', choices: ['us-east-1', 'us-west-2', 'eu-west-2', 'ap-southeast-2'], description: 'RegionToDeploy')
            choice (name: 'app', choices: ['Java', 'Python', 'Terraform', 'Docker', 'Helm', 'Cloudformation', 'Lambda'], description: 'BuildType')
        }
        stages {
            stage('ParameterCheck') {
                steps {
                    script {
                        if (
                        (params.Environment == 'null' || params.Region == 'null')
                        || (params.app == 'null' && params.app == '')
                        ) {
                            currentBuild.result = 'ABORTED'
                            error('Prameters not provided properly')
                        }
                    }
                }
            }
            stage('Checkout') {
                steps {
                    script {
                        checkout scm
                        config = pipelineConfig()
                        println 'Properties from App Repo :' + config + params
                        Plugins = pulgins()
                        println 'Plugins of jenkins:' + Plugins
                    }
                }
            }
            stage('Prerequistes') {
                steps {
                    script {
                        sh 'echo "Hello"'
                    }
                }
            }
            stage('ChangeDir') {
                steps {
                    script {
                        sh 'echo "Hello2"'
                    }
                }
            }
            stage('Build & Test') {
                steps {
                    script {
                        sh 'mvn --version'
                        sh 'ls -lrt'
                        sh "mvn -Ddb_port=${config.DB_PORT} -Dredis_port=${config.REDIS_PORT} clean install"
                    }
                }
            }
            stage ('Push Docker Image') {
                steps {
                    script {
                        docker.withRegistry('https://navaneethreddydevops.com', 'dockerhub') {
                            sh "docker build -t navaneethreddydevops.com/${config.SERVICE_NAME}:${BUILD_NUMBER} ."
                            sh "docker push navaneethreddydevops.com/${config.SERVICE_NAME}:${BUILD_NUMBER}"
                        }
                    }
                }
            }
            stage ('Deploy') {
                steps {
                    script {
                        echo "We are going to deploy ${p.SERVICE_NAME}"
                        sh "kubectl set image deployment/${p.SERVICE_NAME} ${config.SERVICE_NAME}=opstree/${config.SERVICE_NAME}:${BUILD_NUMBER} "
                        sh "kubectl rollout status deployment/${config.SERVICE_NAME} -n ${config.ENVIRONMENT_NAME} "
                    }
                }
            }
        }
    }
}
