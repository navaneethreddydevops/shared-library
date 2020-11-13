def call() {
  def podLabel = "slave-${UUID.randomUUID().toString()}"
  pipeline {
        agent {
      kubernetes {
        yaml '''
apiVersion: v1
kind: Pod
metadata:
  lables:
    some-labels: ${podLabel}
spec:
  securityContext:
    runAsUser: 1000
    runAsGroup: 1000
    fsGroup: 1000
  containers:
# This Docker Image is for maven build Types
  - name: maven
    image: maven
    command:
    -cat
    tty: true
    volumeMounts:
    - mountPath: /root/.m2
    name: m2-repo
# This Docker Image is for jhipster
  - name: docker-node
    image: jhipster/jhipster
    command:
    - cat
    tty: true
# This Docker Image is for aws cli Types
  - name: aws
    image: amazon/aws-cli
    securityContext: 0
      runAsUser: 0
      runAsGroup: 0
      fsGroup: 0
    command:
    - cat
    tty: true
    volumeMounts:
    - mountPath: /var/run/docker.sock
      name: docker-sock
# This Docker Image is for docker image building
  - name: docker
    image: docker:latest
    command:
    - cat
    tty: true
    volumeMounts:
    - mountPath: /var/run/docker.sock
    name: docker-sock
  Volumes:
    - name: docker-sock
      hostPath:
        path: /var/run/docker.sock
    - name: m2-repo
      hostPath:
        path: /root/.m2
'''
      }
        }
  }
  options {
        buildDiscarder(logRotator(numToKeepStr: '5', artifactNumToKeepStr: '5'))
        disableConcurrentBuilds(logRotator(numToKeepStr: ''))
  }
  triggers {
        pollSCM('H/1 * * * * ')
  }
  parameters {
        choices (name: 'Environment', choices:'', description:'Deployment Environment')
        choice(name:'awsRegion', choices:['us-east-1', 'us-west-1'], description:'AWS Region')
  }
  stages {
        stage('Git Checkout') {
      container('jnlp') {
        script {
          echo 'Running inside docker'
        }
      }
        }
        stage('Build') {
      container('aws') {
        script {
          echo 'Running inside docker'
        }
      }
        }
  }
}
