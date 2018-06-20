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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.strategy;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoDepExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoInspectorManager;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.model.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.extraction.model.StandardExecutableFinder.StandardExecutableType;
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

public class GoCliStrategy extends Strategy {
    public static final String GOFILE_FILENAME_PATTERN = "*.go";

    private final DetectFileFinder fileFinder;
    private final GoInspectorManager goInspectorManager;
    private final StandardExecutableFinder standardExecutableFinder;
    private final GoDepExtractor goDepExtractor;

    private File goExe;
    private String goDepInspector;

    public GoCliStrategy(final StrategyEnvironment environment, final DetectFileFinder fileFinder, final StandardExecutableFinder standardExecutableFinder, final GoInspectorManager goInspectorManager, final GoDepExtractor goDepExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.standardExecutableFinder = standardExecutableFinder;
        this.goInspectorManager = goInspectorManager;
        this.goDepExtractor = goDepExtractor;
    }

    @Override
    public StrategyResult applicable() {
        final List<File> found = fileFinder.findFiles(environment.getDirectory(), GOFILE_FILENAME_PATTERN);
        if (found == null || found.size() == 0) {
            return new FileNotFoundStrategyResult(GOFILE_FILENAME_PATTERN);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable() throws StrategyException {
        goExe = standardExecutableFinder.getExecutable(StandardExecutableType.GO);
        if (goExe == null) {
            return new ExecutableNotFoundStrategyResult("go");
        }

        goDepInspector = goInspectorManager.evaluate(environment);
        if (goDepInspector == null) {
            return new InspectorNotFoundStrategyResult("go");
        }

        return new PassedStrategyResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return goDepExtractor.extract(environment.getDirectory(), goExe, goDepInspector);
    }

    @Override
    public String getName() {
        return "Go Cli";
    }

    @Override
    public BomToolType getBomToolType() {
        return BomToolType.GO_DEP;
    }

    @Override
    public StrategyType getStrategyType() {
        return StrategyType.GO_CLI;
    }

}