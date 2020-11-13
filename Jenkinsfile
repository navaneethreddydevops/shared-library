#!/usr/bin/groovy
import groovy.json.*
import jenkins.model.*

pipeline{
    agent{
        label "master"
    }
    stages{
        stage("Clone"){
            steps{
                sh 'git clone https://github.com/navaneethreddydevops/shared-library.git'
                sh 'git clean -dfx'
            }
        }
        stage("Clone"){
        jobDsl(
            ignoreMissingFiles: true,
            ignoreExisting: false,
            removeJobAction: 'DELETE',
            targets: 'Jenkins.dsl,/*.dsl'
        )
        }
    }
}