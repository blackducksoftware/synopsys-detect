package com.synopsys.integration.detect.configuration;

import com.synopsys.integration.util.OperatingSystemType;

public class DetectInfo {
    private final OperatingSystemType currentOs;
    private final String detectVersion;

    public DetectInfo(String detectVersionText, OperatingSystemType currentOs) {
        this.detectVersion = detectVersionText;
        this.currentOs = currentOs;
    }

    public String getDetectVersion() {
        return detectVersion;
    }

    public OperatingSystemType getCurrentOs() {
        return currentOs;
    }

}
