#!/usr/bin/groovy
def call() {
    script {
        def dockerFileStr = ''
        echo ''
    }
    /* groovylint-disable-next-line NglParseError */
    try {
        dockerFileStr = readFile 'Dockerfile'
        echo ''
    }
    catch (Exception e) {
        ansiColor('xterm') {
            error "\\033[1;31m Dockerfile does not exist: \\033[0m${e}"
        }
    }
}
