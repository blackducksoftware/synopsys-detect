/**
 * synopsys-detect
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
package com.synopsys.integration.detect.tool.detector.inspectors.nuget.runtime;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;

public class DotNetRuntimeFinder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ExecutableRunner executableRunner;
    private File workingDir;

    public DotNetRuntimeFinder(ExecutableRunner executableRunner, File workingDir) {
        this.executableRunner = executableRunner;
        this.workingDir = workingDir;
    }

    public List<String> listAvailableRuntimes() throws DetectableException {
        try {
            ExecutableOutput runtimesOutput = executableRunner.execute(workingDir, "dotnet", "--list-runtimes");
            List<String> foundRuntimes = runtimesOutput.getStandardOutputAsList()
                                             .stream()
                                             .map(StringUtils::trimToEmpty)
                                             .filter(StringUtils::isNotBlank)
                                             .collect(Collectors.toList());
            logger.info("Found {} available dotnet runtimes", foundRuntimes.size());
            if (foundRuntimes.isEmpty()) {
                throw new DetectableException("No available dotnet runtimes");
            }
            return foundRuntimes;
        } catch (ExecutableRunnerException e) {
            throw new DetectableException("Could not determine available dotnet runtimes", e);
        }
    }
}
