/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.codelocation;

import java.util.List;
import java.util.Map;

public class BdioCodeLocationResult {
    private final List<BdioCodeLocation> bdioCodeLocations;
    private final Map<DetectCodeLocation, String> codeLocationNames;

    public BdioCodeLocationResult(final List<BdioCodeLocation> bdioCodeLocations, final Map<DetectCodeLocation, String> codeLocationNames) {
        this.bdioCodeLocations = bdioCodeLocations;
        this.codeLocationNames = codeLocationNames;
    }

    public Map<DetectCodeLocation, String> getCodeLocationNames() {
        return codeLocationNames;
    }

    public List<BdioCodeLocation> getBdioCodeLocations() {
        return bdioCodeLocations;
    }
}
