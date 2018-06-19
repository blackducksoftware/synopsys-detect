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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.packagist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.StrategySearchOptions;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.strategy.result.FileNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class ComposerLockStrategy extends Strategy<ComposerLockContext, ComposerLockExtractor> {
    public static final String COMPOSER_LOCK = "composer.lock";
    public static final String COMPOSER_JSON = "composer.json";

    @Autowired
    public DetectFileFinder fileFinder;

    public ComposerLockStrategy() {
        super("Composer Lock", BomToolType.PACKAGIST, ComposerLockContext.class, ComposerLockExtractor.class, StrategySearchOptions.defaultNotNested());
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final ComposerLockContext context) {
        context.composerLock = fileFinder.findFile(environment.getDirectory(), COMPOSER_LOCK);
        if (context.composerLock == null) {
            return new FileNotFoundStrategyResult(COMPOSER_LOCK);
        }

        context.composerJson = fileFinder.findFile(environment.getDirectory(), COMPOSER_JSON);
        if (context.composerJson == null) {
            return new FileNotFoundStrategyResult(COMPOSER_JSON);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final ComposerLockContext context){
        return new PassedStrategyResult();
    }

}