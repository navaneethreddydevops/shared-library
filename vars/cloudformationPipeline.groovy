        stage ('Release') {
      steps {
                script {
                    def apply = true
                    def status = null
                    try {
                            status = sh(script: "aws cloudformation describe-stacks --stack-name WEBAPP-${STACK_ENV} \
                                --query Stacks[0].StackStatus --output text --profile ${PROFILE}", returnStdout: true)
                            apply = true
                    } catch (err) {
                            apply = false
                            sh 'echo Creating WEBAPP-${STACK_ENV}....'
                            sh "aws cloudformation validate-template --template-body file://`pwd`/cloudformation.yml --profile ${PROFILE}"
                            sh "aws cloudformation create-stack --stack-name WEBAPP-${STACK_ENV} --template-body \
                                            file://`pwd`/cloudformation.yml --parameters file://`pwd`/${ENV}.json --profile ${PROFILE}"
                            sh "aws cloudformation wait stack-create-complete --stack-name WEBAPP-${STACK_ENV} --profile ${PROFILE}"
                            sh "aws cloudformation describe-stack-events --stack-name WEBAPP-${STACK_ENV} \
                                            --query 'StackEvents[].[{Resource:LogicalResourceId,Status:ResourceStatus,Reason:ResourceStatusReason}]' \
                                            --output table --profile ${PROFILE}"
                    }
                    if (apply) {
                            try {
                                    sh 'echo Stack exists, attempting update...'
                                    sh "aws cloudformation update-stack --stack-name \
                                            WEBAPP-${STACK_ENV} --template-body file://`pwd`/cloudformation.yml \
                                            --parameters file://`pwd`/${ENV}.json --profile ${PROFILE}"
                            } catch (error) {
                                    sh 'echo Finished create/update - no updates to be performed'
                            }
                    }
                    sh 'echo Finished create/update successfully!'
                }
      }
        }
