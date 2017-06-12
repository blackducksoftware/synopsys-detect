package com.blackducksoftware.integration.hub.detect.util.executable

import org.apache.commons.lang3.StringUtils

class Executable {
    File workingDirectory
    def environmentVariables = [:]
    String executablePath
    def executableArguments = []

    Executable(File workingDirectory, final String executablePath, List<String> executableArguments) {
        this.workingDirectory = workingDirectory
        this.executablePath = executablePath
        this.executableArguments.addAll(executableArguments)
    }

    Executable(File workingDirectory, Map<String, String> environmentVariables, final String executablePath, List<String> executableArguments) {
        this.workingDirectory = workingDirectory
        this.environmentVariables.putAll(environmentVariables)
        this.executablePath = executablePath
        this.executableArguments.addAll(executableArguments)
    }

    ProcessBuilder createProcessBuilder() {
        def processBuilderArguments = createProcessBuilderArguments()
        ProcessBuilder processBuilder = new ProcessBuilder(processBuilderArguments);
        processBuilder.directory(workingDirectory)
        def processBuilderEnvironment = processBuilder.environment()
        System.getenv().each { key, value ->
            populateEnvironmentMap(processBuilderEnvironment, key, value)
        }
        environmentVariables.each { key, value ->
            populateEnvironmentMap(processBuilderEnvironment, key, value)
        }

        processBuilder
    }

    String getExecutableDescription() {
        StringUtils.join(createProcessBuilderArguments(), ' ')
    }

    private List<String> createProcessBuilderArguments() {
        //ProcessBuilder can only be called with a List<java.lang.String> so do any needed conversion
        List<String> processBuilderArguments = new ArrayList<>()
        processBuilderArguments.add(executablePath.toString())
        executableArguments.each {
            processBuilderArguments.add(it.toString())
        }

        processBuilderArguments
    }

    private void populateEnvironmentMap(Map<String, String> environment, Object key, Object value) {
        //ProcessBuilder's environment's keys and values must be non-null java.lang.String's
        if (key && value) {
            String keyString = key.toString()
            String valueString = value.toString()
            if (keyString && valueString) {
                environment.put(keyString, valueString)
            }
        }
    }
}