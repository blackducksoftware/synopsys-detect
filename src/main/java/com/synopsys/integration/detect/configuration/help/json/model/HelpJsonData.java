package com.synopsys.integration.detect.configuration.help.json.model;

import java.util.ArrayList;
import java.util.List;

public class HelpJsonData {
    private List<HelpJsonExitCode> exitCodes = new ArrayList<>();
    private List<HelpJsonDetectorRule> detectors = new ArrayList<>();
    private List<HelpJsonOption> options = new ArrayList<>();
    private List<HelpJsonDetectorStatusCode> detectorStatusCodes = new ArrayList<>();

    public List<HelpJsonExitCode> getExitCodes() {
        return exitCodes;
    }

    public void setExitCodes(List<HelpJsonExitCode> exitCodes) {
        this.exitCodes = exitCodes;
    }

    public List<HelpJsonOption> getOptions() {
        return options;
    }

    public void setOptions(List<HelpJsonOption> options) {
        this.options = options;
    }

    public List<HelpJsonDetectorStatusCode> getDetectorStatusCodes() {
        return detectorStatusCodes;
    }

    public void setDetectorStatusCodes(List<HelpJsonDetectorStatusCode> detectorStatusCodes) {
        this.detectorStatusCodes = detectorStatusCodes;
    }

    public List<HelpJsonDetectorRule> getDetectors() {
        return detectors;
    }

    public void setDetectors(List<HelpJsonDetectorRule> detectors) {
        this.detectors = detectors;
    }
}
