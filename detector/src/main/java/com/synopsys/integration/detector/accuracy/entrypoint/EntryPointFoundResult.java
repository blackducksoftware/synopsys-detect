package com.synopsys.integration.detector.accuracy.entrypoint;

import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detector.result.DetectorResult;
import com.synopsys.integration.detector.rule.EntryPoint;

public class EntryPointNotFoundResult {
    private final EntryPoint entryPoint;
    private final DetectorResult searchResult;
    private final DetectableResult applicableResult;

    public EntryPointNotFoundResult(
        EntryPoint entryPoint,
        DetectorResult searchResult,
        DetectableResult applicableResult
    ) {
        this.entryPoint = entryPoint;
        this.searchResult = searchResult;
        this.applicableResult = applicableResult;
    }

    public static EntryPointNotFoundResult notSearchable(EntryPoint entryPoint, DetectorResult searchResult) {

    }

    public static EntryPointNotFoundResult notApplicable(EntryPoint entryPoint, DetectorResult searchResult, DetectableResult applicableResult) {

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
}
