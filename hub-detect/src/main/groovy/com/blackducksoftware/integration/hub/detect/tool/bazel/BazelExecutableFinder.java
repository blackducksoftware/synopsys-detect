/**
 * hub-detect
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
package com.blackducksoftware.integration.hub.detect.tool.bazel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableFinder;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

public class BazelExecutableFinder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String BAZEL_VERSION_SUBCOMMAND = "version";
    private final ExecutableRunner executableRunner;
    private final ExecutableFinder executableFinder;
    private final DetectConfiguration detectConfiguration;

    private boolean hasLookedForSystemBazel = false;
    private String resolvedBazel = null;

    public BazelExecutableFinder(final ExecutableRunner executableRunner, final ExecutableFinder executableFinder, final DetectConfiguration detectConfiguration) {
        this.executableRunner = executableRunner;
        this.executableFinder = executableFinder;
        this.detectConfiguration = detectConfiguration;
    }

    public String findBazel(final DetectorEnvironment environment) {
        if (!hasLookedForSystemBazel) {
            final String userProvidedBazelPath = detectConfiguration.getProperty(DetectProperty.DETECT_BAZEL_PATH, PropertyAuthority.None);
            resolvedBazel = executableFinder.getExecutablePathOrOverride(ExecutableType.BAZEL, true, environment.getDirectory(), userProvidedBazelPath);
                hasLookedForSystemBazel = true;
        }

        final ExecutableOutput bazelQueryDepsRecursiveOutput;
        try {
            bazelQueryDepsRecursiveOutput = executableRunner.executeQuietly(environment.getDirectory(), resolvedBazel, BAZEL_VERSION_SUBCOMMAND);
            int returnCode = bazelQueryDepsRecursiveOutput.getReturnCode();
            logger.trace(String.format("Bazel version returned %d; output: %s", returnCode, bazelQueryDepsRecursiveOutput.getStandardOutput()));
        } catch (ExecutableRunnerException e) {
            logger.debug(String.format("Bazel version threw exception: %s", e.getMessage()));
            resolvedBazel = null;
        }
        return resolvedBazel;
    }
}
