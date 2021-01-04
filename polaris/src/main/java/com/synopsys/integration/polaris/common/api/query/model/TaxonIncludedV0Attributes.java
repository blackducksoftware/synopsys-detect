/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.polaris.common.api.query.model;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisComponent;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class TaxonIncludedV0Attributes extends PolarisComponent {
    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("abbreviation")
    private String abbreviation;

    @SerializedName("extra")
    private Map<String, String> extra = null;

    /**
     * Localized name of this taxon.
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /**
     * &#x60;Nullable&#x60;.  Localized description of this taxon.
     * @return description
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * &#x60;Nullable&#x60;.  Localized abbreviation of this taxon.
     * @return abbreviation
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(final String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public TaxonIncludedV0Attributes putExtraItem(final String key, final String extraItem) {
        if (this.extra == null) {
            this.extra = new HashMap<>();
        }
        this.extra.put(key, extraItem);
        return this;
    }

    /**
     * Get extra
     * @return extra
     */
    public Map<String, String> getExtra() {
        return extra;
    }

    public void setExtra(final Map<String, String> extra) {
        this.extra = extra;
    }

}

