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
import com.blackducksoftware.integration.hub.detect.strategy.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class NpmCliStrategy extends Strategy<NpmCliContext, NpmCliExtractor>{
    public static final String NODE_MODULES= "node_modules";

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public NpmExecutableFinder npmExecutableFinder;

    public NpmCliStrategy() {
        super("Npm Cli", BomToolType.NPM, NpmCliContext.class, NpmCliExtractor.class, StrategySearchOptions.defaultNested());
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final NpmCliContext context) {
        final File pom= fileFinder.findFile(environment.getDirectory(), NODE_MODULES);
        if (pom == null) {
            return new FileNotFoundStrategyResult(NODE_MODULES);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final NpmCliContext context) throws StrategyException {
        context.npmExe = npmExecutableFinder.findNpm(environment);

        if (context.npmExe == null) {
            return new ExecutableNotFoundStrategyResult("npm");
        }

        return new PassedStrategyResult();
    }

}
