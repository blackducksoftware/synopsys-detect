package com.synopsys.integration.detector.accuracy.search;

import java.util.function.Predicate;

import com.synopsys.integration.detector.base.DetectorType;

public class SearchOptions {
    private final Predicate<DetectorType> detectorFilter;
    private final boolean forceNestedSearch;

    public SearchOptions(
        Predicate<DetectorType> detectorFilter,
        boolean forceNestedSearch
    ) {
        this.detectorFilter = detectorFilter;
        this.forceNestedSearch = forceNestedSearch;
    }

    public Predicate<DetectorType> getDetectorFilter() {
        return detectorFilter;
    }

    public boolean isForceNestedSearch() {
        return forceNestedSearch;
    }
}
