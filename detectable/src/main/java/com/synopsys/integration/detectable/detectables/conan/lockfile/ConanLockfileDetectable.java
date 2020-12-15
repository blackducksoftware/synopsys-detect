/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.conan.lockfile;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.GivenFileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "C/C++", forge = "conan", requirementsMarkdown = "Files: conan.lock.")
public class ConanLockfileDetectable extends Detectable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String CONANLOCKFILE = "conan.lock";
    private final FileFinder fileFinder;
    private final ConanLockfileExtractor conanLockfileExtractor;
    private final ConanLockfileExtractorOptions conanLockfileExtractorOptions;
    private File lockfile;

    public ConanLockfileDetectable(DetectableEnvironment environment, FileFinder fileFinder, ConanLockfileExtractor conanLockfileExtractor,
        ConanLockfileExtractorOptions conanLockfileExtractorOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.conanLockfileExtractor = conanLockfileExtractor;
        this.conanLockfileExtractorOptions = conanLockfileExtractorOptions;
    }

    @Override
    public DetectableResult applicable() {
        if (conanLockfileExtractorOptions.getLockfilePath().isPresent()) {
            logger.debug("Conan Lockfile detectable applies because user supplied lockfile path {}", conanLockfileExtractorOptions.getLockfilePath().get());
            return new PassedDetectableResult();
        }
        File discoveredLockfile = fileFinder.findFile(environment.getDirectory(), CONANLOCKFILE);
        if (discoveredLockfile == null) {
            return new FileNotFoundDetectableResult(CONANLOCKFILE);
        }
        logger.debug("Conan Lockfile detectable applies because Detect found the default lockfile {}", discoveredLockfile.getAbsolutePath());
        lockfile = discoveredLockfile;
        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        if (conanLockfileExtractorOptions.getLockfilePath().isPresent()) {
            String givenLockfilePath = conanLockfileExtractorOptions.getLockfilePath().get();
            File userProvidedLockfile = new File(givenLockfilePath);
            if (userProvidedLockfile.exists()) {
                lockfile = userProvidedLockfile;
            } else {
                logger.debug("File {} does not exist", givenLockfilePath);
                return new GivenFileNotFoundDetectableResult(givenLockfilePath);
            }
        }
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException {
        return conanLockfileExtractor.extract(lockfile, conanLockfileExtractorOptions);
    }
}
