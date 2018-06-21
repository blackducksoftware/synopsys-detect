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

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.manager.result.search.ExtractionId;
import com.blackducksoftware.integration.hub.detect.manager.result.search.StrategyType;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyException;
import com.blackducksoftware.integration.hub.detect.strategy.result.ExecutableNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.FileNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.InspectorNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

public class PipInspectorStrategy extends Strategy {
    public static final String SETUPTOOLS_DEFAULT_FILE_NAME = "setup.py";

    private final DetectFileFinder fileFinder;
    private final PythonExecutableFinder pythonExecutableFinder;
    private final PipInspectorManager pipInspectorManager;
    private final PipInspectorExtractor pipInspectorExtractor;
    private final String requirementFilePath;

    String pythonExe;
    File pipInspector;
    File setupFile;


    public PipInspectorStrategy(final StrategyEnvironment environment, final String requirementFilePath, final DetectFileFinder fileFinder, final PythonExecutableFinder pythonExecutableFinder, final PipInspectorManager pipInspectorManager, final PipInspectorExtractor pipInspectorExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.pipInspectorExtractor = pipInspectorExtractor;
        this.pythonExecutableFinder = pythonExecutableFinder;
        this.pipInspectorManager = pipInspectorManager;
        this.requirementFilePath = requirementFilePath;
    }

    @Override
    public StrategyResult applicable() {
        setupFile = fileFinder.findFile(environment.getDirectory(), SETUPTOOLS_DEFAULT_FILE_NAME);
        final boolean hasSetups = setupFile != null;
        final boolean hasRequirements = requirementFilePath != null && StringUtils.isNotBlank(requirementFilePath);
        if (hasSetups || hasRequirements) {
            return new PassedStrategyResult();
        } else {
            return new FileNotFoundStrategyResult(SETUPTOOLS_DEFAULT_FILE_NAME);
        }

    }

    @Override
    public StrategyResult extractable() throws StrategyException {
        pythonExe = pythonExecutableFinder.findPython(environment);
        if (pythonExe == null) {
            return new ExecutableNotFoundStrategyResult("python");
        }

        final String pipExe = pythonExecutableFinder.findPip(environment);
        if (pipExe == null) {
            return new ExecutableNotFoundStrategyResult("pip");
        }

        pipInspector = pipInspectorManager.findPipInspector(environment);
        if (pipInspector == null) {
            return new InspectorNotFoundStrategyResult("pip");
        }

        return new PassedStrategyResult();
    }


    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return pipInspectorExtractor.extract(environment.getDirectory(), pythonExe, pipInspector, setupFile, requirementFilePath);
    }


    @Override
    public String getName() {
        return "Pip Inspector";
    }


    @Override
    public BomToolType getBomToolType() {
        return BomToolType.PIP;
    }


    @Override
    public StrategyType getStrategyType() {
        return StrategyType.PIP_INSPECTOR;
    }

}
