package com.blackducksoftware.integration.hub.packman.packagemanager

import org.springframework.stereotype.Component

@Component
class ExecutableFinder {
    String findExecutable(final String executable) {
        String systemPath = System.getenv("PATH");
        for (String pathPiece : systemPath.split(File.pathSeparator)) {
            def executableFile = new File(pathPiece).listFiles().find { fileInPath ->
                fileInPath.name == executable && fileInPath.canExecute()
            }
            if (executableFile) {
                return executableFile.absolutePath
            }
        }

        null
    }
}
