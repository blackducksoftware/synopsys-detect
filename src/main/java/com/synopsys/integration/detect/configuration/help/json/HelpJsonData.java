/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.configuration.help.json;

import java.util.ArrayList;
import java.util.List;

public class HelpJsonData {
    private List<HelpJsonExitCode> exitCodes = new ArrayList<>();
    private List<HelpJsonDetector> buildDetectors = new ArrayList<>();
    private List<HelpJsonDetector> buildlessDetectors = new ArrayList<>();
    private List<HelpJsonOption> options = new ArrayList<>();
    private List<HelpJsonDetectorStatusCode> detectorStatusCodes = new ArrayList<>();

    public List<HelpJsonExitCode> getExitCodes() {
        return exitCodes;
    }

    public void setExitCodes(final List<HelpJsonExitCode> exitCodes) {
        this.exitCodes = exitCodes;
    }

    public List<HelpJsonDetector> getBuildDetectors() {
        return buildDetectors;
    }

    public void setBuildDetectors(final List<HelpJsonDetector> buildDetectors) {
        this.buildDetectors = buildDetectors;
    }

    public List<HelpJsonDetector> getBuildlessDetectors() {
        return buildlessDetectors;
    }

    public void setBuildlessDetectors(final List<HelpJsonDetector> buildlessDetectors) {
        this.buildlessDetectors = buildlessDetectors;
    }

    public List<HelpJsonOption> getOptions() {
        return options;
    }

    public void setOptions(final List<HelpJsonOption> options) {
        this.options = options;
    }

    public List<HelpJsonDetectorStatusCode> getDetectorStatusCodes() {
        return detectorStatusCodes;
    }

    public void setDetectorStatusCodes(final List<HelpJsonDetectorStatusCode> detectorStatusCodes) {
        this.detectorStatusCodes = detectorStatusCodes;
    }
}
