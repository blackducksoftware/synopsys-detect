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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.StrategySearchOptions;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyException;
import com.blackducksoftware.integration.hub.detect.strategy.result.ExecutableNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.FileNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.InspectorNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class PipInspectorStrategy extends Strategy<PipInspectorContext, PipInspectorExtractor> {
    public static final String SETUPTOOLS_DEFAULT_FILE_NAME = "setup.py";
    public static final String PIPFILE_FILE_NAME = "Pipfile";
    public static final String PIPFILE_DOT_LOCK_FILE_NAME = "Pipfile.lock";

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public PythonExecutableFinder pythonExecutableFinder;

    @Autowired
    public PipInspectorManager pipInspectorManager;

    @Autowired
    public DetectConfiguration detectConfiguration;

    public PipInspectorStrategy() {
        super("Pip Inspector", BomToolType.PIP, PipInspectorContext.class, PipInspectorExtractor.class, StrategySearchOptions.defaultNotNested());
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final PipInspectorContext context) {
        context.setupFile = fileFinder.findFile(environment.getDirectory(), SETUPTOOLS_DEFAULT_FILE_NAME);

        context.requirementFilePath = detectConfiguration.getRequirementsFilePath();

        final boolean hasSetups = context.setupFile != null;
        final boolean hasRequirements = context.requirementFilePath != null && StringUtils.isNotBlank(context.requirementFilePath);
        if (hasSetups || hasRequirements) {
            return new PassedStrategyResult();
        } else {
            return new FileNotFoundStrategyResult(SETUPTOOLS_DEFAULT_FILE_NAME);
        }

    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final PipInspectorContext context) throws StrategyException {
        context.pythonExe = pythonExecutableFinder.findExecutable(environment, ExecutableType.PYTHON, ExecutableType.PYTHON3, detectConfiguration.getPythonPath());
        if (context.pythonExe == null) {
            return new ExecutableNotFoundStrategyResult("python");
        }

        final String pipExe = pythonExecutableFinder.findExecutable(environment, ExecutableType.PIP, ExecutableType.PIP3);
        if (pipExe == null) {
            return new ExecutableNotFoundStrategyResult("pip");
        }

        context.pipInspector = pipInspectorManager.findPipInspector(environment);
        if (context.pipInspector == null) {
            return new InspectorNotFoundStrategyResult("pip");
        }

        return new PassedStrategyResult();
    }

}
