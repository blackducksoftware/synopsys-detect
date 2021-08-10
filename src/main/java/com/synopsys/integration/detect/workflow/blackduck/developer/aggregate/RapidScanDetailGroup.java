/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.developer.aggregate;

public enum RapidScanDetailGroup {
    LICENSE("License"),
    POLICY("Components"),
    SECURITY("Security");

    private String displayName;

    RapidScanDetailGroup(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
