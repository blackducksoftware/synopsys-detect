package com.synopsys.integration.detect.tool.sigma;

import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detect.workflow.status.StatusType;

public class SigmaStatus extends Status {
    public SigmaStatus(String scanTarget, StatusType statusType) {
        super(String.format("Sigma scan on %s", scanTarget), statusType);
    }
}
