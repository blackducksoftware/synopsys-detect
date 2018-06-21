/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.pip;

import java.io.File;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.manager.result.search.ExtractionId;
import com.blackducksoftware.integration.hub.detect.manager.result.search.StrategyType;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyException;
import com.blackducksoftware.integration.hub.detect.strategy.result.ExecutableNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.FilesNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

public class PipenvStrategy extends Strategy {
    public static final String SETUPTOOLS_DEFAULT_FILE_NAME = "setup.py";
    public static final String PIPFILE_FILE_NAME = "Pipfile";
    public static final String PIPFILE_DOT_LOCK_FILE_NAME = "Pipfile.lock";

    private final DetectFileFinder fileFinder;
    private final PythonExecutableFinder pythonExecutableFinder;
    private final PipenvExtractor pipenvExtractor;

    String pythonExe;
    String pipenvExe;
    File pipfileDotLock;
    File pipfile;
    File setupFile;

    public PipenvStrategy(final StrategyEnvironment environment, final DetectFileFinder fileFinder, final PythonExecutableFinder pythonExecutableFinder, final PipenvExtractor pipenvExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.pipenvExtractor = pipenvExtractor;
        this.pythonExecutableFinder = pythonExecutableFinder;
    }

    @Override
    public StrategyResult applicable() {
        pipfile = fileFinder.findFile(environment.getDirectory(), PIPFILE_FILE_NAME);
        pipfileDotLock = fileFinder.findFile(environment.getDirectory(), PIPFILE_DOT_LOCK_FILE_NAME);

        if (pipfile != null || pipfileDotLock != null) {
            return new PassedStrategyResult();
        } else {
            return new FilesNotFoundStrategyResult(PIPFILE_FILE_NAME, PIPFILE_DOT_LOCK_FILE_NAME);
        }

    }

    @Override
    public StrategyResult extractable() throws StrategyException {
        pythonExe = pythonExecutableFinder.findPython(environment);
        if (pythonExe == null) {
            return new ExecutableNotFoundStrategyResult("python");
        }

        pipenvExe = pythonExecutableFinder.findPipenv(environment);
        if (pipenvExe == null) {
            return new ExecutableNotFoundStrategyResult("pipenv");
        }

        setupFile = fileFinder.findFile(environment.getDirectory(), SETUPTOOLS_DEFAULT_FILE_NAME);

        return new PassedStrategyResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return pipenvExtractor.extract(environment.getDirectory(), pythonExe, pipenvExe, pipfileDotLock, pipfile, setupFile);
    }

    @Override
    public String getName() {
        return "Pipenv Graph";
    }

    @Override
    public BomToolType getBomToolType() {
        return BomToolType.PIP;
    }

    @Override
    public StrategyType getStrategyType() {
        return StrategyType.PIP_ENV;
    }

}
