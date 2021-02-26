/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.report.output;

import com.google.gson.annotations.SerializedName;

public class FormattedExitCodeOutput {
    @SerializedName("exitCode")
    public Integer exitCode = 0;

    @SerializedName("exitCodeDescription")
    public String exitCodeDescription = "";

    @SerializedName("exitCodeReason")
    public String exitCodeReason = "";

    public FormattedExitCodeOutput(final Integer exitCode, final String exitCodeDescription, final String exitCodeReason) {
        this.exitCode = exitCode;
        this.exitCodeDescription = exitCodeDescription;
        this.exitCodeReason = exitCodeReason;
    }
}
