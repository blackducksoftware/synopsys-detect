/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
import java.util.Optional;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FilesNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PoetryLockfileNotFoundDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Python", forge = "pypi", requirementsMarkdown = "Files: Poetry.lock, pyproject.toml")
public class PoetryDetectable extends Detectable {
    private static final String PYPROJECT_TOML_FILE_NAME = "pyproject.toml";
    private static final String POETRY_LOCK = "poetry.lock";

    private final FileFinder fileFinder;
    private final PoetryExtractor poetryExtractor;

    private File pyprojectToml;
    private File poetryLock;

    public PoetryDetectable(DetectableEnvironment environment, FileFinder fileFinder, PoetryExtractor poetryExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.poetryExtractor = poetryExtractor;
    }

    @Override
    public DetectableResult applicable() {
        poetryLock = fileFinder.findFile(environment.getDirectory(), POETRY_LOCK);
        pyprojectToml = fileFinder.findFile(environment.getDirectory(), PYPROJECT_TOML_FILE_NAME);
        if (poetryLock == null && pyprojectToml == null) {
            return new FilesNotFoundDetectableResult(PYPROJECT_TOML_FILE_NAME, POETRY_LOCK);
        }
        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() {
        if (poetryLock == null && pyprojectToml != null) {
            return new PoetryLockfileNotFoundDetectableResult(environment.getDirectory().getAbsolutePath());
        }
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return poetryExtractor.extract(poetryLock, Optional.ofNullable(pyprojectToml));
    }
}
