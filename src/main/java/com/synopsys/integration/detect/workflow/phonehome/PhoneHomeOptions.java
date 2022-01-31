package com.synopsys.integration.detect.workflow.phonehome;

import java.util.Map;

public class PhoneHomeOptions {
    private final Map<String, String> passthrough;

    public PhoneHomeOptions(Map<String, String> passthrough) {
        this.passthrough = passthrough;
    }

    public Map<String, String> getPassthrough() {
        return passthrough;
    }
}