package com.synopsys.integration.detect.workflow.blackduck.codelocation;

import java.util.Set;

public class CodeLocationResults {
    private final Set<String> allCodeLocationNames;
    private final CodeLocationWaitData codeLocationWaitData;

    public CodeLocationResults(Set<String> allCodeLocationNames, CodeLocationWaitData codeLocationWaitData) {
        this.allCodeLocationNames = allCodeLocationNames;
        this.codeLocationWaitData = codeLocationWaitData;
    }

    public Set<String> getAllCodeLocationNames() {
        return allCodeLocationNames;
    }

    public CodeLocationWaitData getCodeLocationWaitData() {
        return codeLocationWaitData;
    }
}
