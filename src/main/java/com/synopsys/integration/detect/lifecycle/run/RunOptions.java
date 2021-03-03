/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.bdio.AggregateMode;

public class RunOptions {
    private final boolean unmapCodeLocations;
    private final String aggregateName;
    private final AggregateMode aggregateMode;
    private final List<DetectTool> preferredTools;
    private final DetectToolFilter detectToolFilter;
    private final boolean useBdio2;
    private final BlackduckScanMode scanMode;

    public RunOptions(boolean unmapCodeLocations, @Nullable String aggregateName, AggregateMode aggregateMode, List<DetectTool> preferredTools, DetectToolFilter detectToolFilter, boolean useBdio2, BlackduckScanMode scanMode) {
        this.unmapCodeLocations = unmapCodeLocations;
        this.aggregateName = aggregateName;
        this.aggregateMode = aggregateMode;
        this.preferredTools = preferredTools;
        this.detectToolFilter = detectToolFilter;
        this.useBdio2 = useBdio2;
        this.scanMode = scanMode;
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

    public BlackduckScanMode getScanMode() {
        return scanMode;
    }

    public boolean shouldPerformRapidModeScan() {
        return BlackduckScanMode.RAPID == scanMode;
    }
}
