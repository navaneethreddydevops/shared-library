package com.utils

//imports

import com.cloudbees.groovy.cps.NonCPS
import groovy.json.JsonOutput
import hudson.FilePath
import jenkins.model.Jenkins

def call() {
    Map pipelineConfig = readYaml(file: "${WORKSPACE}/pipeline.yaml")
    return pipelineConfig
}
