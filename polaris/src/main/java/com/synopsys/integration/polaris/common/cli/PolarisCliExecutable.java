/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.cli;

import com.synopsys.integration.executable.Executable;

import java.io.File;
import java.util.*;

public class PolarisCliExecutable extends Executable {
    public static final String COVERITY_UNSUPPORTED_KEY = "COVERITY_UNSUPPORTED";
    public static final String SWIP_USER_INPUT_TIMEOUT_MINUTES_KEY = "SWIP_USER_INPUT_TIMEOUT_MINUTES";
    public static final String POLARIS_USER_INPUT_TIMEOUT_MINUTES_KEY = "POLARIS_USER_INPUT_TIMEOUT_MINUTES";

    public static final PolarisCliExecutable createSetup(File polarisCli, File projectDirectory, Map<String, String> environmentVariables) {
        return new PolarisCliExecutable(polarisCli, projectDirectory, environmentVariables, Collections.singletonList("setup"));
    }

    public static final PolarisCliExecutable createAnalyze(File polarisCli, File projectDirectory, Map<String, String> environmentVariables) {
        return new PolarisCliExecutable(polarisCli, projectDirectory, environmentVariables, Arrays.asList("analyze", "-w"));
    }

    private static Map<String, String> completeEnvironmentVariables(Map<String, String> environmentVariables) {
        Map<String, String> completed = new HashMap<>(environmentVariables);

        if (!completed.containsKey(COVERITY_UNSUPPORTED_KEY)) {
            completed.put(COVERITY_UNSUPPORTED_KEY, "1");
        }
        if (!completed.containsKey(SWIP_USER_INPUT_TIMEOUT_MINUTES_KEY)) {
            completed.put(SWIP_USER_INPUT_TIMEOUT_MINUTES_KEY, "1");
        }
        if (!completed.containsKey(POLARIS_USER_INPUT_TIMEOUT_MINUTES_KEY)) {
            completed.put(POLARIS_USER_INPUT_TIMEOUT_MINUTES_KEY, "1");
        }

        return completed;
    }

    private static List<String> createCommandWithArguments (File polarisCli, List<String> arguments) {
        List<String> commandWithArguments = new ArrayList<>();
        commandWithArguments.add(polarisCli.getAbsolutePath());
        commandWithArguments.addAll(arguments);
        return commandWithArguments;
    }

    public PolarisCliExecutable(File polarisCli, File projectDirectory, Map<String, String> environmentVariables, List<String> arguments) {
        super(projectDirectory, completeEnvironmentVariables(environmentVariables), createCommandWithArguments(polarisCli, arguments));
    }

}
