package com.synopsys.integration.detect.workflow.codelocation;

public class BdioCodeLocation {
    private final String codeLocationName;
    private final String bdioName;
    private final DetectCodeLocation detectCodeLocation;

    public BdioCodeLocation(DetectCodeLocation detectCodeLocation, String codeLocationName, String bdioName) {
        this.codeLocationName = codeLocationName;
        this.bdioName = bdioName;
        this.detectCodeLocation = detectCodeLocation;
    }

    public String getCodeLocationName() {
        return codeLocationName;
    }

    public String getBdioName() {
        return bdioName;
    }

    public DetectCodeLocation getDetectCodeLocation() {
        return detectCodeLocation;
    }
}
