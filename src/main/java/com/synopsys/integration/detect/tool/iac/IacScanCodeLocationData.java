package com.synopsys.integration.detect.tool.iac;

import java.util.Set;

public class IacScanCodeLocationData {
    private final Set<String> codeLocationNames;

    public IacScanCodeLocationData(Set<String> codeLocationNames) {
        this.codeLocationNames = codeLocationNames;
    }

    public Set<String> getCodeLocationNames() {
        return codeLocationNames;
    }
}
