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
package com.synopsys.integration.detectable.detectables.pip;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.PipResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PythonResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.inspector.PipInspectorResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FilesNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.InspectorNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Python", forge = "Pypi", requirementsMarkdown = "A setup.py file, or one or more requirements.txt files.<br/><br/> Executables: python and pip, or python3 and pip3.")
public class PipInspectorDetectable extends Detectable {
    private static final String SETUPTOOLS_DEFAULT_FILE_NAME = "setup.py";
    private static final String REQUIREMENTS_DEFAULT_FILE_NAME = "requirements.txt";

    private final FileFinder fileFinder;
    private final PythonResolver pythonResolver;
    private final PipResolver pipResolver;
    private final PipInspectorResolver pipInspectorResolver;
    private final PipInspectorExtractor pipInspectorExtractor;
    private final PipInspectorDetectableOptions pipInspectorDetectableOptions;

    private ExecutableTarget pythonExe;
    private File pipInspector;
    private File setupFile;
    private List<Path> requirementsFiles;

    public PipInspectorDetectable(DetectableEnvironment environment, FileFinder fileFinder, PythonResolver pythonResolver, PipResolver pipResolver,
        PipInspectorResolver pipInspectorResolver, PipInspectorExtractor pipInspectorExtractor, PipInspectorDetectableOptions pipInspectorDetectableOptions) {
        super(environment);
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
        boolean hasSetups = setupFile != null;

        requirementsFiles = pipInspectorDetectableOptions.getRequirementsFilePaths();
        if (CollectionUtils.isEmpty(pipInspectorDetectableOptions.getRequirementsFilePaths())) {
            requirementsFiles = fileFinder.findFiles(environment.getDirectory(), REQUIREMENTS_DEFAULT_FILE_NAME)
                                    .stream()
                                    .map(File::toPath)
                                    .collect(Collectors.toList());
        }
        boolean hasRequirements = CollectionUtils.isNotEmpty(requirementsFiles);

        if (hasSetups || hasRequirements) {
            return new PassedDetectableResult();
        } else {
            return new FilesNotFoundDetectableResult(SETUPTOOLS_DEFAULT_FILE_NAME, REQUIREMENTS_DEFAULT_FILE_NAME);
        }
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        pythonExe = pythonResolver.resolvePython();
        if (pythonExe == null) {
            return new ExecutableNotFoundDetectableResult("python");
        }

        ExecutableTarget pipExe = pipResolver.resolvePip();
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
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        //TODO: Handle null better.
        return pipInspectorExtractor.extract(environment.getDirectory(), pythonExe, pipInspector, setupFile, requirementsFiles, pipInspectorDetectableOptions.getPipProjectName().orElse(""));
    }
}
