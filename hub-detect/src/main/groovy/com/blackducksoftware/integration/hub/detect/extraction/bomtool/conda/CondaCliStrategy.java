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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.conda;

import java.io.File;

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

public class CondaCliStrategy extends Strategy {
    public static final String ENVIRONEMNT_YML = "environment.yml";

    private final DetectFileFinder fileFinder;
    private StandardExecutableFinder standardExecutableFinder;
    private final CondaCliExtractor condaExtractor;

    private File condaExe;

    public CondaCliStrategy(final StrategyEnvironment environment, final DetectFileFinder fileFinder, final StandardExecutableFinder standardExecutableFinder, final CondaCliExtractor condaExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.condaExtractor = condaExtractor;
    }

    @Override
    public StrategyResult applicable() {
        final File ymlFile = fileFinder.findFile(environment.getDirectory(), ENVIRONEMNT_YML);
        if (ymlFile == null) {
            return new FileNotFoundStrategyResult(ENVIRONEMNT_YML);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable() throws StrategyException {
        condaExe = standardExecutableFinder.getExecutable(StandardExecutableType.CONDA);

        if (condaExe == null) {
            return new ExecutableNotFoundStrategyResult("conda");
        }

        return new PassedStrategyResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return condaExtractor.extract(environment.getDirectory(), condaExe);
    }

    @Override
    public String getName() {
        return "Conda Cli";
    }

    @Override
    public BomToolType getBomToolType() {
        return BomToolType.CONDA;
    }

    @Override
    public StrategyType getStrategyType() {
        return StrategyType.CONDA_CLI;
    }

}
