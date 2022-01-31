package com.synopsys.integration.detect.workflow.bdio;

import java.util.List;

import com.synopsys.integration.blackduck.codelocation.upload.UploadTarget;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocationNamesResult;

public class BdioResult {
    private final List<UploadTarget> uploadTargets;
    private final DetectCodeLocationNamesResult codeLocationNamesResult;
    private final boolean isBdio2;

    public BdioResult(List<UploadTarget> uploadTargets, DetectCodeLocationNamesResult codeLocationNamesResult, boolean isBdio2) {
        this.uploadTargets = uploadTargets;
        this.codeLocationNamesResult = codeLocationNamesResult;
        this.isBdio2 = isBdio2;
    }

    public List<UploadTarget> getUploadTargets() {
        return uploadTargets;
    }

    public boolean isBdio2() {
        return isBdio2;
    }

    public DetectCodeLocationNamesResult getCodeLocationNamesResult() {
        return codeLocationNamesResult;
    }
}
