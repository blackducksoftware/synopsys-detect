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
    public static final String BAZEL_QUERY_SUBCOMMAND = "query";
    // This query will succeed only within a workspace (top level, or nested dir)
    private static final String BAZEL_QUERY_SPEC_WORKSPACE_TEST = "kind(rule, //...:*)";
    public static final String BAZEL_QUERY_SPEC_GET_EXTERNAL_DEPENDENCIES = "kind(.*, //external:*)";
    public static final String BAZEL_QUERY_OUTPUT_TYPE_SELECTOR = "--output";
    public static final String BAZEL_QUERY_OUTPUT_TYPE_XML = "xml";

    private static final String BAZEL_WORKSPACE_FILENAME = "WORKSPACE";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final BazelExtractor bazelExtractor;
    private File workspaceFile;
    private File workspaceDir;
    private final DetectFileFinder fileFinder;
    private final ExecutableRunner executableRunner;
    private final BazelExecutableFinder bazelExecutableFinder;
    private String bazelExe;

    public BazelDetector(final DetectorEnvironment environment, final ExecutableRunner executableRunner, final DetectFileFinder fileFinder, final BazelExtractor bazelExtractor,
        BazelExecutableFinder bazelExecutableFinder) {
        super(environment, "Bazel", DetectorType.BAZEL);
        this.fileFinder = fileFinder;
        this.executableRunner = executableRunner;
        this.bazelExtractor = bazelExtractor;
        this.bazelExecutableFinder = bazelExecutableFinder;
    }

    @Override
    public DetectorResult applicable() {
        workspaceFile = fileFinder.findFile(environment.getDirectory(), BAZEL_WORKSPACE_FILENAME);
        workspaceDir = workspaceFile.getParentFile();
        if (workspaceFile == null) {
            return new FileNotFoundDetectorResult(BAZEL_WORKSPACE_FILENAME);
        }
        return new PassedDetectorResult();
    }

    @Override
    public DetectorResult extractable() throws DetectorException {
        bazelExe = bazelExecutableFinder.findBazel(environment);
        // TODO figure out this try/catch
//        try {
        // TODO: We're only supporting certain languages. OK if workspace contains others? What will happen?
        final ExecutableOutput bazelQueryDepsRecursiveOutput;
        try {
            bazelQueryDepsRecursiveOutput = executableRunner.executeQuietly(workspaceDir, bazelExe, BAZEL_QUERY_SUBCOMMAND, BAZEL_QUERY_SPEC_WORKSPACE_TEST);
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
        return bazelExtractor.extract(bazelExe, environment.getDirectory(), environment.getDepth(), extractionId);
    }
}
