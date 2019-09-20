/**
 * detectable
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
package com.synopsys.integration.detectable.detectables.go.godep;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FilesNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.GoDepRunInitEnsureDetectableResult;
import com.synopsys.integration.detectable.detectable.result.NpmRunInstallDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

public class GoDepLockDetectable extends Detectable {
    public static final String GOPKG_LOCK_FILENAME = "Gopkg.lock";
    public static final String GOFILE_FILENAME_PATTERN = "Gopkg.toml";

    private final FileFinder fileFinder;
    private final GoDepExtractor goDepExtractor;

    private File goLock;
    private File goToml;

    public GoDepLockDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final GoDepExtractor goDepExtractor) {
        super(environment, "Go Lock", "Go Dep");
        this.fileFinder = fileFinder;
        this.goDepExtractor = goDepExtractor;
    }

    @Override
    public DetectableResult applicable() {
        goLock = fileFinder.findFile(environment.getDirectory(), GOPKG_LOCK_FILENAME);
        if (goLock == null) {
            goToml = fileFinder.findFile(environment.getDirectory(), GOFILE_FILENAME_PATTERN);
            if (goToml == null) {
                return new FilesNotFoundDetectableResult(GOPKG_LOCK_FILENAME, GOFILE_FILENAME_PATTERN);
            }
        }

        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() {
        if (goLock == null && goToml != null) {
            return new GoDepRunInitEnsureDetectableResult(environment.getDirectory().getAbsolutePath());
        }
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        try (final InputStream inputStream = new FileInputStream(goLock)) {
            return goDepExtractor.extract(inputStream);
        } catch (final IOException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
