#!/usr/bin/groovy
def call() {
    node('master') {
        stage('Checkout') {
            checkout scm
        }
        /* groovylint-disable-next-line VariableTypeRequired */
        def config = pipelineConfig()

        stage('Prerequistes') {
            serviceName = sh (
                    script: "echo ${config.SERVICE_NAME}|cut -d '-' -f 1",
                    returnStdout: true
                ).trim()
        }
        stage('Build & Test') {
                sh 'cd cicd'
                sh 'mvn --version'
                sh "mvn -Ddb_port=${config.DB_PORT} -Dredis_port=${config.REDIS_PORT} clean install"
        }
        stage ('Push Docker Image') {
            docker.withRegistry('https://navaneethreddydevops.com', 'dockerhub') {
                sh "docker build -t navaneethreddydevops.com/${config.SERVICE_NAME}:${BUILD_NUMBER} ."
                sh "docker push navaneethreddydevops.com/${config.SERVICE_NAME}:${BUILD_NUMBER}"
            }
        }
        stage ('Deploy') {
            echo "We are going to deploy ${p.SERVICE_NAME}"
            sh "kubectl set image deployment/${p.SERVICE_NAME} ${config.SERVICE_NAME}=opstree/${config.SERVICE_NAME}:${BUILD_NUMBER} "
            sh "kubectl rollout status deployment/${config.SERVICE_NAME} -n ${config.ENVIRONMENT_NAME} "
        }
    }
}
