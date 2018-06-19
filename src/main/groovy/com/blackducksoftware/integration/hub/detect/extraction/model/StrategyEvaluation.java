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
package com.blackducksoftware.integration.hub.detect.extraction.model;

import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction.ExtractionResultType;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.strategy.result.StrategyResult;

public class StrategyEvaluation {
    public Strategy strategy;
    public StrategyEnvironment environment;
    public StrategyState context;

    public StrategyResult searchable;
    public StrategyResult applicable;
    public StrategyResult extractable;

    public Extraction extraction;

    public StrategyEvaluation(final Strategy strategy, final StrategyEnvironment environment, final StrategyState context) {
        this.strategy = strategy;
        this.environment = environment;
        this.context = context;
    }

    public boolean isSearchable() {
        if (this.searchable != null) {
            return this.searchable.getPassed();
        }else {
            return false;
        }
    }

    public boolean isApplicable() {
        if (isSearchable()) {
            if (this.applicable != null) {
                return this.applicable.getPassed();
            }
        }
        return false;
    }

    public boolean isExtractable() {
        if (isApplicable()) {
            if (this.extractable != null) {
                return this.extractable.getPassed();
            }
        }
        return false;
    }

    public boolean isExtractionSuccess() {
        if (isExtractable()) {
            if (this.extraction != null) {
                return this.extraction.result == ExtractionResultType.Success;
            }
        }
        return false;
    }

}
