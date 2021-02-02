/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detect.lifecycle.run;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.configuration.enumeration.DetectWorkflow;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.bdio.AggregateMode;

public class RunOptions {
    private final boolean unmapCodeLocations;
    private final String aggregateName;
    private final AggregateMode aggregateMode;
    private final List<DetectTool> preferredTools;
    private final DetectToolFilter detectToolFilter;
    private final boolean useBdio2;
    private final DetectWorkflow detectWorkflow;

    public RunOptions(boolean unmapCodeLocations, @Nullable String aggregateName, AggregateMode aggregateMode, List<DetectTool> preferredTools, DetectToolFilter detectToolFilter, boolean useBdio2, DetectWorkflow detectWorkflow) {
        this.unmapCodeLocations = unmapCodeLocations;
        this.aggregateName = aggregateName;
        this.aggregateMode = aggregateMode;
        this.preferredTools = preferredTools;
        this.detectToolFilter = detectToolFilter;
        this.useBdio2 = useBdio2;
        this.detectWorkflow = detectWorkflow;
    }

    public boolean shouldUnmapCodeLocations() {
        return unmapCodeLocations;
    }

    public Optional<String> getAggregateName() {
        return Optional.ofNullable(aggregateName);
    }

    public AggregateMode getAggregateMode() {
        return aggregateMode;
    }

    public List<DetectTool> getPreferredTools() {
        return preferredTools;
    }

    public DetectToolFilter getDetectToolFilter() {
        return detectToolFilter;
    }

    public boolean shouldUseBdio2() {
        return useBdio2;
    }

    public DetectWorkflow getDetectWorkflow() {
        return detectWorkflow;
    }

    public boolean shouldPerformRapidWorkflow() {
        return DetectWorkflow.TRANSIENT == detectWorkflow;
    }
}
