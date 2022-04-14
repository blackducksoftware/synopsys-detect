package com.synopsys.integration.detectable.detectables.xcode.model;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.result.FailedDetectableResult;

public class XcodeWorkspaceResult {
    private final List<CodeLocation> codeLocations;
    private final List<FailedDetectableResult> failedDetectableResults;

    public static XcodeWorkspaceResult failure(List<FailedDetectableResult> failedDetectableResults) {
        return new XcodeWorkspaceResult(Collections.emptyList(), failedDetectableResults);
    }

    public static XcodeWorkspaceResult success(List<CodeLocation> codeLocations) {
        return new XcodeWorkspaceResult(codeLocations, Collections.emptyList());
    }

    private XcodeWorkspaceResult(List<CodeLocation> codeLocations, List<FailedDetectableResult> failedDetectableResults) {
        this.codeLocations = codeLocations;
        this.failedDetectableResults = failedDetectableResults;
    }

    public List<CodeLocation> getCodeLocations() {
        return codeLocations;
    }

    public List<FailedDetectableResult> getFailedDetectableResults() {
        return failedDetectableResults;
    }

    public boolean isFailure() {
        return CollectionUtils.isNotEmpty(getFailedDetectableResults());
    }
}
