#!/usr/bin/groovy
/* groovylint-disable LineLength, NestedBlockDepth */

def call() {
    def config
    currentBuild.result = 'SUCCESS'
    pipeline {
        agent {
            label ('master')
        }
        triggers {
            cron('H */4 * * 1-5')
        }
        // tools {
        //     maven 'apache-maven-3.0.1'
        // }
        options {
            parallelsAlwaysFailFast()
            timeout(time: 1, unit: 'HOURS')
            buildDiscarder (logRotator(numToKeepStr: '10'))
        }
        parameters {
            choice (name: 'Environment', choices: ['MyCustomCloudDev', 'MyCustomCloudQA', 'MyCustomCloudProd'], description: 'Environment')
            choice (name: 'Region', choices: ['us-east-1', 'us-west-2', 'eu-west-2', 'ap-southeast-2'], description: 'RegionToDeploy')
            choice (name: 'app', choices: ['Java', 'Python', 'Terraform', 'Docker', 'Helm', 'Cloudformation', 'Lambda'], description: 'BuildType')
            string(name: 'PERSON', defaultValue: 'Mr Jenkins', description: 'Who should I say hello to?')
            booleanParam(name: 'TOGGLE', defaultValue: true, description: 'Toggle this value')
            password(name: 'PASSWORD', defaultValue: 'SECRET', description: 'Enter a password')
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
                    }
                }
            }
            stage('Prerequistes') {
                steps {
                    script {
                        sh 'which java'
                        sh 'which docker'
                    }
                }
            }
            stage('DockerCleanUp') {
                steps {
                    script {
                        dockerClean()
                    }
                }
            }
            stage('Sonarqube') {
                steps {
                    script {
                        withSonarQubeEnv('sonar') {
                        println ${ env.SONAR_HOST_URL }
                        }
                    }
                }
            }
            stage('SonarQube Quality Gate') {
                steps {
                    script {
                        timeout(time: 5, unit: 'MINUTES') {
                            def qualitygate = waitForQualityGate()
                            if (qualitygate.status != 'OK') {
                                abortPipeline:true
                                error "Pipeline aborted due to quality gate failure:${qualitygate.status}"
                            }
                            else {
                                echo 'Quality gate passed'
                            }
                        }
                    }
                }
            }
            // stage('DockerBuildPush') {
            //     steps {
            //         script {
            //             dockerbuildPipeline()
            //         }
            //     }
            // }
            // stage('Example Deploy') {
            //     when {
            //             branch 'production'
            //     }
            //         steps {
            //             echo 'Deploying'
            //         }
            // }

        // stage ('Push Docker Image') {
        //     steps {
        //         script {
        //             docker.withRegistry('https://navaneethreddydevops.com', 'dockerhub') {
        //                 sh "docker build -t navaneethreddydevops.com/${config.SERVICE_NAME}:${BUILD_NUMBER} ."
        //                 sh "docker push navaneethreddydevops.com/${config.SERVICE_NAME}:${BUILD_NUMBER}"
        //             }
        //         }
        //     }
        // }
        // stage ('Deploy') {
        //     steps {
        //         script {
        //             echo "We are going to deploy ${p.SERVICE_NAME}"
        //             sh "kubectl set image deployment/${p.SERVICE_NAME} ${config.SERVICE_NAME}=opstree/${config.SERVICE_NAME}:${BUILD_NUMBER} "
        //             sh "kubectl rollout status deployment/${config.SERVICE_NAME} -n ${config.ENVIRONMENT_NAME} "
        //         }
        //     }
        // }
        }
    }
}
