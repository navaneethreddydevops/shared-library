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
        stage('Build environment') {
            steps {
                sh '''
                pip3 install -r requirements.txt --user
                '''
            }
        }
        stage('Test environment') {
            steps {
                sh '''
                pip3 list
                which pip3
                which python3
                '''
            }
        }
            stage('DockerCleanUp') {
                steps {
                    script {
                        dockerClean()
                    }
                }
            }
            // stage('Static code metrics') {
            // steps {
            //     echo 'Raw metrics'
            //     // sh  '''
            //     //     radon raw --json irisvmpy > raw_report.json
            //     //     radon cc --json irisvmpy > cc_report.json
            //     //     radon mi --json irisvmpy > mi_report.json
            //     //     sloccount --duplicates --wide irisvmpy > sloccount.sc
            //     //     '''
            //     echo 'Test coverage'
            //     sh  '''
            //         coverage run irisvmpy/iris.py 1 1 2 3
            //         python3 -m coverage xml -o reports/coverage.xml
            //         '''
            //     echo 'Style check'
            //     sh  '''
            //         pylint irisvmpy || true
            //         '''
            // }
            // post {
            //     always {
            //         step([$class: 'CoberturaPublisher',
            //                        autoUpdateHealth: false,
            //                        autoUpdateStability: false,
            //                        coberturaReportFile: 'reports/coverage.xml',
            //                        failNoReports: false,
            //                        failUnhealthy: false,
            //                        failUnstable: false,
            //                        maxNumberOfBuilds: 10,
            //                        onlyStable: false,
            //                        sourceEncoding: 'ASCII',
            //                        zoomCoverageChart: false])
            //         }
            //     }
            // }
        //     stage('Unit tests') {
        //     steps {
        //         sh  '''
        //             python3 -m pytest --verbose --junit-xml reports/unit_tests.xml
        //             '''
        //     }
        //     post {
        //         always {
        //             // Archive unit tests for the future
        //             junit allowEmptyResults: true, testResults: 'reports/unit_tests.xml'
        //             }
        //         }
        //     }
        //     stage('Acceptance tests') {
        //     steps {
        //         sh  '''
        //             behave -f=formatters.cucumber_json:PrettyCucumberJSONFormatter -o ./reports/acceptance.json || true
        //             '''
        //     }
        //     post {
        //         always {
        //             cucumber (buildStatus: 'SUCCESS',
        //             fileIncludePattern: '**/*.json',
        //             jsonReportDirectory: './reports/',
        //             sortingMethod: 'ALPHABETICAL')
        //         }
        //     }
        // }
        stage('Build package') {
            when {
                expression {
                    currentBuild.result == null || currentBuild.result == 'SUCCESS'
                }
            }
            steps {
                sh  '''
                    python3 setup.py bdist_wheel
                    '''
            }
            post {
                always {
                    // Archive unit tests for the future
                    archiveArtifacts allowEmptyArchive: true, artifacts: 'dist/*whl', fingerprint: true
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
        }
    }
}
