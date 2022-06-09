package com.synopsys.integration.detector.accuracy;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detector.rule.EntryPoint;

public class DetectorSearchEntryPointResult {
    @NotNull //Only present when one or more entry points applied.
    private final EntryPoint entryPoint;
    @NotNull //Only present when searchable is not passed, aka NOT_SEARCHABLE. Otherwise it is passed.
    private final DetectableResult applicableResult;

    public DetectorSearchEntryPointResult(
        @NotNull EntryPoint entryPoint,
        @NotNull DetectableResult applicableResult
    ) {
        this.entryPoint = entryPoint;
        this.applicableResult = applicableResult;
    }

    public EntryPoint getEntryPoint() {
        return entryPoint;
    }

    public DetectableResult getApplicableResult() {
        return applicableResult;
    }
}

