package com.synopsys.integration.detector.accuracy;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detector.result.DetectorResult;

public class DetectorSearchResult {
    @Nullable
    private final DetectorResult notSearchable;
    @Nullable //The found entry point, there will only be one.
    private final DetectorSearchEntryPointResult foundEntryPoint;
    @NotNull //All entry points evaluated but not found.
    private final List<DetectorSearchEntryPointResult> notFoundEntryPoints;

    private DetectorSearchResult(
        @Nullable DetectorResult notSearchable,
        @Nullable DetectorSearchEntryPointResult foundEntryPoint,
        @NotNull List<DetectorSearchEntryPointResult> notFoundEntryPoints
    ) {
        this.notSearchable = notSearchable;
        this.foundEntryPoint = foundEntryPoint;
        this.notFoundEntryPoints = notFoundEntryPoints;
    }

    public static DetectorSearchResult notSearchable(DetectorResult detectableResult) {
        return new DetectorSearchResult(detectableResult, null, Collections.emptyList());
    }

    public static DetectorSearchResult found(DetectorSearchEntryPointResult found, @NotNull List<DetectorSearchEntryPointResult> notFoundEntryPoints) {
        return new DetectorSearchResult(null, found, notFoundEntryPoints);
    }

    public static DetectorSearchResult notFound(@NotNull List<DetectorSearchEntryPointResult> notFoundEntryPoints) {
        return new DetectorSearchResult(null, null, notFoundEntryPoints);
    }

    public Optional<DetectorResult> getNotSearchableResult() {
        return Optional.ofNullable(notSearchable);
    }

    public boolean wasFound() {
        return foundEntryPoint != null;
    }

    public Optional<DetectorSearchEntryPointResult> getFoundEntryPoint() {
        return Optional.ofNullable(foundEntryPoint);
    }

    public @NotNull List<DetectorSearchEntryPointResult> getNotFoundEntryPoints() {
        return notFoundEntryPoints;
    }
}

