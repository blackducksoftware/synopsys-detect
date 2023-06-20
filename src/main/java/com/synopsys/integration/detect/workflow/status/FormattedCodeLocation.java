package com.synopsys.integration.detect.workflow.status;

/**
 * This class captures the data needed to populate the codeLocation's array in the status.json 
 */
public class FormattedCodeLocation {

    private final String codeLocationName;
    private final String scanId;
    private final String scanType;
    
    public FormattedCodeLocation(String codeLocationName, String scanId, String scanType) {
        this.codeLocationName = codeLocationName;
        this.scanId = scanId;
        this.scanType = scanType;
    }
    
    public String getCodeLocationName() {
        return codeLocationName;
    }

    public String getScanId() {
        return scanId;
    }

    public String getScanType() {
        return scanType;
    }
}
