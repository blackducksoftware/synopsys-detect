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
package com.synopsys.integration.polaris.common.api.common.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.annotations.SerializedName;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class MetaWithOrganizationTrash extends PolarisComponent {
    @SerializedName("in-trash")
    private Boolean inTrash = false;

    @SerializedName("etag")
    private String etag;

    @SerializedName("organization-id")
    private String organizationId;

    /**
     * &#x60;Mutable&#x60;, &#x60;Non-null&#x60;: indicates if this object is considered to be in the trash.  Note that all properties are immutable while this object is in the trash.
     * @return inTrash
     */
    public Boolean getInTrash() {
        return inTrash;
    }

    public void setInTrash(final Boolean inTrash) {
        this.inTrash = inTrash;
    }

    /**
     * &#x60;Automatic&#x60;: The ETag used to update this object at a later time.
     * @return etag
     */
    public String getEtag() {
        return etag;
    }

    public void setEtag(final String etag) {
        this.etag = etag;
    }

    /**
     * &#x60;Automatic&#x60;.  The organization-id to which this object belongs.
     * @return organizationId
     */
    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(final String organizationId) {
        this.organizationId = organizationId;
    }

}

