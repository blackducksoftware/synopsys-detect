/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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