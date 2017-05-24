package com.blackducksoftware.integration.hub.packman.util.command

import org.apache.commons.lang3.StringUtils

class Command {
    File workingDirectory
    Map<String, String> environmentVariables
    String executablePath
    def executableArguments = []

    Command(File workingDirectory, final String executablePath, final String... executableArguments) {
        this.workingDirectory = workingDirectory
        this.executablePath = executablePath
        this.executableArguments.addAll(executableArguments)
    }

    Command(File workingDirectory, Map<String, String> environmentVariables, final String executablePath, final String... executableArguments) {
        this.workingDirectory = workingDirectory
        this.environmentVariables = environmentVariables
        this.executablePath = executablePath
        this.executableArguments.addAll(executableArguments)
    }

    ProcessBuilder createProcessBuilder() {
        def processBuilderArguments = [executablePath]+ executableArguments
        ProcessBuilder processBuilder = new ProcessBuilder(processBuilderArguments);
        processBuilder.directory(workingDirectory)
        if (environmentVariables) {
            processBuilder.environment().putAll(environmentVariables)
        }

        processBuilder
    }

    String getCommandDescription() {
        StringUtils.join([executablePath]+ executableArguments, ' ')
    }
}
