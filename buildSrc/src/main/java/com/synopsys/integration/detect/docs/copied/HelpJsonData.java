package com.synopsys.integration.detect.docs.copied;

import java.util.ArrayList;
import java.util.List;

public class HelpJsonData {
    private List<HelpJsonExitCode> exitCodes = new ArrayList<>();
    private List<HelpJsonDetector> buildDetectors = new ArrayList<>();
    private List<HelpJsonDetector> buildlessDetectors = new ArrayList<>();
    private List<HelpJsonDetectorRule> detectors = new ArrayList<>();
    private List<HelpJsonOption> options = new ArrayList<>();
    private List<HelpJsonDetectorStatusCode> detectorStatusCodes = new ArrayList<>();

    public List<HelpJsonExitCode> getExitCodes() {
        return exitCodes;
    }

    public void setExitCodes(List<HelpJsonExitCode> exitCodes) {
        this.exitCodes = exitCodes;
    }

    public List<HelpJsonDetector> getBuildDetectors() {
        return buildDetectors;
    }

    public void setBuildDetectors(List<HelpJsonDetector> buildDetectors) {
        this.buildDetectors = buildDetectors;
    }

    public List<HelpJsonDetector> getBuildlessDetectors() {
        return buildlessDetectors;
    }

    public void setBuildlessDetectors(List<HelpJsonDetector> buildlessDetectors) {
        this.buildlessDetectors = buildlessDetectors;
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
