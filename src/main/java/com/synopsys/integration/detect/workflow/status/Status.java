package com.synopsys.integration.detect.workflow.status;

import com.synopsys.integration.detect.configuration.enumeration.DetectTool;

public class Status {
    private final String descriptionKey;
    private final StatusType statusType;

    public Status(String descriptionKey, StatusType statusType) {
        this.descriptionKey = descriptionKey;
        this.statusType = statusType;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    public static Status forTool(DetectTool tool, StatusType statusType) {
        return new Status(tool.toString(), statusType);
    }
}
