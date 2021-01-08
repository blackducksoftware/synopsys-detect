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

import java.time.OffsetDateTime;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class NewCookie extends PolarisComponent {
    @SerializedName("name")
    private String name;

    @SerializedName("value")
    private String value;

    @SerializedName("version")
    private Integer version;

    @SerializedName("path")
    private String path;

    @SerializedName("domain")
    private String domain;

    @SerializedName("comment")
    private String comment;

    @SerializedName("maxAge")
    private Integer maxAge;

    @SerializedName("expiry")
    private OffsetDateTime expiry;

    @SerializedName("secure")
    private Boolean secure;

    @SerializedName("httpOnly")
    private Boolean httpOnly;

    /**
     * Get name
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get value
     * @return value
     */
    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * Get version
     * @return version
     */
    public Integer getVersion() {
        return version;
    }

    public void setVersion(final Integer version) {
        this.version = version;
    }

    /**
     * Get path
     * @return path
     */
    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    /**
     * Get domain
     * @return domain
     */
    public String getDomain() {
        return domain;
    }

    public void setDomain(final String domain) {
        this.domain = domain;
    }

    /**
     * Get comment
     * @return comment
     */
    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    /**
     * Get maxAge
     * @return maxAge
     */
    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(final Integer maxAge) {
        this.maxAge = maxAge;
    }

    /**
     * Get expiry
     * @return expiry
     */
    public OffsetDateTime getExpiry() {
        return expiry;
    }

    public void setExpiry(final OffsetDateTime expiry) {
        this.expiry = expiry;
    }

    /**
     * Get secure
     * @return secure
     */
    public Boolean getSecure() {
        return secure;
    }

    public void setSecure(final Boolean secure) {
        this.secure = secure;
    }

    /**
     * Get httpOnly
     * @return httpOnly
     */
    public Boolean getHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(final Boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

}

