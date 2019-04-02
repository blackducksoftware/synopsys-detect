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
package com.synopsys.integration.detect.detector.npm;

import java.io.File;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.detector.Detector;
import com.synopsys.integration.detect.detector.DetectorEnvironment;
import com.synopsys.integration.detect.detector.DetectorType;
import com.synopsys.integration.detect.detector.ExtractionId;
import com.synopsys.integration.detect.workflow.extraction.Extraction;
import com.synopsys.integration.detect.workflow.file.DetectFileFinder;
import com.synopsys.integration.detect.workflow.search.result.DetectorResult;
import com.synopsys.integration.detect.workflow.search.result.FileNotFoundDetectorResult;
import com.synopsys.integration.detect.workflow.search.result.PassedDetectorResult;

public class NpmShrinkwrapDetector extends Detector {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String SHRINKWRAP_JSON = "npm-shrinkwrap.json";
    public static final String PACKAGE_JSON = "package.json";

    private final DetectFileFinder fileFinder;
    private final NpmLockfileExtractor npmLockfileExtractor;

    private File lockfile;
    private Optional<File> packageJson = Optional.empty();

    public NpmShrinkwrapDetector(final DetectorEnvironment environment, final DetectFileFinder fileFinder, final NpmLockfileExtractor npmLockfileExtractor) {
        super(environment, "Shrinkwrap", DetectorType.NPM);
        this.fileFinder = fileFinder;
        this.npmLockfileExtractor = npmLockfileExtractor;
    }

    @Override
    public DetectorResult applicable() {
        lockfile = fileFinder.findFile(environment.getDirectory(), SHRINKWRAP_JSON);
        if (lockfile == null) {
            return new FileNotFoundDetectorResult(SHRINKWRAP_JSON);
        }

        File foundPackageJson = fileFinder.findFile(environment.getDirectory(), PACKAGE_JSON);
        if (foundPackageJson == null) {
            logger.warn("Npm applied but it could not find a package.json so dependencies may not be entirely accurate.");
        } else {
            packageJson = Optional.of(foundPackageJson);
        }

        return new PassedDetectorResult();
    }

    @Override
    public DetectorResult extractable() {
        return new PassedDetectorResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        addRelevantDiagnosticFile(lockfile);
        if (packageJson.isPresent()) {
            addRelevantDiagnosticFile(packageJson.get());
        }
        return npmLockfileExtractor.extract(environment.getDirectory(), lockfile, packageJson);
    }

}
