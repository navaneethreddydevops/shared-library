import groovy.json.*
import jenkins.model.*
pipeline{
node('master'){
    stages{
    stage(Clone){
        sh 'git clone https://github.com/navaneethreddydevops/shared-library.git'
        sh 'git clean -dfx'
    }
    stage('Validate DSl'){
        jobDsl(
            ignoreMissingFiles: true,
            ignoreExisting: false,
            removeJobAction: 'DELETE',
            targets: 'Jenkins.dsl,/*.dsl'
        )
    }
    }
    }
}