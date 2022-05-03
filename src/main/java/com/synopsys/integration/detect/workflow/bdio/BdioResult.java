package com.synopsys.integration.detect.workflow.bdio;

import java.util.List;

import com.synopsys.integration.blackduck.codelocation.upload.UploadTarget;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocationNamesResult;

public class BdioResult {
    private final List<UploadTarget> uploadTargets;
    private final DetectCodeLocationNamesResult codeLocationNamesResult;

    public BdioResult(List<UploadTarget> uploadTargets, DetectCodeLocationNamesResult codeLocationNamesResult) {
        this.uploadTargets = uploadTargets;
        this.codeLocationNamesResult = codeLocationNamesResult;
    }

    public List<UploadTarget> getUploadTargets() {
        return uploadTargets;
    }

    public DetectCodeLocationNamesResult getCodeLocationNamesResult() {
        return codeLocationNamesResult;
    }
}
