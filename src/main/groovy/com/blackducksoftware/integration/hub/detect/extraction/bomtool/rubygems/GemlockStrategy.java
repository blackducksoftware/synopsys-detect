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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.rubygems;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.model.ExtractionContext;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.StrategySearchOptions;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.strategy.result.FileNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class GemlockStrategy extends Strategy {
    public static final String GEMFILE_LOCK_FILENAME = "Gemfile.lock";
    public static final String GEMFILE_KEY = "gemfile";

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public GemlockExtractor extractor;

    public GemlockStrategy() {
        super("Gemlock", BomToolType.RUBYGEMS, StrategySearchOptions.defaultNotNested());
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final ExtractionContext context) {
        final File gemlock = fileFinder.findFile(environment.getDirectory(), GEMFILE_LOCK_FILENAME);
        if (gemlock != null) {
            context.addFileKey(GEMFILE_KEY, gemlock);
        } else {
            return new FileNotFoundStrategyResult(GEMFILE_LOCK_FILENAME);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final ExtractionContext context){
        return new PassedStrategyResult();
    }

    @Override
    public Extraction extract(final StrategyEnvironment environment, final ExtractionContext context) {
        final File directory = context.getDirectory();
        final File gemlock = context.getFileKey(GEMFILE_KEY);
        return extractor.extract(directory, gemlock);
    }

}