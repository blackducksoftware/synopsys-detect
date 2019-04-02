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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.detector.Detector;
import com.synopsys.integration.detect.detector.DetectorEnvironment;
import com.synopsys.integration.detect.detector.DetectorException;
import com.synopsys.integration.detect.detector.DetectorType;
import com.synopsys.integration.detect.detector.ExtractionId;
import com.synopsys.integration.detect.workflow.extraction.Extraction;
import com.synopsys.integration.detect.workflow.file.DetectFileFinder;
import com.synopsys.integration.detect.workflow.search.result.DetectorResult;
import com.synopsys.integration.detect.workflow.search.result.ExecutableNotFoundDetectorResult;
import com.synopsys.integration.detect.workflow.search.result.FileNotFoundDetectorResult;
import com.synopsys.integration.detect.workflow.search.result.InspectorNotFoundDetectorResult;
import com.synopsys.integration.detect.workflow.search.result.PassedDetectorResult;

public class PipInspectorDetector extends Detector {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String SETUPTOOLS_DEFAULT_FILE_NAME = "setup.py";

    private final DetectFileFinder fileFinder;
    private final PythonExecutableFinder pythonExecutableFinder;
    private final PipInspectorManager pipInspectorManager;
    private final PipInspectorExtractor pipInspectorExtractor;
    private final String requirementFilePath;

    private String pythonExe;
    private File pipInspector;
    private File setupFile;

    public PipInspectorDetector(final DetectorEnvironment environment, final String requirementFilePath, final DetectFileFinder fileFinder, final PythonExecutableFinder pythonExecutableFinder, final PipInspectorManager pipInspectorManager,
        final PipInspectorExtractor pipInspectorExtractor) {
        super(environment, "Pip Inspector", DetectorType.PIP);
        this.fileFinder = fileFinder;
        this.pipInspectorExtractor = pipInspectorExtractor;
        this.pythonExecutableFinder = pythonExecutableFinder;
        this.pipInspectorManager = pipInspectorManager;
        this.requirementFilePath = requirementFilePath;
    }

    @Override
    public DetectorResult applicable() {
        setupFile = fileFinder.findFile(environment.getDirectory(), SETUPTOOLS_DEFAULT_FILE_NAME);
        final boolean hasSetups = setupFile != null;
        final boolean hasRequirements = requirementFilePath != null && StringUtils.isNotBlank(requirementFilePath);
        if (hasSetups || hasRequirements) {
            logger.warn("------------------------------------------------------------------------------------------------------");
            logger.warn("The Pip inspector has been deprecated. Please use pipenv and the Pipenv Graph inspector in the future.");
            logger.warn("------------------------------------------------------------------------------------------------------");
            return new PassedDetectorResult();
        } else {
            return new FileNotFoundDetectorResult(SETUPTOOLS_DEFAULT_FILE_NAME);
        }

    }

    @Override
    public DetectorResult extractable() throws DetectorException {
        pythonExe = pythonExecutableFinder.findPython(environment);
        if (pythonExe == null) {
            return new ExecutableNotFoundDetectorResult("python");
        }

        final String pipExe = pythonExecutableFinder.findPip(environment);
        if (pipExe == null) {
            return new ExecutableNotFoundDetectorResult("pip");
        }

        pipInspector = pipInspectorManager.findPipInspector(environment);
        if (pipInspector == null) {
            return new InspectorNotFoundDetectorResult("pip");
        }

        return new PassedDetectorResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return pipInspectorExtractor.extract(environment.getDirectory(), pythonExe, pipInspector, setupFile, requirementFilePath);
    }

}
