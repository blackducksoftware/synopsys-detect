/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.configuration;

public enum DetectPropertyFromVersion {
    VERSION_3_0_0("3.0.0"),
    VERSION_3_1_0("3.1.0"),
    VERSION_3_2_0("3.2.0"),
    VERSION_4_0_0("4.0.0"),
    VERSION_4_1_0("4.1.0"),
    VERSION_4_2_0("4.2.0"),
    VERSION_4_3_0("4.3.0"),
    VERSION_4_4_0("4.4.0"),
    VERSION_5_0_0("5.0.0"),
    VERSION_5_2_0("5.2.0"),
    VERSION_5_3_0("5.3.0"),
    VERSION_5_4_0("5.4.0"),
    VERSION_5_5_0("5.5.0"),
    VERSION_5_6_0("5.6.0"),
    VERSION_6_0_0("6.0.0"),
    VERSION_6_1_0("6.1.0"),
    VERSION_6_2_0("6.2.0"),
    VERSION_6_4_0("6.4.0"),
    VERSION_6_5_0("6.5.0"),
    VERSION_6_8_0("6.8.0"),
    VERSION_6_9_0("6.9.0");

    private final String version;

    DetectPropertyFromVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
