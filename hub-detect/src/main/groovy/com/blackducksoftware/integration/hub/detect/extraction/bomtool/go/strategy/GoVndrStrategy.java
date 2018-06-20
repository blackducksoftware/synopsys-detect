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

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoVndrExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.manager.result.search.ExtractionId;
import com.blackducksoftware.integration.hub.detect.manager.result.search.StrategyType;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.strategy.result.FileNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

public class GoVndrStrategy extends Strategy {
    public static final String VNDR_CONF_FILENAME = "vendor.conf";

    private final DetectFileFinder fileFinder;
    private final GoVndrExtractor goVndrExtractor;

    private File vndrConfig;

    public GoVndrStrategy(final StrategyEnvironment environment, final DetectFileFinder fileFinder, final GoVndrExtractor goVndrExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.goVndrExtractor = goVndrExtractor;
    }

    @Override
    public StrategyResult applicable() {
        vndrConfig = fileFinder.findFile(environment.getDirectory(), VNDR_CONF_FILENAME);
        if (vndrConfig == null) {
            return new FileNotFoundStrategyResult(VNDR_CONF_FILENAME);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(){
        return new PassedStrategyResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return goVndrExtractor.extract(environment.getDirectory(), vndrConfig);
    }

    @Override
    public String getName() {
        return "Vendor Config";
    }

    @Override
    public BomToolType getBomToolType() {
        return BomToolType.GO_VNDR;
    }

    @Override
    public StrategyType getStrategyType() {
        return StrategyType.GO_VNDR;
    }
}