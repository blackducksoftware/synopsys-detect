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

import java.util.ArrayList;
import java.util.List;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class ErrorResource extends PolarisComponent {
    @SerializedName("errors")
    private List<Error> errors = new ArrayList<>();

    public ErrorResource addErrorsItem(final Error errorsItem) {
        this.errors.add(errorsItem);
        return this;
    }

    /**
     * Get errors
     * @return errors
     */
    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(final List<Error> errors) {
        this.errors = errors;
    }

}

