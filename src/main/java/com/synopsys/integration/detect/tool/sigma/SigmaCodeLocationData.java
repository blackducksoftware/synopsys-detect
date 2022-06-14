package com.synopsys.integration.detect.tool.sigma;

import java.util.Set;

public class SigmaCodeLocationData {
    private final Set<String> codeLocationNames;

    public SigmaCodeLocationData(Set<String> codeLocationNames) {
        this.codeLocationNames = codeLocationNames;
    }

    public Set<String> getCodeLocationNames() {
        return codeLocationNames;
    }
}
