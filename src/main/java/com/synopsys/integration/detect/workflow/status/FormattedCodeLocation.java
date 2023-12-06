package com.synopsys.integration.detect.workflow.status;

import java.util.UUID;

/**
 * This class captures the data needed to populate the codeLocation array in the status.json file.
 */
public class FormattedCodeLocation {

    private final String codeLocationName;
    private final UUID scanId;
    private final String scanType;
    
    public FormattedCodeLocation(String codeLocationName, UUID scanId, String scanType) {
        this.codeLocationName = codeLocationName;
        this.scanId = scanId;
        this.scanType = scanType;
    }
    
    public String getCodeLocationName() {
        return codeLocationName;
    }

    public UUID getScanId() {
        return scanId;
    }

    public String getScanType() {
        return scanType;
    }
}
