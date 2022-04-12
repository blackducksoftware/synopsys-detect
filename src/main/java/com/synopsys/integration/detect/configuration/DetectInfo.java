package com.synopsys.integration.detect.configuration;

import com.synopsys.integration.util.OperatingSystemType;

public class DetectInfo {
    private final OperatingSystemType currentOs;
    private final String detectVersion;
    private final String buildDateString;

    public DetectInfo(String detectVersionText, OperatingSystemType currentOs, String buildDateString) {
        this.detectVersion = detectVersionText;
        this.currentOs = currentOs;
        this.buildDateString = buildDateString;
    }

    public String getDetectVersion() {
        return detectVersion;
    }

    public OperatingSystemType getCurrentOs() {
        return currentOs;
    }

    public String getBuildDateString() {
        return buildDateString;
    }
}
