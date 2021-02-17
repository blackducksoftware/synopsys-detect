/*
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

public class OAuthTokenResponse extends PolarisComponent {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("expires_in")
    private Long expiresIn;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("scope")
    private String scope;

    @SerializedName("user_id")
    private String userId;

    /**
     * Get accessToken
     * @return accessToken
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Get tokenType
     * @return tokenType
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     * Get expiresIn
     * @return expiresIn
     */
    public Long getExpiresIn() {
        return expiresIn;
    }

    /**
     * Get refreshToken
     * @return refreshToken
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Get scope
     * @return scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * Get userId
     * @return userId
     */
    public String getUserId() {
        return userId;
    }

}

