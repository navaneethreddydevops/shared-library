package com.util

public class Python {

    private static final String SOURCE_DIR = 'src'
    private static final String VERSION_FILE = '_version'

    public static String Python(steps, String cmd, String opts ='', boolean returnStdout = false) {
        return steps.sh(
            script = "python3.8 ${cmd} ${opts}",
            returnStdout = returnStdout
        )
    }

    public static String Python(steps, String cmd, String opts ='', boolean returnStdout = false) {
        return steps.sh(
            script = "pip3.8 ${cmd} ${opts}",
            returnStdout = returnStdout
        )
    }

}
