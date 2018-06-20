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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.StrategyCoordinator;

@Component
public class NpmStrategyCoordinator extends StrategyCoordinator {
    @Autowired
    public NpmCliStrategy cliStrategy;

    @Autowired
    public NpmShrinkwrapStrategy shrinkwrapStrategy;

    @Autowired
    public NpmPackageLockStrategy packageLockStrategy;

    @Autowired
    public List<Strategy> strategies;

    @Override
    public void init() {
        for (final Strategy strategy : strategies) {
            if (strategy.getBomToolType() == BomToolType.YARN) {
                packageLockStrategy.yieldsTo(strategy);
                shrinkwrapStrategy.yieldsTo(strategy);
                cliStrategy.yieldsTo(strategy);
            }
        }

        cliStrategy.yieldsTo(shrinkwrapStrategy);
        cliStrategy.yieldsTo(packageLockStrategy);

        shrinkwrapStrategy.yieldsTo(packageLockStrategy);
    }
}
