package com.synopsys.integration.detect.configuration;

import com.synopsys.integration.configuration.property.PropertyVersion;

public enum DetectPropertyFromVersion implements PropertyVersion {
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
    VERSION_6_9_0("6.9.0"),
    VERSION_7_0_0("7.0.0"),
    VERSION_7_1_0("7.1.0"),
    VERSION_7_5_0("7.5.0"),
    VERSION_7_6_0("7.6.0"),
    VERSION_7_7_0("7.7.0"),
    VERSION_7_8_0("7.8.0"),
    VERSION_7_9_0("7.9.0"),
    VERSION_7_10_0("7.10.0"),
    VERSION_7_11_0("7.11.0"),
    VERSION_7_12_0("7.12.0"),
    VERSION_7_12_1("7.12.1"),
    VERSION_7_13_0("7.13.0"),
    VERSION_7_14_0("7.14.0"),
    VERSION_8_0_0("8.0.0"),
    VERSION_8_1_0("8.1.0"), 
    VERSION_8_2_0("8.2.0"),
    VERSION_8_3_0("8.3.0");

    private final String version;

    DetectPropertyFromVersion(String version) {
        this.version = version;
    }

    @Override
    public String getVersion() {
        return version;
    }
}
