/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.codelocation;

public class BdioCodeLocation {
    private final String codeLocationName;
    private final String bdioName;
    private final DetectCodeLocation detectCodeLocation;

    public BdioCodeLocation(final DetectCodeLocation detectCodeLocation, String codeLocationName, final String bdioName) {
        this.codeLocationName = codeLocationName;
        this.bdioName = bdioName;
        this.detectCodeLocation = detectCodeLocation;
    }

    public String getCodeLocationName() {
        return codeLocationName;
    }

    public String getBdioName() {
        return bdioName;
    }

    public DetectCodeLocation getDetectCodeLocation() {
        return detectCodeLocation;
    }
}
