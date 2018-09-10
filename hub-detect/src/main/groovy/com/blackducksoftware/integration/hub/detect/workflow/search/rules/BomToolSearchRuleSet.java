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
package com.blackducksoftware.integration.hub.detect.workflow.search.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolExcludedBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.ForcedNestedPassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.MaxDepthExceededBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.NotNestableBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.NotSelfNestableBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.YieldedBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.profiling.BomToolProfiler;

public class BomToolSearchRuleSet {
    private final List<BomToolSearchRule> orderedBomToolRules;
    private final BomToolEnvironment environment;

    public BomToolSearchRuleSet(final List<BomToolSearchRule> orderedBomToolRules, final BomToolEnvironment environment) {
        this.orderedBomToolRules = orderedBomToolRules;
        this.environment = environment;
    }

    public List<BomToolSearchRule> getOrderedBomToolRules() {
        return orderedBomToolRules;
    }

    public BomToolEnvironment getEnvironment() {
        return environment;
    }
}
