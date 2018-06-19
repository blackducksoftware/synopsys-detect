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

import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoDepsExtractor;
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

@Component
public class GoDepsStrategy extends Strategy {
    public static final String GODEPS_DIRECTORYNAME = "Godeps";

    private final DetectFileFinder fileFinder;
    private final GoDepsExtractor goDepsExtractor;

    private File goDepsDirectory;

    public GoDepsStrategy(final StrategyEnvironment environment, final DetectFileFinder fileFinder, final GoDepsExtractor goDepsExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.goDepsExtractor = goDepsExtractor;
    }

    @Override
    public StrategyResult applicable() {
        goDepsDirectory = fileFinder.findFile(environment.getDirectory(), GODEPS_DIRECTORYNAME);
        if (goDepsDirectory == null) {
            return new FileNotFoundStrategyResult(GODEPS_DIRECTORYNAME);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(){
        return new PassedStrategyResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return goDepsExtractor.extract(environment.getDirectory(), goDepsDirectory);
    }

    @Override
    public String getName() {
        return "Go Deps Lock File";
    }

    @Override
    public BomToolType getBomToolType() {
        return BomToolType.GO_GODEP;
    }

    @Override
    public StrategyType getStrategyType() {
        return StrategyType.GO_DEPS;
    }

}