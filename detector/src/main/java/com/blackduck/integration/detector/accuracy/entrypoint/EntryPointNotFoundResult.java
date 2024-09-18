package com.blackduck.integration.detector.accuracy.entrypoint;

import java.util.Optional;

import com.blackduck.integration.detector.result.DetectorResult;
import com.blackduck.integration.detector.rule.EntryPoint;
import org.jetbrains.annotations.Nullable;

import com.blackduck.integration.detectable.detectable.result.DetectableResult;

public class EntryPointNotFoundResult {
    private final EntryPoint entryPoint;
    private final DetectorResult searchResult;
    @Nullable
    private final DetectableResult applicableResult;

    public EntryPointNotFoundResult(
        EntryPoint entryPoint,
        DetectorResult searchResult,
        @Nullable DetectableResult applicableResult
    ) {
        this.entryPoint = entryPoint;
        this.searchResult = searchResult;
        this.applicableResult = applicableResult;
    }

    public static EntryPointNotFoundResult notSearchable(EntryPoint entryPoint, DetectorResult searchResult) {
        return new EntryPointNotFoundResult(entryPoint, searchResult, null);
    }

    public static EntryPointNotFoundResult notApplicable(EntryPoint entryPoint, DetectorResult searchResult, DetectableResult applicableResult) {
        return new EntryPointNotFoundResult(entryPoint, searchResult, applicableResult);
    }

    public EntryPoint getEntryPoint() {
        return entryPoint;
    }

    public Optional<DetectableResult> getApplicableResult() {
        return Optional.ofNullable(applicableResult);
    }

    public DetectorResult getSearchResult() {
        return searchResult;
    }
}
