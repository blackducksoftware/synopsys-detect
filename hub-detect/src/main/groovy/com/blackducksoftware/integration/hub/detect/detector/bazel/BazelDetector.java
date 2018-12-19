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

import com.blackducksoftware.integration.hub.detect.detector.Detector;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.detector.DetectorException;
import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.detector.ExtractionId;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.FileNotFoundDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PassedDetectorResult;

public class BazelDetector extends Detector {
    private static final String BAZEL_WORKSPACE_FILENAME = "WORKSPACE";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final BazelExtractor bazelExtractor;
    private File workspaceFile;
    private final DetectFileFinder fileFinder;
    private final ExecutableRunner executableRunner;

    public BazelDetector(final DetectorEnvironment environment, final ExecutableRunner executableRunner, final DetectFileFinder fileFinder, final BazelExtractor bazelExtractor) {
        super(environment, "Clang", DetectorType.BAZEL);
        this.fileFinder = fileFinder;
        this.executableRunner = executableRunner;
        this.bazelExtractor = bazelExtractor;
    }

    @Override
    public DetectorResult applicable() {
        logger.info("*********** Bazel applicable()");
        workspaceFile = fileFinder.findFile(environment.getDirectory(), BAZEL_WORKSPACE_FILENAME);
        if (workspaceFile == null) {
            return new FileNotFoundDetectorResult(BAZEL_WORKSPACE_FILENAME);
        }
        return new PassedDetectorResult();
    }

    @Override
    public DetectorResult extractable() throws DetectorException {
        logger.info("*********** Bazel extractable()");
//        try {
            // TODO run a bazel command to see if we are in a valid workspace
            // bazel query 'kind(rule, //...:*)'
            // will succeed only inside a workspace.
        // Will succeed at top of workspace, and in a workspace subdir, so
        // make sure there is a WORKSPACE file in the dir
        // If there is a WORKSPACE file in this dir AND bazel query 'kind(rule, //...:*)' succeeds (status: 0): It's a WORKSPACE root dir
        // We're only supporting certain languages. OK if workspace contains others? What will happen?
        final ExecutableOutput bazelQueryDepsRecursiveOutput;
        try {
            bazelQueryDepsRecursiveOutput = executableRunner.executeQuietly(workspaceFile.getParentFile(), "bazel", "query", "kind(rule, //...:*)");
            int returnCode = bazelQueryDepsRecursiveOutput.getReturnCode();
            logger.info(String.format("Bazel query returned %d; output: %s", returnCode, bazelQueryDepsRecursiveOutput.getStandardOutput()));
        } catch (ExecutableRunnerException e) {
            logger.info(String.format("Bazel query threw exception: %s", e.getMessage()));
        }

//        } catch (final IntegrationException e) {
//            return new ExecutableNotFoundDetectorResult("bazel");
//        }
        return new PassedDetectorResult();
    }


    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return bazelExtractor.extract(environment.getDirectory(), environment.getDepth(), extractionId);
    }
}
