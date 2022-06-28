package com.synopsys.integration.detect.docs.copied;

public class HelpJsonOptionDeprecatedValue {
    private String value;
    private String reason;

    public HelpJsonOptionDeprecatedValue(String value, String reason) {
        this.value = value;
        this.reason = reason;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
