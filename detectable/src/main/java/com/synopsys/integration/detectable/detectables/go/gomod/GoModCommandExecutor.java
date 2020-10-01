/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.go.gomod;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;

public class GoModCommandExecutor {
    private final ExecutableRunner executableRunner;

    public GoModCommandExecutor(final ExecutableRunner executableRunner) {
        this.executableRunner = executableRunner;
    }

    List<String> generateGoListOutput(File directory, File goExe) throws ExecutableRunnerException, DetectableException {
        return execute(directory, goExe, "Querying go for the list of modules failed: ", "list", "-m");
    }

    List<String> generateGoListUJsonOutput(File directory, File goExe) throws ExecutableRunnerException, DetectableException {
        List<String> goVersionOutput = execute(directory, goExe, "Querying for the version failed: ", "version");
        Pattern pattern = Pattern.compile("\\d+\\.[\\d.]+");
        Matcher matcher = pattern.matcher(goVersionOutput.get(0));
        if (matcher.find()) {
            String version = matcher.group();
            String[] parts = version.split("\\.");
            if (Integer.parseInt(parts[0]) > 1 || Integer.parseInt(parts[1]) >= 14) {
                return execute(directory, goExe, "Querying for the go mod graph failed:", "list", "-mod=readonly", "-m", "-u", "-json", "all");
            } else {
                return execute(directory, goExe, "Querying for the go mod graph failed:", "list", "-m", "-u", "-json", "all");
            }
        }
        return new ArrayList<>();
    }

    List<String> generateGoModGraphOutput(File directory, File goExe) throws ExecutableRunnerException, DetectableException {
        return execute(directory, goExe, "Querying for the go mod graph failed:", "mod", "graph");
    }

    private List<String> execute(File directory, File goExe, String failureMessage, String... arguments) throws DetectableException, ExecutableRunnerException {
        ExecutableOutput output = executableRunner.execute(directory, goExe, arguments);

        if (output.getReturnCode() == 0) {
            return output.getStandardOutputAsList();
        } else {
            throw new DetectableException(failureMessage + output.getReturnCode());
        }
    }
}
