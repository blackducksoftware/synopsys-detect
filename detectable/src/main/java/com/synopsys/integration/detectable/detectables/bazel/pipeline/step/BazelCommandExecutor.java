/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.bazel.pipeline.step;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class BazelCommandExecutor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectableExecutableRunner executableRunner;
    private final File workspaceDir;
    private final File bazelExe;

    public BazelCommandExecutor(DetectableExecutableRunner executableRunner, File workspaceDir, File bazelExe) {
        this.executableRunner = executableRunner;
        this.workspaceDir = workspaceDir;
        this.bazelExe = bazelExe;
    }

    public Optional<String> executeToString(List<String> args) throws IntegrationException {
        ExecutableOutput executableOutput = execute(args);
        String cmdStdErr = executableOutput.getErrorOutput();
        if (cmdStdErr != null && cmdStdErr.contains("ERROR")) {
            logger.warn(String.format("Bazel error: %s", cmdStdErr.trim()));
        }
        String cmdStdOut = executableOutput.getStandardOutput();
        if ((StringUtils.isBlank(cmdStdOut))) {
            logger.debug("bazel command produced no output");
            return Optional.empty();
        }
        return Optional.of(cmdStdOut);
    }

    @NotNull
    private ExecutableOutput execute(List<String> args) throws IntegrationException {
        logger.debug(String.format("Executing bazel with args: %s", args));
        ExecutableOutput targetDependenciesQueryResults;
        try {
            targetDependenciesQueryResults = executableRunner.execute(workspaceDir, bazelExe, args);
        } catch (ExecutableRunnerException e) {
            String msg = String.format("Error executing %s with args: %s", bazelExe, args);
            logger.debug(msg);
            throw new IntegrationException(msg, e);
        }
        int targetDependenciesQueryReturnCode = targetDependenciesQueryResults.getReturnCode();
        if (targetDependenciesQueryReturnCode != 0) {
            String msg = String.format("Error executing bazel with args: %s: Return code: %d; stderr: %s", args,
                targetDependenciesQueryReturnCode,
                targetDependenciesQueryResults.getErrorOutput());
            logger.debug(msg);
            throw new IntegrationException(msg);
        }
        logger.debug(String.format("bazel command return code: %d", targetDependenciesQueryReturnCode));
        return targetDependenciesQueryResults;
    }
}
