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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.hex;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.model.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.extraction.model.StandardExecutableFinder.StandardExecutableType;
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
public class RebarStrategy extends Strategy<RebarContext, RebarExtractor> {
    public static final String REBAR_CONFIG = "rebar.config";

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public StandardExecutableFinder standardExecutableFinder;

    public RebarStrategy() {
        super("Rebar Config", BomToolType.HEX, RebarContext.class, RebarExtractor.class, StrategySearchOptions.defaultNotNested());
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final RebarContext context) {
        final File rebar = fileFinder.findFile(environment.getDirectory(), REBAR_CONFIG);
        if (rebar == null) {
            return new FileNotFoundStrategyResult(REBAR_CONFIG);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final RebarContext context) throws StrategyException {
        context.rebarExe = standardExecutableFinder.getExecutable(StandardExecutableType.CONDA);

        if (context.rebarExe == null) {
            return new ExecutableNotFoundStrategyResult("rebar");
        }

        return new PassedStrategyResult();
    }

}