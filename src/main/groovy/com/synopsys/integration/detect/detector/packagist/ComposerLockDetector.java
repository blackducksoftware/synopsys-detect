/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect.detector.packagist;

import java.io.File;

import com.synopsys.integration.detect.detector.Detector;
import com.synopsys.integration.detect.detector.DetectorEnvironment;
import com.synopsys.integration.detect.detector.DetectorType;
import com.synopsys.integration.detect.detector.ExtractionId;
import com.synopsys.integration.detect.workflow.extraction.Extraction;
import com.synopsys.integration.detect.workflow.file.DetectFileFinder;
import com.synopsys.integration.detect.workflow.search.result.DetectorResult;
import com.synopsys.integration.detect.workflow.search.result.FileNotFoundDetectorResult;
import com.synopsys.integration.detect.workflow.search.result.PassedDetectorResult;

public class ComposerLockDetector extends Detector {
    public static final String COMPOSER_LOCK = "composer.lock";
    public static final String COMPOSER_JSON = "composer.json";

    private final DetectFileFinder fileFinder;
    private final ComposerLockExtractor composerLockExtractor;

    File composerLock;
    File composerJson;

    public ComposerLockDetector(final DetectorEnvironment environment, final DetectFileFinder fileFinder, final ComposerLockExtractor composerLockExtractor) {
        super(environment, "Composer Lock", DetectorType.PACKAGIST);
        this.fileFinder = fileFinder;
        this.composerLockExtractor = composerLockExtractor;
    }

    @Override
    public DetectorResult applicable() {
        composerLock = fileFinder.findFile(environment.getDirectory(), COMPOSER_LOCK);
        if (composerLock == null) {
            return new FileNotFoundDetectorResult(COMPOSER_LOCK);
        }

        composerJson = fileFinder.findFile(environment.getDirectory(), COMPOSER_JSON);
        if (composerJson == null) {
            return new FileNotFoundDetectorResult(COMPOSER_JSON);
        }

        return new PassedDetectorResult();
    }

    @Override
    public DetectorResult extractable() {
        return new PassedDetectorResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return composerLockExtractor.extract(environment.getDirectory(), composerJson, composerLock);
    }

}
