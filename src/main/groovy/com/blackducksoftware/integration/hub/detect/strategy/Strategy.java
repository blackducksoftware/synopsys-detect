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
package com.blackducksoftware.integration.hub.detect.strategy;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.model.StrategyState;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyException;
import com.blackducksoftware.integration.hub.detect.strategy.result.BomToolExcludedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.ForcedNestedPassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.MaxDepthExceededStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.NotNestableStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.NotSelfNestableStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.YieldedStrategyResult;

@SuppressWarnings("rawtypes")
public abstract class Strategy  {
    private final String name;
    private final BomToolType bomToolType;

    private final Set<Strategy> yieldsToStrategies = new HashSet<>();
    private final StrategySearchOptions searchOptions;

    public Strategy(final String name, final BomToolType bomToolType, final StrategySearchOptions searchOptions) {
        this.name = name;
        this.bomToolType = bomToolType;
        this.searchOptions = searchOptions;
    }

    public void yieldsTo(final Strategy strategy) {
        yieldsToStrategies.add(strategy);
    }

    public StrategyResult searchable(final StrategyEnvironment environment, final StrategyState context) {
        if (!environment.getBomToolFilter().shouldInclude(bomToolType.toString())) {
            return new BomToolExcludedStrategyResult();
        }

        if (environment.getDepth() > searchOptions.getMaxDepth()) {
            return new MaxDepthExceededStrategyResult(environment.getDepth(), searchOptions.getMaxDepth());
        }

        final Set<Strategy> yielded = yieldsToStrategies.stream().filter(it -> environment.getAppliedToDirectory().contains(it)).collect(Collectors.toSet());
        if (yielded.size() > 0) {
            return new YieldedStrategyResult(yielded);
        }

        if (environment.getForceNestedSearch()) {
            return new ForcedNestedPassedStrategyResult();
        } else if (searchOptions.getNestable()) {
            if (environment.getAppliedToParent().contains(this)) {
                return new NotSelfNestableStrategyResult();
            }
        } else if (!searchOptions.getNestable() && environment.getAppliedToParent().size() > 0) {
            return new NotNestableStrategyResult();
        }

        return new PassedStrategyResult();
    }

    //Applicable should be light-weight and should never throw an exception. Look for files, check properties, short and sweet.
    public abstract StrategyResult applicable(final StrategyEnvironment environment, final StrategyState context);
    //Extractable may be as heavy as needed, and may (and sometimes should) fail. Make web requests, install inspectors or run executables.
    public abstract StrategyResult extractable(final StrategyEnvironment environment, final StrategyState context) throws StrategyException;

    public abstract Extraction extract(final StrategyEnvironment environment, final StrategyState context);

    public String getName() {
        return name;
    }

    public String getDescriptiveName() {
        return bomToolType.toString() + " - " + name;
    }

    public BomToolType getBomToolType() {
        return bomToolType;
    }

    public Set<Strategy> getYieldsToStrategies() {
        return yieldsToStrategies;
    }

    public StrategySearchOptions getSearchOptions() {
        return searchOptions;
    }

    public StrategyState createNewState() {
        // TODO Auto-generated method stub
        return null;
    }
}
