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
import java.io.IOException;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.detector.Detector;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.detector.ExtractionId;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.FileNotFoundDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PassedDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PropertyInsufficientDetectorResult;
import com.synopsys.integration.hub.bdio.model.Forge;

public class BitbakeDetector extends Detector {
    public static Forge YOCTO_FORGE = new Forge("/", "/", "yocto");

    private final DetectFileFinder detectFileFinder;
    private final DetectConfiguration detectConfiguration;
    private final BitbakeExtractor bitbakeExtractor;

    private File foundBuildEnvScript;

    public BitbakeDetector(final DetectorEnvironment detectorEnvironment, final DetectFileFinder detectFileFinder, final DetectConfiguration detectConfiguration, final BitbakeExtractor bitbakeExtractor) {
        super(detectorEnvironment, "Bitbake", DetectorType.BITBAKE);
        this.detectFileFinder = detectFileFinder;
        this.detectConfiguration = detectConfiguration;
        this.bitbakeExtractor = bitbakeExtractor;
    }

    @Override
    public DetectorResult applicable() {
        foundBuildEnvScript = detectFileFinder.findFile(environment.getDirectory(), detectConfiguration.getProperty(DetectProperty.DETECT_INIT_BUILD_ENV_NAME, PropertyAuthority.None));
        if (foundBuildEnvScript == null) {
            return new FileNotFoundDetectorResult(DetectProperty.DETECT_INIT_BUILD_ENV_NAME.getDefaultValue());
        }

        return new PassedDetectorResult();
    }

    @Override
    public DetectorResult extractable() {
        final String[] packageNames = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_BITBAKE_PACKAGE_NAMES, PropertyAuthority.None);
        if (packageNames == null || packageNames.length == 0) {
            return new PropertyInsufficientDetectorResult();
        }

        return new PassedDetectorResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        try {
            return bitbakeExtractor.extract(extractionId, foundBuildEnvScript.getCanonicalPath(), environment.getDirectory().getCanonicalPath());
        } catch (final IOException e) {
            return new Extraction.Builder().failure(String.format("Failed to extract dependencies from bitbake: %s", e.getMessage())).build();
        }
    }
}
