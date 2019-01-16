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
package com.blackducksoftware.integration.hub.detect.detector.bitbake;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.detector.Detector;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.detector.DetectorException;
import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.detector.ExtractionId;
import com.blackducksoftware.integration.hub.detect.util.executable.CacheableExecutableFinder;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.ExecutableNotFoundDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.FileNotFoundDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PassedDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PropertyInsufficientDetectorResult;

public class BitbakeDetector extends Detector {
    private final BitbakeDetectorOptions bitbakeDetectorOptions;
    private final DetectFileFinder detectFileFinder;
    private final BitbakeExtractor bitbakeExtractor;
    private final CacheableExecutableFinder cacheableExecutableFinder;

    private File foundBuildEnvScript;
    private File bashExe;

    public BitbakeDetector(final DetectorEnvironment detectorEnvironment, final DetectFileFinder detectFileFinder, final BitbakeDetectorOptions bitbakeDetectorOptions, final BitbakeExtractor bitbakeExtractor,
        final CacheableExecutableFinder cacheableExecutableFinder) {
        super(detectorEnvironment, "Bitbake", DetectorType.BITBAKE);
        this.detectFileFinder = detectFileFinder;
        this.bitbakeDetectorOptions = bitbakeDetectorOptions;
        this.bitbakeExtractor = bitbakeExtractor;
        this.cacheableExecutableFinder = cacheableExecutableFinder;
    }

    @Override
    public DetectorResult applicable() {
        foundBuildEnvScript = detectFileFinder.findFile(environment.getDirectory(), bitbakeDetectorOptions.getBuildEnvName());
        if (foundBuildEnvScript == null) {
            return new FileNotFoundDetectorResult(DetectProperty.DETECT_BITBAKE_BUILD_ENV_NAME.getDefaultValue());
        }

        if (bitbakeDetectorOptions.getPackageNames() == null || bitbakeDetectorOptions.getPackageNames().length == 0) {
            return new PropertyInsufficientDetectorResult();
        }

        return new PassedDetectorResult();
    }

    @Override
    public DetectorResult extractable() throws DetectorException {
        bashExe = cacheableExecutableFinder.getExecutable(CacheableExecutableFinder.CacheableExecutableType.BASH);
        if (bashExe == null) {
            return new ExecutableNotFoundDetectorResult("bash");
        }

        return new PassedDetectorResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return bitbakeExtractor.extract(extractionId, foundBuildEnvScript, environment.getDirectory(), bitbakeDetectorOptions.getPackageNames(), bashExe);
    }
}
