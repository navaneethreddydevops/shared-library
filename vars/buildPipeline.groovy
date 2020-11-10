#!/usr/bin/groovy
def call() {
    Map pipelineConfig = readYaml(file: "${WORKSPACE}/pipeline.yaml")
    return pipelineConfig
}
