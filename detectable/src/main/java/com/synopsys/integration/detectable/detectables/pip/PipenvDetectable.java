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
package com.synopsys.integration.detectable.detectables.pip;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FilesNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

public class PipenvDetectable extends Detectable {
    public static final String SETUPTOOLS_DEFAULT_FILE_NAME = "setup.py";
    public static final String PIPFILE_FILE_NAME = "Pipfile";
    public static final String PIPFILE_DOT_LOCK_FILE_NAME = "Pipfile.lock";

    private final FileFinder fileFinder;
    private final PythonExecutableFinder pythonExecutableFinder;
    private final PipenvExtractor pipenvExtractor;

    private String pythonExe;
    private String pipenvExe;
    private File pipfileDotLock;
    private File pipfile;
    private File setupFile;

    public PipenvDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final PythonExecutableFinder pythonExecutableFinder, final PipenvExtractor pipenvExtractor) {
        super(environment, "Pipenv Graph", "PIP");
        this.fileFinder = fileFinder;
        this.pipenvExtractor = pipenvExtractor;
        this.pythonExecutableFinder = pythonExecutableFinder;
    }

    @Override
    public DetectableResult applicable() {
        pipfile = fileFinder.findFile(environment.getDirectory(), PIPFILE_FILE_NAME);
        pipfileDotLock = fileFinder.findFile(environment.getDirectory(), PIPFILE_DOT_LOCK_FILE_NAME);

        if (pipfile != null || pipfileDotLock != null) {
            return new PassedDetectableResult();
        } else {
            return new FilesNotFoundDetectableResult(PIPFILE_FILE_NAME, PIPFILE_DOT_LOCK_FILE_NAME);
        }

    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        pythonExe = pythonExecutableFinder.findPython(environment);
        if (pythonExe == null) {
            return new ExecutableNotFoundDetectableResult("python");
        }

        pipenvExe = pythonExecutableFinder.findPipenv(environment);
        if (pipenvExe == null) {
            return new ExecutableNotFoundDetectableResult("pipenv");
        }

        setupFile = fileFinder.findFile(environment.getDirectory(), SETUPTOOLS_DEFAULT_FILE_NAME);

        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return pipenvExtractor.extract(environment.getDirectory(), pythonExe, pipenvExe, setupFile);
    }

}
