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
package com.synopsys.integration.detectable.detectables.pip.poetry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PoetryRunPoetryInstallDetectResult;

public class PoetryDetectable extends Detectable {
    private static final String PYPROJECT_TOML_FILE_NAME = "pyproject.toml";
    private static final String POETRY_LOCK = "poetry.lock";

    private final FileFinder fileFinder;
    private final PoetryExtractor poetryExtractor;

    private File pyprojectToml;
    private File poetryLock;

    public PoetryDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final PoetryExtractor poetryExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.poetryExtractor = poetryExtractor;
    }

    @Override
    public DetectableResult applicable() {
        poetryLock = fileFinder.findFile(environment.getDirectory(), POETRY_LOCK);
        if (poetryLock == null) {
            pyprojectToml = fileFinder.findFile(environment.getDirectory(), PYPROJECT_TOML_FILE_NAME);
            if (pyprojectToml == null) {
                return new FileNotFoundDetectableResult(PYPROJECT_TOML_FILE_NAME);
            }
        }
        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() {
        if (poetryLock == null && pyprojectToml != null) {
            return new PoetryRunPoetryInstallDetectResult(environment.getDirectory().getAbsolutePath());
        }
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        final Path path = Paths.get(poetryLock.getAbsolutePath());
        try {
            return poetryExtractor.extract(path);
        } catch (IOException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
