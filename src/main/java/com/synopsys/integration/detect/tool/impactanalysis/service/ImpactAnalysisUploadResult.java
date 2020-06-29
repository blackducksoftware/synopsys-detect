package com.synopsys.integration.detect.tool.impactanalysis.service;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class ImpactAnalysisUploadResult {
    @Nullable
    private final ImpactAnalysisSuccessResult impactAnalysisSuccessResult;

    @Nullable
    private final ImpactAnalysisErrorResult impactAnalysisErrorResult;

    public ImpactAnalysisUploadResult(@Nullable ImpactAnalysisSuccessResult impactAnalysisSuccessResult, @Nullable ImpactAnalysisErrorResult impactAnalysisErrorResult) {
        this.impactAnalysisSuccessResult = impactAnalysisSuccessResult;
        this.impactAnalysisErrorResult = impactAnalysisErrorResult;
    }

    public Optional<ImpactAnalysisSuccessResult> getImpactAnalysisSuccessResult() {
        return Optional.ofNullable(impactAnalysisSuccessResult);
    }

    public Optional<ImpactAnalysisErrorResult> getImpactAnalysisErrorResult() {
        return Optional.ofNullable(impactAnalysisErrorResult);
    }
}
