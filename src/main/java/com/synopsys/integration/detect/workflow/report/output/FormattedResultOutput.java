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
