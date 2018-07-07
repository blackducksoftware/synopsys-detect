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

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;

public class BomToolSearchRuleBuilder {
    private final BomTool bomTool;
    private int maxDepth;
    private boolean nestable;
    private final List<BomToolType> yieldsTo;

    public BomToolSearchRuleBuilder(final BomTool bomTool) {
        this.bomTool = bomTool;
        yieldsTo = new ArrayList<>();
    }

    public BomToolSearchRuleBuilder defaultNotNested() {
        maxDepth(Integer.MAX_VALUE);
        return nestable(false);
    }

    public BomToolSearchRuleBuilder defaultNested() {
        maxDepth(Integer.MAX_VALUE);
        return nestable(true);
    }

    public BomToolSearchRuleBuilder maxDepth(final int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public BomToolSearchRuleBuilder nestable(final boolean nestable) {
        this.nestable = nestable;
        return this;
    }

    public BomToolSearchRuleBuilder yield(final BomToolType type) {
        this.yieldsTo.add(type);
        return this;
    }

    public BomToolSearchRule build() {
        return new BomToolSearchRule(bomTool, maxDepth, nestable, yieldsTo);
    }
}
