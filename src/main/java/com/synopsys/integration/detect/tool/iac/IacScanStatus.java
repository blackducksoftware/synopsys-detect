package com.synopsys.integration.detect.tool.iac;

import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detect.workflow.status.StatusType;

public class IacScanStatus extends Status {
    public IacScanStatus(String scanTarget, StatusType statusType) {
        super(String.format("IaC scan on %s", scanTarget), statusType);
    }
}
