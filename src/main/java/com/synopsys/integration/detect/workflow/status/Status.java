/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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
