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
package com.synopsys.integration.detect.detector.pip;

import java.io.File;

import com.synopsys.integration.detect.detector.Detector;
import com.synopsys.integration.detect.detector.DetectorEnvironment;
import com.synopsys.integration.detect.detector.DetectorException;
import com.synopsys.integration.detect.detector.DetectorType;
import com.synopsys.integration.detect.detector.ExtractionId;
import com.synopsys.integration.detect.workflow.extraction.Extraction;
import com.synopsys.integration.detect.workflow.file.DetectFileFinder;
import com.synopsys.integration.detect.workflow.search.result.DetectorResult;
import com.synopsys.integration.detect.workflow.search.result.ExecutableNotFoundDetectorResult;
import com.synopsys.integration.detect.workflow.search.result.FilesNotFoundDetectorResult;
import com.synopsys.integration.detect.workflow.search.result.PassedDetectorResult;

public class PipenvDetector extends Detector {
    public static final String SETUPTOOLS_DEFAULT_FILE_NAME = "setup.py";
    public static final String PIPFILE_FILE_NAME = "Pipfile";
    public static final String PIPFILE_DOT_LOCK_FILE_NAME = "Pipfile.lock";

    private final DetectFileFinder fileFinder;
    private final PythonExecutableFinder pythonExecutableFinder;
    private final PipenvExtractor pipenvExtractor;

    private String pythonExe;
    private String pipenvExe;
    private File pipfileDotLock;
    private File pipfile;
    private File setupFile;

    public PipenvDetector(final DetectorEnvironment environment, final DetectFileFinder fileFinder, final PythonExecutableFinder pythonExecutableFinder, final PipenvExtractor pipenvExtractor) {
        super(environment, "Pipenv Graph", DetectorType.PIP);
        this.fileFinder = fileFinder;
        this.pipenvExtractor = pipenvExtractor;
        this.pythonExecutableFinder = pythonExecutableFinder;
    }

    @Override
    public DetectorResult applicable() {
        pipfile = fileFinder.findFile(environment.getDirectory(), PIPFILE_FILE_NAME);
        pipfileDotLock = fileFinder.findFile(environment.getDirectory(), PIPFILE_DOT_LOCK_FILE_NAME);

        if (pipfile != null || pipfileDotLock != null) {
            return new PassedDetectorResult();
        } else {
            return new FilesNotFoundDetectorResult(PIPFILE_FILE_NAME, PIPFILE_DOT_LOCK_FILE_NAME);
        }

    }

    @Override
    public DetectorResult extractable() throws DetectorException {
        pythonExe = pythonExecutableFinder.findPython(environment);
        if (pythonExe == null) {
            return new ExecutableNotFoundDetectorResult("python");
        }

        pipenvExe = pythonExecutableFinder.findPipenv(environment);
        if (pipenvExe == null) {
            return new ExecutableNotFoundDetectorResult("pipenv");
        }

        setupFile = fileFinder.findFile(environment.getDirectory(), SETUPTOOLS_DEFAULT_FILE_NAME);

        return new PassedDetectorResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return pipenvExtractor.extract(environment.getDirectory(), pythonExe, pipenvExe, setupFile);
    }

}
