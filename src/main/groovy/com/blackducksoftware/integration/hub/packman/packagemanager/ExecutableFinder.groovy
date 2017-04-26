package com.blackducksoftware.integration.hub.packman.packagemanager

import org.springframework.stereotype.Component

@Component
class ExecutableFinder {

    String findExecutable(final String executable) {
        String command = "which";
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            command = "where";
        }

        def pathToExecutable = "${command} ${executable}".execute().text.trim()
        if (pathToExecutable) {
            return pathToExecutable
        }

        String systemPath = System.getenv("PATH");
        return findExecutable(executable, systemPath)
    }

    String findExecutable(final String executable, final String path) {
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
