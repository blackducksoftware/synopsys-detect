/**
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

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.PassedResultBuilder;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.PipenvResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PythonResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FilesNotFoundDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Python", forge = "PyPi", requirementsMarkdown = "Files: Pipfile or Pipfile.lock.<br/><br/>Executables: python or python3, and pipenv.")
public class PipenvDetectable extends Detectable {
    public static final String SETUPTOOLS_DEFAULT_FILE_NAME = "setup.py";
    public static final String PIPFILE_FILE_NAME = "Pipfile";
    public static final String PIPFILE_DOT_LOCK_FILE_NAME = "Pipfile.lock";

    private final PipenvDetectableOptions pipenvDetectableOptions;
    private final FileFinder fileFinder;
    private final PythonResolver pythonResolver;
    private final PipenvResolver pipenvResolver;
    private final PipenvExtractor pipenvExtractor;

    private File pythonExe;
    private File pipenvExe;
    private File setupFile;

    public PipenvDetectable(final DetectableEnvironment environment, final PipenvDetectableOptions pipenvDetectableOptions, final FileFinder fileFinder, final PythonResolver pythonResolver, final PipenvResolver pipenvResolver,
        final PipenvExtractor pipenvExtractor) {
        super(environment);
        this.pipenvDetectableOptions = pipenvDetectableOptions;
        this.fileFinder = fileFinder;
        this.pipenvResolver = pipenvResolver;
        this.pipenvExtractor = pipenvExtractor;
        this.pythonResolver = pythonResolver;
    }

    @Override
    public DetectableResult applicable() {
        final File pipfile = fileFinder.findFile(environment.getDirectory(), PIPFILE_FILE_NAME);
        final File pipfileDotLock = fileFinder.findFile(environment.getDirectory(), PIPFILE_DOT_LOCK_FILE_NAME);

        if (pipfile != null || pipfileDotLock != null) {
            PassedResultBuilder passedResultBuilder = new PassedResultBuilder();
            passedResultBuilder.foundNullableFile(pipfile);
            passedResultBuilder.foundNullableFile(pipfileDotLock);
            return passedResultBuilder.build();
        } else {
            return new FilesNotFoundDetectableResult(PIPFILE_FILE_NAME, PIPFILE_DOT_LOCK_FILE_NAME);
        }

    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        pythonExe = requirements.executable(pythonResolver::resolvePython, "python");
        pipenvExe = requirements.executable(pipenvResolver::resolvePipenv, "pipenv");

        setupFile = fileFinder.findFile(environment.getDirectory(), SETUPTOOLS_DEFAULT_FILE_NAME);
        requirements.explainNullableFile(setupFile);

        return requirements.result();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        //TODO: Handle null better.
        return pipenvExtractor.extract(environment.getDirectory(), pythonExe, pipenvExe, setupFile, pipenvDetectableOptions.getPipProjectName().orElse(""), pipenvDetectableOptions.getPipProjectVersionName().orElse(""),
            pipenvDetectableOptions.isPipProjectTreeOnly());
    }

}

