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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm;

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
import com.blackducksoftware.integration.hub.detect.strategy.result.FileNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.NpmRunInstallStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

public class NpmCliStrategy extends Strategy{
    public static final String NODE_MODULES = "node_modules";
    public static final String PACKAGE_JSON = "package.json";

    private final DetectFileFinder fileFinder;
    private NpmExecutableFinder npmExecutableFinder;
    private final NpmCliExtractor npmCliExtractor;

    private String npmExe;

    public NpmCliStrategy(final StrategyEnvironment environment, final DetectFileFinder fileFinder, final NpmCliExtractor npmCliExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.npmCliExtractor = npmCliExtractor;
    }


    @Override
    public StrategyResult applicable() {
        final File packageJson = fileFinder.findFile(environment.getDirectory(), PACKAGE_JSON);
        if (packageJson == null) {
            return new FileNotFoundStrategyResult(PACKAGE_JSON);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable() throws StrategyException {
        final File nodeModules = fileFinder.findFile(environment.getDirectory(), NODE_MODULES);
        if (nodeModules == null) {
            return new NpmRunInstallStrategyResult(environment.getDirectory().getAbsolutePath());
        }

        npmExe = npmExecutableFinder.findNpm(environment);
        if (npmExe == null) {
            return new ExecutableNotFoundStrategyResult("npm");
        }

        return new PassedStrategyResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return npmCliExtractor.extract(environment.getDirectory(), npmExe, extractionId);
    }

    @Override
    public String getName() {
        return "Npm Cli";
    }

    @Override
    public BomToolType getBomToolType() {
        return BomToolType.NPM;
    }

    @Override
    public StrategyType getStrategyType() {
        return StrategyType.NPM_CLI;
    }

}
