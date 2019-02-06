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
package com.synopsys.integration.detectable.detectables.cran;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

public class PackratLockDetectable extends Detectable {
    public static final String PACKRATLOCK_FILE_NAME = "packrat.lock";

    private final FileFinder fileFinder;
    private final PackratLockExtractor packratLockExtractor;

    private File packratLockFile;

    public PackratLockDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final PackratLockExtractor packratLockExtractor) {
        super(environment, "Packrat Lock", "CRAN");
        this.fileFinder = fileFinder;
        this.packratLockExtractor = packratLockExtractor;
    }

    @Override
    public DetectableResult applicable() {
        packratLockFile = fileFinder.findFile(environment.getDirectory(), PACKRATLOCK_FILE_NAME);

        if (packratLockFile == null) {
            return new FileNotFoundDetectableResult(PACKRATLOCK_FILE_NAME);
        }

        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        // addRelevantDiagnosticFile(packratLockFile); TODO
        return packratLockExtractor.extract(environment.getDirectory(), packratLockFile);
    }

}
