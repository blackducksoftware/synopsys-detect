/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.query.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisComponent;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class FilterKeyV0Attributes extends PolarisComponent {
    @SerializedName("name")
    private String name;

    @SerializedName("key")
    private String key;

    @SerializedName("value-type")
    private String valueType;

    @SerializedName("apis")
    private List<String> apis = null;

    /**
     * The localized name of this filter key
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /**
     * The text key to use in query parameters
     * @return key
     */
    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    /**
     * Describes the type of filter key values, and how to obtain them. The types List &amp; Dynamic indicate that these values can be fetched using &#39;/&lt;keyid&gt;/values&#39; endpoint while the types String, Date, DateTime, and Number indicate that the filter value takes a free-form text input of that type.
     * @return valueType
     */
    public String getValueType() {
        return valueType;
    }

    public void setValueType(final String valueType) {
        this.valueType = valueType;
    }

    public FilterKeyV0Attributes addApisItem(final String apisItem) {
        if (this.apis == null) {
            this.apis = new ArrayList<>();
        }
        this.apis.add(apisItem);
        return this;
    }

    /**
     * Get apis
     * @return apis
     */
    public List<String> getApis() {
        return apis;
    }

    public void setApis(final List<String> apis) {
        this.apis = apis;
    }

}

