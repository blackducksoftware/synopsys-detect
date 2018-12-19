/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.detector.bazel;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.detector.ExtractionId;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;

public class BazelExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExecutableRunner executableRunner;

    public BazelExtractor(final ExecutableRunner executableRunner) {
        this.executableRunner = executableRunner;
    }

    public Extraction extract(final File workspaceDir, final int depth, final ExtractionId extractionId) {
        logger.info("*********** Bazel extractable()");
        // TODO Should write and use BazelExecutableFinder like Gradle and MavenExecutableFinder
        final ExecutableOutput bazelQueryDepsRecursiveOutput;
        try {
            bazelQueryDepsRecursiveOutput = executableRunner.executeQuietly(workspaceDir, BazelDetector.BAZEL_COMMAND, BazelDetector.BAZEL_QUERY_SUBCOMMAND,
                BazelDetector.BAZEL_QUERY_SPEC_GET_EXTERNAL_DEPENDENCIES, BazelDetector.BAZEL_QUERY_OUTPUT_TYPE_SELECTOR, BazelDetector.BAZEL_QUERY_OUTPUT_TYPE_XML);
            int returnCode = bazelQueryDepsRecursiveOutput.getReturnCode();
            logger.info(String.format("Bazel query returned %d; output: %s", returnCode, bazelQueryDepsRecursiveOutput.getStandardOutput()));
        } catch (ExecutableRunnerException e) {
            logger.info(String.format("Bazel query threw exception: %s", e.getMessage()));
        }
        return new Extraction.Builder().success().build();
    }
}
