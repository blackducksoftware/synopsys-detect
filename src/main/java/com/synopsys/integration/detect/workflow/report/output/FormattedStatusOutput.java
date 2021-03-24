/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.report.output;

public class FormattedStatusOutput {
    public final String key;
    public final String status;

    public FormattedStatusOutput(final String descriptionKey, final String status) {
        this.key = descriptionKey;
        this.status = status;
    }
}
