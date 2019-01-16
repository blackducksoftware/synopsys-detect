/**
 * detectable
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.synopsys.integration.detectable.detectable.executable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class Executable {
    private final File workingDirectory;
    private final Map<String, String> environmentVariables = new HashMap<>();
    private final String executablePath;
    private final List<String> executableArguments = new ArrayList<>();

    public Executable(final File workingDirectory, final Map<String, String> environmentVariables, final File executablePath, final List<String> executableArguments) {
        this.workingDirectory = workingDirectory;
        this.environmentVariables.putAll(environmentVariables);
        this.executablePath = executablePath.toString();
        this.executableArguments.addAll(executableArguments);
    }

    public ProcessBuilder createProcessBuilder() {
        final List<String> processBuilderArguments = createProcessBuilderArguments();
        final ProcessBuilder processBuilder = new ProcessBuilder(processBuilderArguments);
        processBuilder.directory(workingDirectory);
        final Map<String, String> processBuilderEnvironment = processBuilder.environment();
        final Map<String, String> systemEnv = System.getenv();
        for (final String key : systemEnv.keySet()) {
            populateEnvironmentMap(processBuilderEnvironment, key, systemEnv.get(key));
        }
        for (final String key : environmentVariables.keySet()) {
            populateEnvironmentMap(processBuilderEnvironment, key, environmentVariables.get(key));
        }
        return processBuilder;
    }

    public String getMaskedExecutableDescription() {
        final List<String> arguments = new ArrayList<>();
        for (final String argument : createProcessBuilderArguments()) {
            if (argument.matches(".*password.*=.*")) {
                final String maskedArgument = argument.substring(0, argument.indexOf('=') + 1) + "********";
                arguments.add(maskedArgument);
            } else {
                arguments.add(argument);
            }
        }
        return StringUtils.join(arguments, ' ');
    }

    public String getExecutableDescription() {
        return StringUtils.join(createProcessBuilderArguments(), ' ');
    }

    private List<String> createProcessBuilderArguments() {
        // ProcessBuilder can only be called with a List<java.lang.String> so do any needed conversion
        final List<String> processBuilderArguments = new ArrayList<>();
        processBuilderArguments.add(executablePath.toString());
        for (final String arg : executableArguments) {
            processBuilderArguments.add(arg.toString());
        }
        return processBuilderArguments;
    }

    private void populateEnvironmentMap(final Map<String, String> environment, final Object key, final Object value) {
        // ProcessBuilder's environment's keys and values must be non-null java.lang.String's
        if (key != null && value != null) {
            final String keyString = key.toString();
            final String valueString = value.toString();
            if (keyString != null && valueString != null) {
                environment.put(keyString, valueString);
            }
        }
    }
}
