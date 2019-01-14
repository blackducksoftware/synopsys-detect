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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.DetectTool;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.tool.SimpleToolDetector;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.ExecutableNotFoundDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PassedDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PropertyInsufficientDetectorResult;

public class BazelDetector extends SimpleToolDetector {
    private static final String BAZEL_VERSION_SUBCOMMAND = "version";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectorEnvironment environment;
    private final BazelExtractor bazelExtractor;
    private final ExecutableRunner executableRunner;
    private final BazelExecutableFinder bazelExecutableFinder;
    private String bazelExe;
    private final DetectConfiguration detectConfiguration;

    public BazelDetector(final DetectorEnvironment environment, final ExecutableRunner executableRunner, final BazelExtractor bazelExtractor,
        BazelExecutableFinder bazelExecutableFinder, final DetectConfiguration detectConfiguration) {
        super(DetectTool.BAZEL);
        this.environment = environment;
        this.executableRunner = executableRunner;
        this.bazelExtractor = bazelExtractor;
        this.bazelExecutableFinder = bazelExecutableFinder;
        this.detectConfiguration = detectConfiguration;
    }

    @Override
    public DetectorResult applicable() {
        final String bazelTarget = detectConfiguration.getProperty(DetectProperty.DETECT_BAZEL_TARGET, PropertyAuthority.None);
        if (StringUtils.isBlank(bazelTarget)) {
            return new PropertyInsufficientDetectorResult();
        }
        return new PassedDetectorResult();
    }

    @Override
    public DetectorResult extractable() {
        bazelExe = bazelExecutableFinder.findBazel(environment);
        final ExecutableOutput bazelQueryDepsRecursiveOutput;
        try {
            bazelQueryDepsRecursiveOutput = executableRunner.executeQuietly(environment.getDirectory(), bazelExe, BAZEL_VERSION_SUBCOMMAND);
            int returnCode = bazelQueryDepsRecursiveOutput.getReturnCode();
            logger.trace(String.format("Bazel version returned %d; output: %s", returnCode, bazelQueryDepsRecursiveOutput.getStandardOutput()));
        } catch (ExecutableRunnerException e) {
            logger.debug(String.format("Bazel version threw exception: %s", e.getMessage()));
            return new ExecutableNotFoundDetectorResult("bazel");
        }
        return new PassedDetectorResult();
    }

    @Override
    public Extraction extract() {
        Extraction extractResult = bazelExtractor.extract(bazelExe, environment.getDirectory());
        return extractResult;
    }
}
