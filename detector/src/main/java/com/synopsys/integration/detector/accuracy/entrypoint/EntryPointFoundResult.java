package com.synopsys.integration.detector.accuracy.entrypoint;

import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detector.result.DetectorResult;
import com.synopsys.integration.detector.rule.EntryPoint;

public class EntryPointFoundResult {
    private final EntryPoint entryPoint;
    private final DetectorResult searchResult;
    private final DetectableResult applicableResult;
    private final EntryPointEvaluation entryPointEvaluation;

    public EntryPointFoundResult(
        EntryPoint entryPoint,
        DetectorResult searchResult,
        DetectableResult applicableResult,
        EntryPointEvaluation entryPointEvaluation
    ) {
        this.entryPoint = entryPoint;
        this.searchResult = searchResult;
        this.applicableResult = applicableResult;
        this.entryPointEvaluation = entryPointEvaluation;
    }

    public static EntryPointFoundResult evaluated(
        EntryPoint entryPoint,
        DetectorResult searchResult,
        DetectableResult applicableResult,
        EntryPointEvaluation entryPointEvaluation
    ) {
        return new EntryPointFoundResult(entryPoint, searchResult, applicableResult, entryPointEvaluation);
    }

    public EntryPoint getEntryPoint() {
        return entryPoint;
    }

    public DetectableResult getApplicableResult() {
        return applicableResult;
    }

    public DetectorResult getSearchResult() {
        return searchResult;
    }

    public EntryPointEvaluation getEntryPointEvaluation() {
        return entryPointEvaluation;
    }
}
