/*
 * buildSrc
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.docs.copied;

//Copied from detect-configuration
public class HelpJsonExitCode {
    private String exitCodeKey = "";
    private String exitCodeDescription = "";
    private Integer exitCodeValue = 0;

    public Integer getExitCodeValue() {
        return exitCodeValue;
    }

    public void setExitCodeValue(final Integer exitCodeValue) {
        this.exitCodeValue = exitCodeValue;
    }

    public String getExitCodeKey() {
        return exitCodeKey;
    }

    public void setExitCodeKey(final String exitCodeKey) {
        this.exitCodeKey = exitCodeKey;
    }

    public String getExitCodeDescription() {
        return exitCodeDescription;
    }

    public void setExitCodeDescription(final String exitCodeDescription) {
        this.exitCodeDescription = exitCodeDescription;
    }
}
