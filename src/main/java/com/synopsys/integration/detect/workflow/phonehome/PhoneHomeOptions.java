/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.phonehome;

import java.util.Map;

public class PhoneHomeOptions {
    private final Map<String, String> passthrough;

    public PhoneHomeOptions(Map<String, String> passthrough) {
        this.passthrough = passthrough;
    }

    public Map<String, String> getPassthrough() {
        return passthrough;
    }
}