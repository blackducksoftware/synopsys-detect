/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.configuration;

import com.synopsys.integration.util.OperatingSystemType;

public class DetectInfo {
    private final OperatingSystemType currentOs;
    private final String detectVersion;
    private final int majorVersion;

    public DetectInfo(String detectVersionText, int majorVersion, OperatingSystemType currentOs) {
        this.detectVersion = detectVersionText;
        this.currentOs = currentOs;
        this.majorVersion = majorVersion;
    }

    public int getDetectMajorVersion() {
        return majorVersion;
    }

    public String getDetectVersion() {
        return detectVersion;
    }

    public OperatingSystemType getCurrentOs() {
        return currentOs;
    }

}
