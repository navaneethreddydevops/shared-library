def bucket = 'deployment-packages-mlabouardy'
def functionName = 'emr-cicd'
def region = 'us-east-1'
def commitID() {
    sh 'git rev-parse HEAD > .git/commitID'
    def commitID = readFile('.git/commitID').trim()
    sh 'rm .git/commitID'
    commitID
}

node('master') {
    stage('Checkout') {
        checkout scm
    }

    stage('Virtualenv') {
        sh '''
        virtualenv lambda
        source lambda/bin/activate
        pip3 install -r requirements
        deactivate
        cd lambda/lib/python3.8/site-packages
        zip -r my-deployment-package.zip .
        zip -g my-deployment-package.zip lambda_function.py
        '''
    }
    stage('Publish') {
        sh "aws s3 cp ${commitID()}.zip s3://${bucket}"
    }

    stage('Deploy') {
        sh "aws lambda update-function-code --function-name ${functionName} \
                --s3-bucket ${bucket} \
                --s3-key ${commitID()}.zip \
                --region ${region}"
    }

    if (env.BRANCH_NAME == 'master') {
        stage('Publish') {
            def lambdaVersion = sh(
                script: "aws lambda publish-version --function-name ${functionName} --region ${region} | jq -r '.Version'",
                returnStdout: true
            )
            sh "aws lambda update-alias --function-name ${functionName} --name production --region ${region} --function-version ${lambdaVersion}"
        }
    }
}

