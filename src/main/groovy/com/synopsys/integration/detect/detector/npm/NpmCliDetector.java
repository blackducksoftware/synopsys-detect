/**
 * synopsys-detect
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
package com.synopsys.integration.detect.detector.npm;

import java.io.File;

import com.synopsys.integration.detect.detector.Detector;
import com.synopsys.integration.detect.detector.DetectorEnvironment;
import com.synopsys.integration.detect.detector.DetectorException;
import com.synopsys.integration.detect.detector.DetectorType;
import com.synopsys.integration.detect.detector.ExtractionId;
import com.synopsys.integration.detect.workflow.extraction.Extraction;
import com.synopsys.integration.detect.workflow.file.DetectFileFinder;
import com.synopsys.integration.detect.workflow.search.result.DetectorResult;
import com.synopsys.integration.detect.workflow.search.result.ExecutableNotFoundDetectorResult;
import com.synopsys.integration.detect.workflow.search.result.FileNotFoundDetectorResult;
import com.synopsys.integration.detect.workflow.search.result.NpmRunInstallDetectorResult;
import com.synopsys.integration.detect.workflow.search.result.PassedDetectorResult;

public class NpmCliDetector extends Detector {
    public static final String NODE_MODULES = "node_modules";
    public static final String PACKAGE_JSON = "package.json";

    private final DetectFileFinder fileFinder;
    private final NpmExecutableFinder npmExecutableFinder;
    private final NpmCliExtractor npmCliExtractor;

    private String npmExe;

    public NpmCliDetector(final DetectorEnvironment environment, final DetectFileFinder fileFinder, final NpmExecutableFinder npmExecutableFinder, final NpmCliExtractor npmCliExtractor) {
        super(environment, "Npm Cli", DetectorType.NPM);
        this.fileFinder = fileFinder;
        this.npmExecutableFinder = npmExecutableFinder;
        this.npmCliExtractor = npmCliExtractor;
    }

    @Override
    public DetectorResult applicable() {
        final File packageJson = fileFinder.findFile(environment.getDirectory(), PACKAGE_JSON);
        if (packageJson == null) {
            return new FileNotFoundDetectorResult(PACKAGE_JSON);
        }

        addRelevantDiagnosticFile(packageJson);
        return new PassedDetectorResult();
    }

    @Override
    public DetectorResult extractable() throws DetectorException {
        final File nodeModules = fileFinder.findFile(environment.getDirectory(), NODE_MODULES);
        if (nodeModules == null) {
            return new NpmRunInstallDetectorResult(environment.getDirectory().getAbsolutePath());
        }

        npmExe = npmExecutableFinder.findNpm(environment);
        if (npmExe == null) {
            return new ExecutableNotFoundDetectorResult("npm");
        }

        return new PassedDetectorResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return npmCliExtractor.extract(environment.getDirectory(), npmExe, extractionId);
    }

}
