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

public class BomToolProfiler {
    public BomToolTimekeeper applicableTimekeeper = new BomToolTimekeeper();
    public BomToolTimekeeper extractableTimekeeper = new BomToolTimekeeper();
    public BomToolTimekeeper extractionTimekeeper = new BomToolTimekeeper();
    private EventSystem eventSystem;

    public BomToolProfiler(EventSystem eventSystem) {
        this.eventSystem = eventSystem;
        eventSystem.registerListener(Event.ApplicableStarted, event -> applicableStarted(event));
        eventSystem.registerListener(Event.ApplicableEnded, event -> applicableEnded(event));
        eventSystem.registerListener(Event.ExtractableStarted, event -> extractableStarted(event));
        eventSystem.registerListener(Event.ExtractableEnded, event -> extractableEnded(event));
        eventSystem.registerListener(Event.ExtractionStarted, event -> extractionStarted(event.getBomTool()));
        eventSystem.registerListener(Event.ExtractionEnded, event -> extractionEnded(event.getBomTool()));
        eventSystem.registerListener(Event.BomToolsComplete, event -> bomToolsComplete());
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

    public void bomToolsComplete() {
        BomToolAggregateTimings timings = new BomToolAggregateTimings();
        timings.bomToolTimings = getAggregateBomToolGroupTimes();
        eventSystem.publishEvent(Event.BomToolsProfiled, timings);
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
