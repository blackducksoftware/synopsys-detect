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
package com.blackducksoftware.integration.hub.detect.detector.sbt;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.detector.Detector;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.detector.ExtractionId;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.FileNotFoundDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PassedDetectorResult;

public class SbtResolutionCacheDetector extends Detector {
    public static final String BUILD_SBT_FILENAME = "build.sbt";

    private final DetectFileFinder fileFinder;
    private final SbtResolutionCacheExtractor sbtResolutionCacheExtractor;

    public SbtResolutionCacheDetector(final DetectorEnvironment environment, final DetectFileFinder fileFinder, final SbtResolutionCacheExtractor sbtResolutionCacheExtractor) {
        super(environment, "Build SBT", DetectorType.SBT);
        this.fileFinder = fileFinder;
        this.sbtResolutionCacheExtractor = sbtResolutionCacheExtractor;
    }

    @Override
    public DetectorResult applicable() {
        final File build = fileFinder.findFile(environment.getDirectory(), BUILD_SBT_FILENAME);
        if (build == null) {
            return new FileNotFoundDetectorResult(BUILD_SBT_FILENAME);
        }

        return new PassedDetectorResult();
    }

    @Override
    public DetectorResult extractable() {
        return new PassedDetectorResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return sbtResolutionCacheExtractor.extract(environment.getDirectory());
    }

}
