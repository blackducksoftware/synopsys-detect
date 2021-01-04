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
package com.synopsys.integration.polaris.common.api.auth.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.annotations.SerializedName;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class ErrorResponse extends PolarisComponent {
    @SerializedName("jsonPath")
    private JsonPath jsonPath = null;

    @SerializedName("linksInformation")
    private LinksInformation linksInformation = null;

    @SerializedName("metaInformation")
    private MetaInformation metaInformation = null;

    @SerializedName("queryParams")
    private QueryParams queryParams = null;

    @SerializedName("errors")
    private IterableErrorData errors = null;

    /**
     * Get jsonPath
     * @return jsonPath
     */
    public JsonPath getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(final JsonPath jsonPath) {
        this.jsonPath = jsonPath;
    }

    /**
     * Get linksInformation
     * @return linksInformation
     */
    public LinksInformation getLinksInformation() {
        return linksInformation;
    }

    public void setLinksInformation(final LinksInformation linksInformation) {
        this.linksInformation = linksInformation;
    }

    /**
     * Get metaInformation
     * @return metaInformation
     */
    public MetaInformation getMetaInformation() {
        return metaInformation;
    }

    public void setMetaInformation(final MetaInformation metaInformation) {
        this.metaInformation = metaInformation;
    }

    /**
     * Get queryParams
     * @return queryParams
     */
    public QueryParams getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(final QueryParams queryParams) {
        this.queryParams = queryParams;
    }

    /**
     * Get errors
     * @return errors
     */
    public IterableErrorData getErrors() {
        return errors;
    }

    public void setErrors(final IterableErrorData errors) {
        this.errors = errors;
    }

}

