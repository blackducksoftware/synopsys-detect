/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.report.output;

import com.google.gson.annotations.SerializedName;

public class FormattedResultOutput {
    @SerializedName("location")
    public String location;

    @SerializedName("message")
    public String message;

    public FormattedResultOutput(final String location, final String message) {
        this.location = location;
        this.message = message;
    }
}
