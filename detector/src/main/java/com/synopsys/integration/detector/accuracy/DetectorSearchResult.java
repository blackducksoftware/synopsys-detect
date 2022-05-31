package com.synopsys.integration.detector.accuracy;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.result.DetectorResult;
import com.synopsys.integration.detector.rule.EntryPoint;

public class DetectorSearchResult {
    @NotNull
    private final DetectorType detectorType;
    @NotNull
    private final DetectorSearchResultType searchResult;
    @Nullable //Only present when one or more entry points applied.
    private final EntryPoint entryPoint;
    @Nullable //Only present when searchable is not passed, aka NOT_SEARCHABLE. Otherwise it is passed.
    private final DetectorResult searchableResult;

    private DetectorSearchResult(
        DetectorType detectorType,
        DetectorSearchResultType searchResult,
        EntryPoint entryPoint,
        @Nullable DetectorResult searchableResult
    ) {
        this.detectorType = detectorType;
        this.searchResult = searchResult;
        this.entryPoint = entryPoint;
        this.searchableResult = searchableResult;
    }

    public static DetectorSearchResult notSearchable(DetectorType detectorType, DetectorResult detectableResult) {
        return new DetectorSearchResult(detectorType, DetectorSearchResultType.NOT_SEARCHABLE, null, detectableResult);
    }

    public static DetectorSearchResult found(DetectorType detectorType, EntryPoint entryPoint) {
        return new DetectorSearchResult(detectorType, DetectorSearchResultType.FOUND, entryPoint, null);
    }

    public static DetectorSearchResult notFound(DetectorType detectorType) {
        return new DetectorSearchResult(detectorType, DetectorSearchResultType.NOT_FOUND, null, null);
    }

    public DetectorSearchResultType getSearchResult() {
        return searchResult;
    }

    public boolean wasFound() {
        return searchResult == DetectorSearchResultType.FOUND;
    }

    public Optional<EntryPoint> getEntryPoint() {
        return Optional.ofNullable(entryPoint);
    }

    public String getMessage() {
        if (searchableResult != null) {
            return searchableResult.getDescription();
        }
        return "Unknown";
    }
}

