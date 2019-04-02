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
import com.synopsys.integration.detectable.detectable.executable.resolver.PipResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PythonResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.inspector.PipInspectorResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.InspectorNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

public class PipInspectorDetectable extends Detectable {
    public static final String SETUPTOOLS_DEFAULT_FILE_NAME = "setup.py";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FileFinder fileFinder;
    private final PythonResolver pythonResolver;
    private final PipResolver pipResolver;
    private final PipInspectorResolver pipInspectorResolver;
    private final PipInspectorExtractor pipInspectorExtractor;
    private final PipInspectorDetectableOptions pipInspectorDetectableOptions;

    private File pythonExe;
    private File pipInspector;
    private File setupFile;

    public PipInspectorDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final PythonResolver pythonResolver, final PipResolver pipResolver,
        final PipInspectorResolver pipInspectorResolver, final PipInspectorExtractor pipInspectorExtractor, final PipInspectorDetectableOptions pipInspectorDetectableOptions) {
        super(environment, "Pip Inspector", "PIP");
        this.fileFinder = fileFinder;
        this.pythonResolver = pythonResolver;
        this.pipResolver = pipResolver;
        this.pipInspectorResolver = pipInspectorResolver;
        this.pipInspectorExtractor = pipInspectorExtractor;
        this.pipInspectorDetectableOptions = pipInspectorDetectableOptions;
    }

    @Override
    public DetectableResult applicable() {
        setupFile = fileFinder.findFile(environment.getDirectory(), SETUPTOOLS_DEFAULT_FILE_NAME);
        final boolean hasSetups = setupFile != null;
        final boolean hasRequirements = StringUtils.isNotBlank(pipInspectorDetectableOptions.getRequirementsFilePath());
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
        pythonExe = pythonResolver.resolvePython();
        if (pythonExe == null) {
            return new ExecutableNotFoundDetectableResult("python");
        }

        final File pipExe = pipResolver.resolvePip();
        if (pipExe == null) {
            return new ExecutableNotFoundDetectableResult("pip");
        }

        pipInspector = pipInspectorResolver.resolvePipInspector();

        if (pipInspector == null) {
            return new InspectorNotFoundDetectableResult("pip-inspector.py");
        }

        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return pipInspectorExtractor.extract(environment.getDirectory(), pythonExe, pipInspector, setupFile, pipInspectorDetectableOptions.getRequirementsFilePath(), pipInspectorDetectableOptions.getPipProjectName());
    }
}
