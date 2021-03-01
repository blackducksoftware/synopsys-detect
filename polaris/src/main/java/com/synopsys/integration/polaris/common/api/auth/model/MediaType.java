/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class MediaType extends PolarisComponent {
    @SerializedName("type")
    private String type;

    @SerializedName("subtype")
    private String subtype;

    @SerializedName("parameters")
    private Map<String, String> parameters = null;

    @SerializedName("wildcardType")
    private Boolean wildcardType;

    @SerializedName("wildcardSubtype")
    private Boolean wildcardSubtype;

    /**
     * Get type
     * @return type
     */
    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    /**
     * Get subtype
     * @return subtype
     */
    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(final String subtype) {
        this.subtype = subtype;
    }

    public MediaType putParametersItem(final String key, final String parametersItem) {
        if (this.parameters == null) {
            this.parameters = new HashMap<>();
        }
        this.parameters.put(key, parametersItem);
        return this;
    }

    /**
     * Get parameters
     * @return parameters
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(final Map<String, String> parameters) {
        this.parameters = parameters;
    }

    /**
     * Get wildcardType
     * @return wildcardType
     */
    public Boolean getWildcardType() {
        return wildcardType;
    }

    public void setWildcardType(final Boolean wildcardType) {
        this.wildcardType = wildcardType;
    }

    /**
     * Get wildcardSubtype
     * @return wildcardSubtype
     */
    public Boolean getWildcardSubtype() {
        return wildcardSubtype;
    }

    public void setWildcardSubtype(final Boolean wildcardSubtype) {
        this.wildcardSubtype = wildcardSubtype;
    }

}

