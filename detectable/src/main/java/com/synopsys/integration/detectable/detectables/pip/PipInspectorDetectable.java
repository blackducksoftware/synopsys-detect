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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.SystemExecutableFinder;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.InspectorNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

public class PipInspectorDetectable extends Detectable {
    public static final String SETUPTOOLS_DEFAULT_FILE_NAME = "setup.py";
    public static final String PYTHON_EXECUTABLE = "python";
    public static final String PIP_EXECUTABLE = "python";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FileFinder fileFinder;
    private final SystemExecutableFinder systemExecutableFinder;
    private final PipInspectorManager pipInspectorManager;
    private final PipInspectorExtractor pipInspectorExtractor;
    private final String requirementFilePath;

    private File pythonExe;
    private File pipExe;
    private File pipInspector;
    private File setupFile;

    public PipInspectorDetectable(final DetectableEnvironment environment, final String requirementFilePath, final FileFinder fileFinder, final SystemExecutableFinder systemExecutableFinder, final PipInspectorManager pipInspectorManager,
        final PipInspectorExtractor pipInspectorExtractor) {
        super(environment, "Pip Inspector", "PIP");
        this.fileFinder = fileFinder;
        this.pipInspectorExtractor = pipInspectorExtractor;
        this.systemExecutableFinder = systemExecutableFinder;
        this.pipInspectorManager = pipInspectorManager;
        this.requirementFilePath = requirementFilePath;
    }

    @Override
    public DetectableResult applicable() {
        setupFile = fileFinder.findFile(environment.getDirectory(), SETUPTOOLS_DEFAULT_FILE_NAME);
        final boolean hasSetups = setupFile != null;
        final boolean hasRequirements = StringUtils.isNotBlank(requirementFilePath);
        if (hasSetups || hasRequirements) {
            logger.warn("------------------------------------------------------------------------------------------------------");
            logger.warn("The Pip inspector has been deprecated. Please use pipenv and the Pipenv Graph inspector in the future.");
            logger.warn("------------------------------------------------------------------------------------------------------");
            return new PassedDetectableResult();
        } else {
            return new FileNotFoundDetectableResult(SETUPTOOLS_DEFAULT_FILE_NAME);
        }

    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        pythonExe = systemExecutableFinder.findExecutable(PYTHON_EXECUTABLE);
        if (pythonExe == null) {
            return new ExecutableNotFoundDetectableResult(PYTHON_EXECUTABLE);
        }

        pipExe = systemExecutableFinder.findExecutable(PIP_EXECUTABLE);
        if (pipExe == null) {
            return new ExecutableNotFoundDetectableResult(PIP_EXECUTABLE);
        }

        pipInspector = pipInspectorManager.findPipInspector(environment);

        if (pipInspector == null) {
            return new InspectorNotFoundDetectableResult(PipInspectorManager.INSPECTOR_NAME);
        }

        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return pipInspectorExtractor.extract(environment.getDirectory(), pythonExe, pipInspector, setupFile, requirementFilePath);
    }

}
