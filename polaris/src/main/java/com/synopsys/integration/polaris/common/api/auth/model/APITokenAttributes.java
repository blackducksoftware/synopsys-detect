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

import java.util.Objects;
import java.util.Arrays;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.IOException;
import java.time.OffsetDateTime;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class APITokenAttributes extends PolarisComponent {
    @SerializedName("access-token")
    private String accessToken;

    @SerializedName("date-created")
    private OffsetDateTime dateCreated;

    @SerializedName("jwt")
    private String jwt;

    @SerializedName("name")
    private String name;

    @SerializedName("revoked")
    private Boolean revoked;

    /**
     * Get accessToken
     * @return accessToken
     */
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Get dateCreated
     * @return dateCreated
     */
    public OffsetDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(final OffsetDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * Get jwt
     * @return jwt
     */
    public String getJwt() {
        return jwt;
    }

    public void setJwt(final String jwt) {
        this.jwt = jwt;
    }

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
     * Get revoked
     * @return revoked
     */
    public Boolean getRevoked() {
        return revoked;
    }

    public void setRevoked(final Boolean revoked) {
        this.revoked = revoked;
    }

}

