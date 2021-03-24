/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.codelocation;

import java.util.Set;

public class CodeLocationResults {
    private final Set<String> allCodeLocationNames;
    private final CodeLocationWaitData codeLocationWaitData;

    public CodeLocationResults(final Set<String> allCodeLocationNames, final CodeLocationWaitData codeLocationWaitData) {
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
