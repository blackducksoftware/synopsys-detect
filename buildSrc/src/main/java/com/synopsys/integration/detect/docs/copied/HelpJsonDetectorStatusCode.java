/*
 * buildSrc
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.docs.copied;

public class HelpJsonDetectorStatusCode {
    private String statusCode = "";
    private String statusCodeDescription = "";

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(final String code) {
        this.statusCode = code;
    }

    public String getStatusCodeDescription() {
        return statusCodeDescription;
    }

    public void setStatusCodeDescription(final String description) {
        this.statusCodeDescription = description;
    }
}
