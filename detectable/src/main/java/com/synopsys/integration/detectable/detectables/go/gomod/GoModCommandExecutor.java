/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.go.gomod;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class GoModCommandExecutor {
    private static final String FAILURE_MSG_QUERYING_FOR_THE_GO_MOD_GRAPH = "Querying for the go mod graph failed:";
    private static final String FAILURE_MSG_QUERYING_GO_FOR_THE_LIST_OF_MODULES = "Querying go for the list of modules failed: ";
    private static final String FAILURE_MSG_QUERYING_FOR_THE_VERSION = "Querying for the version failed: ";
    private static final Pattern GENERATE_GO_LIST_U_JSON_OUTPUT_PATTERN = Pattern.compile("\\d+\\.[\\d.]+");

    private final DetectableExecutableRunner executableRunner;

    public GoModCommandExecutor(DetectableExecutableRunner executableRunner) {
        this.executableRunner = executableRunner;
    }

    List<String> generateGoListOutput(File directory, ExecutableTarget goExe) throws ExecutableRunnerException, DetectableException {
        return execute(directory, goExe, FAILURE_MSG_QUERYING_GO_FOR_THE_LIST_OF_MODULES, "list", "-m");
    }

    List<String> generateGoListUJsonOutput(File directory, ExecutableTarget goExe) throws ExecutableRunnerException, DetectableException {
        List<String> goVersionOutput = execute(directory, goExe, FAILURE_MSG_QUERYING_FOR_THE_VERSION, "version");
        Matcher matcher = GENERATE_GO_LIST_U_JSON_OUTPUT_PATTERN.matcher(goVersionOutput.get(0));
        if (matcher.find()) {
            String version = matcher.group();
            String[] parts = version.split("\\.");
            if (Integer.parseInt(parts[0]) > 1 || Integer.parseInt(parts[1]) >= 14) {
                return execute(directory, goExe, FAILURE_MSG_QUERYING_FOR_THE_GO_MOD_GRAPH, "list", "-mod=readonly", "-m", "-u", "-json", "all");
            } else {
                return execute(directory, goExe, FAILURE_MSG_QUERYING_FOR_THE_GO_MOD_GRAPH, "list", "-m", "-u", "-json", "all");
            }
        }
        return new ArrayList<>();
    }

    List<String> generateGoModGraphOutput(File directory, ExecutableTarget goExe) throws ExecutableRunnerException, DetectableException {
        return execute(directory, goExe, FAILURE_MSG_QUERYING_FOR_THE_GO_MOD_GRAPH, "mod", "graph");
    }

    List<String> generateGoModWhyOutput(File directory, ExecutableTarget goExe) throws ExecutableRunnerException, DetectableException {
        return execute(directory, goExe, FAILURE_MSG_QUERYING_FOR_THE_GO_MOD_GRAPH, "mod", "why", "-m", "all");
    }

    private List<String> execute(File directory, ExecutableTarget goExe, String failureMessage, String... arguments) throws DetectableException, ExecutableRunnerException {
        ExecutableOutput output = executableRunner.execute(ExecutableUtils.createFromTarget(directory, goExe, arguments));

        if (output.getReturnCode() == 0) {
            return output.getStandardOutputAsList();
        } else {
            throw new DetectableException(failureMessage + output.getReturnCode());
        }
    }
}
