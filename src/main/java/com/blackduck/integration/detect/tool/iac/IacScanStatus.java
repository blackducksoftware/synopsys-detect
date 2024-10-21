package com.blackduck.integration.detect.tool.iac;

import com.blackduck.integration.detect.workflow.status.Status;
import com.blackduck.integration.detect.workflow.status.StatusType;

public class IacScanStatus extends Status {
    public IacScanStatus(String scanTarget, StatusType statusType) {
        super(String.format("IaC scan on %s", scanTarget), statusType);
    }
}
