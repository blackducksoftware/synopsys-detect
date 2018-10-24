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
package com.blackducksoftware.integration.hub.detect.workflow.profiling;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolEvaluation;

public class BomToolProfiler {
    public BomToolTimekeeper applicableTimekeeper = new BomToolTimekeeper();
    public BomToolTimekeeper extractableTimekeeper = new BomToolTimekeeper();
    public BomToolTimekeeper extractionTimekeeper = new BomToolTimekeeper();

    public BomToolProfiler(EventSystem eventSystem) {
        eventSystem.registerListener(Event.ApplicableStarted, it -> applicableStarted((BomTool) it));
        eventSystem.registerListener(Event.ApplicableEnded, it -> applicableEnded((BomTool) it));
        eventSystem.registerListener(Event.ExtractableStarted, it -> extractableStarted((BomTool) it));
        eventSystem.registerListener(Event.ExtractableEnded, it -> extractableEnded((BomTool) it));
        eventSystem.registerListener(Event.ExtractionStarted, it -> extractionStarted(((BomToolEvaluation) it).getBomTool()));
        eventSystem.registerListener(Event.ExtractionEnded, it -> extractionEnded(((BomToolEvaluation) it).getBomTool()));
    }

    private void applicableStarted(final BomTool bomTool) {
        applicableTimekeeper.started(bomTool);
    }

    private void applicableEnded(final BomTool bomTool) {
        applicableTimekeeper.ended(bomTool);
    }

    private void extractableStarted(final BomTool bomTool) {
        extractableTimekeeper.started(bomTool);
    }

    private void extractableEnded(final BomTool bomTool) {
        extractableTimekeeper.ended(bomTool);
    }

    private void extractionStarted(final BomTool bomTool) {
        extractionTimekeeper.started(bomTool);
    }

    private void extractionEnded(final BomTool bomTool) {
        extractionTimekeeper.ended(bomTool);
    }

    public List<BomToolTime> getApplicableTimings() {
        return applicableTimekeeper.getTimings();
    }

    public List<BomToolTime> getExtractableTimings() {
        return extractableTimekeeper.getTimings();
    }

    public List<BomToolTime> getExtractionTimings() {
        return extractionTimekeeper.getTimings();
    }

    public Map<BomToolGroupType, Long> getAggregateBomToolGroupTimes() {
        final Map<BomToolGroupType, Long> aggregate = new HashMap<>();
        addAggregateByBomToolGroupType(aggregate, getExtractableTimings());
        addAggregateByBomToolGroupType(aggregate, getExtractionTimings());
        return aggregate;
    }

    private void addAggregateByBomToolGroupType(final Map<BomToolGroupType, Long> aggregate, final List<BomToolTime> bomToolTimes) {
        for (final BomToolTime bomToolTime : bomToolTimes) {
            final BomToolGroupType type = bomToolTime.getBomTool().getBomToolGroupType();
            if (!aggregate.containsKey(type)) {
                aggregate.put(type, 0L);
            }
            final long time = bomToolTime.getMs();
            final Long currentTime = aggregate.get(type);
            final Long sum = time + currentTime;
            aggregate.put(type, sum);
        }
    }

}
