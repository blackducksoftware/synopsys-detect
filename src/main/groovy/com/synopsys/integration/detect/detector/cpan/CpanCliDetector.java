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
package com.synopsys.integration.detect.detector.cpan;

import java.io.File;

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

public class CpanCliDetector extends Detector {
    public static final String MAKEFILE = "Makefile.PL";

    private final DetectFileFinder fileFinder;
    private final CacheableExecutableFinder cacheableExecutableFinder;
    private final CpanCliExtractor cpanCliExtractor;

    private File cpanExe;
    private File cpanmExe;

    public CpanCliDetector(final DetectorEnvironment environment, final DetectFileFinder fileFinder, final CacheableExecutableFinder cacheableExecutableFinder, final CpanCliExtractor cpanCliExtractor) {
        super(environment, "Cpan Cli", DetectorType.CPAN);
        this.fileFinder = fileFinder;
        this.cpanCliExtractor = cpanCliExtractor;
        this.cacheableExecutableFinder = cacheableExecutableFinder;
    }

    @Override
    public DetectorResult applicable() {
        final File makeFile = fileFinder.findFile(environment.getDirectory(), MAKEFILE);
        if (makeFile == null) {
            return new FileNotFoundDetectorResult(MAKEFILE);
        }

        return new PassedDetectorResult();
    }

    @Override
    public DetectorResult extractable() throws DetectorException {
        final File cpan = cacheableExecutableFinder.getExecutable(CacheableExecutableType.CPAN);

        if (cpan == null) {
            return new ExecutableNotFoundDetectorResult("cpan");
        } else {
            cpanExe = cpan;
        }

        final File cpanm = cacheableExecutableFinder.getExecutable(CacheableExecutableType.CPANM);

        if (cpanm == null) {
            return new ExecutableNotFoundDetectorResult("cpanm");
        } else {
            cpanmExe = cpanm;
        }

        return new PassedDetectorResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return cpanCliExtractor.extract(environment.getDirectory(), cpanExe, cpanmExe, extractionId);
    }

}
