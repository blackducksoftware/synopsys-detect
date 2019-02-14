/**
 * detect-application
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
package com.synopsys.integration.detect.tool.bazel;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.detector.DetectorEnvironment;
import com.synopsys.integration.detect.detector.DetectorException;
import com.synopsys.integration.detect.util.executable.CacheableExecutableFinder;
import com.synopsys.integration.detect.util.executable.ExecutableFinder;
import com.synopsys.integration.detect.util.executable.ExecutableOutput;
import com.synopsys.integration.detect.util.executable.ExecutableRunner;
import com.synopsys.integration.detect.util.executable.ExecutableRunnerException;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;

public class BazelExecutableFinder  extends CacheableExecutableFinder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String BAZEL_VERSION_SUBCOMMAND = "version";
    private final ExecutableRunner executableRunner;

    public BazelExecutableFinder(final ExecutableRunner executableRunner, final DirectoryManager directoryManager, final ExecutableFinder executableFinder, final DetectConfiguration detectConfiguration) {
        super(directoryManager, executableFinder, detectConfiguration);
        this.executableRunner = executableRunner;
    }

    public String findBazel(final DetectorEnvironment environment) {
        final boolean resolvedPreviously = isAlreadyFound(CacheableExecutableType.BAZEL);
        String resolvedBazel = null;
        try {
            final File bazelExeFile = getExecutable(CacheableExecutableType.BAZEL);
            if (bazelExeFile == null) {
                logger.debug("Unable to locate Bazel executable");
                return null;
            }
            resolvedBazel = bazelExeFile.getAbsolutePath();
        } catch (DetectorException e) {
            logger.debug(String.format("Unable to locate Bazel executable: %s", e.getMessage()));
            return null;
        }
        if (!resolvedPreviously) {
            final ExecutableOutput bazelQueryDepsRecursiveOutput;
            try {
                bazelQueryDepsRecursiveOutput = executableRunner.executeQuietly(environment.getDirectory(), resolvedBazel, BAZEL_VERSION_SUBCOMMAND);
                int returnCode = bazelQueryDepsRecursiveOutput.getReturnCode();
                logger.trace(String.format("Bazel version returned %d; output: %s", returnCode, bazelQueryDepsRecursiveOutput.getStandardOutput()));
            } catch (ExecutableRunnerException e) {
                logger.debug(String.format("Bazel version threw exception: %s", e.getMessage()));
                resolvedBazel = null;
            }
        }
        return resolvedBazel;
    }
}
