package com.blackducksoftware.integration.hub.packman.packagemanager

import org.springframework.stereotype.Component

@Component
class ExecutableFinder {

    String findExecutable(final String executable) {
        String systemPath = System.getenv("PATH");
        return findExecutable(executable, systemPath)
    }

    String findExecutable(final String executable, String path) {
        for (String pathPiece : path.split(File.pathSeparator)) {
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
