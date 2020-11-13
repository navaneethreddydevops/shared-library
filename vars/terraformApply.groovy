def call(Map config) {
    config.args = config.args ?: '-no-color'
    withEnv(['TERRAFORM_DIR=/usr/local/bin/terraform']) {
        withCredentials([file(credentialsId:config.credentialsId, variable:'AWS_CREDS')]) {
            if(config.workspace != null && config.workspace !=''){
                sh "terraform workspace new ${config.workspace}" || terraform workspace select ${config.workspace} "
                sh 'terraform init'
            }
            sh "terraform ${config.args}"
        }
    }
}
