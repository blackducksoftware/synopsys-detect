/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.report.output;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class FormattedResultOutput {
    @SerializedName("location")
    public String location;

    @SerializedName("message")
    public String message;

    @SerializedName("sub_messages")
    public List<String> subMessages;

    public FormattedResultOutput(String location, String message, List<String> subMessages) {
        this.location = location;
        this.message = message;
        this.subMessages = subMessages;
    }
}
