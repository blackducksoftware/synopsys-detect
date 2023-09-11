package com.synopsys.integration.detect.workflow.bdio;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.synopsys.integration.blackduck.codelocation.upload.UploadTarget;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocationNamesResult;
import com.synopsys.integration.detector.base.DetectorType;

public class BdioResult {
    private final List<UploadTarget> uploadTargets;
    private final DetectCodeLocationNamesResult codeLocationNamesResult;
    private final Set<DetectorType> applicableDetectorTypes;

    public static BdioResult none() {
        DetectCodeLocationNamesResult emptyNamesResult = new DetectCodeLocationNamesResult(Collections.emptyMap());
        return new BdioResult(Collections.emptyList(), emptyNamesResult, Collections.emptySet());
    }

    public BdioResult(List<UploadTarget> uploadTargets, DetectCodeLocationNamesResult codeLocationNamesResult, Set<DetectorType> applicableDetectorTypes) {
        this.uploadTargets = uploadTargets;
        this.codeLocationNamesResult = codeLocationNamesResult;
        this.applicableDetectorTypes = applicableDetectorTypes;
    }

    public List<UploadTarget> getUploadTargets() {
        return uploadTargets;
    }

    public DetectCodeLocationNamesResult getCodeLocationNamesResult() {
        return codeLocationNamesResult;
    }

    public boolean isNotEmpty() {
        return !uploadTargets.isEmpty();
    }

    public Set<DetectorType> getApplicableDetectorTypes() {
        return applicableDetectorTypes;
    }
}
