/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.common.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.annotations.SerializedName;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class Error extends PolarisComponent {
    @SerializedName("code")
    private String code;

    @SerializedName("title")
    private String title;

    @SerializedName("detail")
    private String detail;

    /**
     * The Pericles Code categorizing the problem.  Codes correspond 1-to-1 with Titles.
     * @return code
     */
    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    /**
     * The Pericles Title (human readable) categorizing the problem.  Titles correspond 1-to-1 with Codes.
     * @return title
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * A human readable explanation of the problem that will vary depending on the exact request.  When present, this will describe the exact problem with the request (ie: field &#x60;abc&#x60; must be of type int, but string was found).
     * @return detail
     */
    public String getDetail() {
        return detail;
    }

    public void setDetail(final String detail) {
        this.detail = detail;
    }

}

