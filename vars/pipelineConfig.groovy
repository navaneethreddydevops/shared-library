//imports

import com.cloudbees.groovy.cps.NonCPS
import groovy.json.JsonOutput
import hudson.FilePath
import jenkins.model.Jenkins

def call() {
    Map pipelineConfig = readYaml(file: "${WORKSPACE}/pipeline.yaml")
    return pipelineConfig
}

// This method id for printing all the plugins
def pulgins() {
    def Plugins =Jenkins.instance.pluginManager.plugins.each {
  plugin ->
        println ("${plugin.getShortName()}: ${plugin.getVersion()}")
        return Plugins
    }
}
