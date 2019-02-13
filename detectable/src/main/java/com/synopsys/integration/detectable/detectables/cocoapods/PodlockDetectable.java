/**
 * detectable
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
package com.synopsys.integration.detectable.detectables.cocoapods;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

public class PodlockDetectable extends Detectable {
    private static final String PODFILE_LOCK_FILENAME = "Podfile.lock";

    private final FileFinder fileFinder;
    private final PodlockExtractor podlockExtractor;

    private File foundPodlock;

    public PodlockDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final PodlockExtractor podlockExtractor) {
        super(environment, "Podlock", "COCOAPODS");
        this.fileFinder = fileFinder;
        this.podlockExtractor = podlockExtractor;
    }

    @Override
    public DetectableResult applicable() {
        foundPodlock = fileFinder.findFile(environment.getDirectory(), PODFILE_LOCK_FILENAME);
        if (foundPodlock == null) {
            return new FileNotFoundDetectableResult(PODFILE_LOCK_FILENAME);
        }

        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        // addRelevantDiagnosticFile(foundPodlock); // TODO: Jordan FIX ME
        return podlockExtractor.extract(environment.getDirectory(), foundPodlock);
    }

}
