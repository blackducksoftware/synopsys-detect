/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.codelocation;

import java.util.Map;

public class DetectCodeLocationNamesResult {
    private final Map<DetectCodeLocation, String> codeLocationNames;

    public DetectCodeLocationNamesResult(final Map<DetectCodeLocation, String> codeLocationNames) {
        this.codeLocationNames = codeLocationNames;
    }

    public Map<DetectCodeLocation, String> getCodeLocationNames() {
        return codeLocationNames;
    }
}
