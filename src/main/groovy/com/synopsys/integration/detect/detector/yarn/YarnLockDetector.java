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
package com.synopsys.integration.detect.detector.yarn;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detect.detector.Detector;
import com.synopsys.integration.detect.detector.DetectorEnvironment;
import com.synopsys.integration.detect.detector.DetectorException;
import com.synopsys.integration.detect.detector.DetectorType;
import com.synopsys.integration.detect.detector.ExtractionId;
import com.synopsys.integration.detect.util.executable.CacheableExecutableFinder;
import com.synopsys.integration.detect.util.executable.CacheableExecutableFinder.CacheableExecutableType;
import com.synopsys.integration.detect.workflow.extraction.Extraction;
import com.synopsys.integration.detect.workflow.file.DetectFileFinder;
import com.synopsys.integration.detect.workflow.search.result.DetectorResult;
import com.synopsys.integration.detect.workflow.search.result.ExecutableNotFoundDetectorResult;
import com.synopsys.integration.detect.workflow.search.result.FileNotFoundDetectorResult;
import com.synopsys.integration.detect.workflow.search.result.PassedDetectorResult;

public class YarnLockDetector extends Detector {
    private static final String YARN_LOCK_FILENAME = "yarn.lock";

    private final DetectFileFinder fileFinder;
    private final CacheableExecutableFinder cacheableExecutableFinder;
    private final YarnLockExtractor yarnLockExtractor;

    private File yarnlock;
    private String yarnExe = "";

    public YarnLockDetector(final DetectorEnvironment environment, final DetectFileFinder fileFinder, final CacheableExecutableFinder cacheableExecutableFinder, final YarnLockExtractor yarnLockExtractor) {
        super(environment, "Yarn Lock", DetectorType.YARN);
        this.fileFinder = fileFinder;
        this.yarnLockExtractor = yarnLockExtractor;
        this.cacheableExecutableFinder = cacheableExecutableFinder;
    }

    @Override
    public DetectorResult applicable() {
        yarnlock = fileFinder.findFile(environment.getDirectory(), YARN_LOCK_FILENAME);
        if (yarnlock == null) {
            return new FileNotFoundDetectorResult(YARN_LOCK_FILENAME);
        }

        return new PassedDetectorResult();
    }

    @Override
    public DetectorResult extractable() throws DetectorException {
        final File yarn = cacheableExecutableFinder.getExecutable(CacheableExecutableType.YARN);
        if (yarn != null) {
            yarnExe = yarn.toString();
        }

        if (StringUtils.isBlank(yarnExe)) {
            return new ExecutableNotFoundDetectorResult("yarn");
        }

        return new PassedDetectorResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return yarnLockExtractor.extract(environment.getDirectory(), yarnlock, yarnExe);
    }

}
