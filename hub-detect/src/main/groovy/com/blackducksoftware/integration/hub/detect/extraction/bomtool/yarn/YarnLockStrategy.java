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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.yarn;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

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
import com.blackducksoftware.integration.hub.detect.strategy.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

public class YarnLockStrategy extends Strategy {
    public static final String YARN_LOCK_FILENAME = "yarn.lock";

    private final DetectFileFinder fileFinder;
    private final StandardExecutableFinder standardExecutableFinder;
    private final YarnLockExtractor yarnLockExtractor;
    private final boolean productionDependenciesOnly;

    File yarnlock;
    String yarnExe;

    public YarnLockStrategy(final StrategyEnvironment environment, final boolean productionDependenciesOnly, final DetectFileFinder fileFinder, final StandardExecutableFinder standardExecutableFinder, final YarnLockExtractor yarnLockExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.yarnLockExtractor = yarnLockExtractor;
        this.standardExecutableFinder = standardExecutableFinder;
        this.productionDependenciesOnly = productionDependenciesOnly;
    }


    @Override
    public StrategyResult applicable() {
        yarnlock = fileFinder.findFile(environment.getDirectory(), YARN_LOCK_FILENAME);
        if (yarnlock == null) {
            return new FileNotFoundStrategyResult(YARN_LOCK_FILENAME);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable() throws StrategyException{
        final File yarn = standardExecutableFinder.getExecutable(StandardExecutableType.YARN);
        if (yarn != null) {
            yarnExe = yarn.toString();
        }

        if (productionDependenciesOnly && StringUtils.isBlank(yarnExe)) {
            return new ExecutableNotFoundStrategyResult("Could not find the Yarn executable, can not get the production only dependencies.");
        }

        return new PassedStrategyResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return yarnLockExtractor.extract(environment.getDirectory(), yarnlock, yarnExe);
    }


    @Override
    public String getName() {
        return "Yarn Lock";
    }


    @Override
    public BomToolType getBomToolType() {
        return BomToolType.YARN;
    }


    @Override
    public StrategyType getStrategyType() {
        return StrategyType.YARN_LOCK;
    }

}