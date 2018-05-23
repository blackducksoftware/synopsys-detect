package com.blackducksoftware.integration.hub.detect.model;

public class ProcessedDetectCodeLocation {
    public String codeLocationName;
    public String bdioName;
    public DetectCodeLocation codeLocation;

    public ProcessedDetectCodeLocation(final DetectCodeLocation codeLocation, final String codeLocationName, final String bdioName) {
        this.codeLocation = codeLocation;
        this.codeLocationName = codeLocationName;
        this.bdioName = bdioName;
    }
}
