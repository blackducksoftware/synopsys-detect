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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoDepContext;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoDepExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoInspectorManager;
import com.blackducksoftware.integration.hub.detect.extraction.model.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.extraction.model.StandardExecutableFinder.StandardExecutableType;
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
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class GoCliStrategy extends Strategy<GoDepContext, GoDepExtractor> {
    public static final String GOFILE_FILENAME_PATTERN = "*.go";

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public GoInspectorManager goInspectorManager;

    @Autowired
    public StandardExecutableFinder standardExecutableFinder;


    public GoCliStrategy() {
        super("Go Cli", BomToolType.GO_DEP, GoDepContext.class, GoDepExtractor.class, StrategySearchOptions.defaultNotNested());
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final GoDepContext context) {
        final List<File> found = fileFinder.findFiles(environment.getDirectory(), GOFILE_FILENAME_PATTERN);
        if (found == null || found.size() == 0) {
            return new FileNotFoundStrategyResult(GOFILE_FILENAME_PATTERN);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final GoDepContext context) throws StrategyException {
        context.goExe = standardExecutableFinder.getExecutable(StandardExecutableType.GO);
        if (context.goExe == null) {
            return new ExecutableNotFoundStrategyResult("go");
        }

        context.goDepInspector = goInspectorManager.evaluate(environment);
        if (context.goDepInspector == null) {
            return new InspectorNotFoundStrategyResult("go");
        }

        return new PassedStrategyResult();
    }

}