/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.report.output;

public class FormattedOperationOutput {
    public final String timestamp;
    public final String descriptionKey;
    public final String status;

    public FormattedOperationOutput(String timestamp, String descriptionKey, String status) {
        this.timestamp = timestamp;
        this.descriptionKey = descriptionKey;
        this.status = status;
    }
}
