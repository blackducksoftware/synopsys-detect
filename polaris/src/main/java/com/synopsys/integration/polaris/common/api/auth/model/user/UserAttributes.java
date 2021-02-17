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
package com.synopsys.integration.polaris.common.api.auth.model.user;

import com.synopsys.integration.polaris.common.api.PolarisAttributes;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.auth.model.SetPassword;

public class UserAttributes extends PolarisAttributes {
    @SerializedName("owner")
    private Boolean owner;
    @SerializedName("system")
    private Boolean system;
    @SerializedName("first-time")
    private Boolean firstTime;
    @SerializedName("name")
    private String name;
    @SerializedName("username")
    private String username;
    @SerializedName("email")
    private String email;
    @SerializedName("enabled")
    private Boolean enabled;
    @SerializedName("password-login")
    private SetPassword passwordLogin = null;

    /**
     * Get email
     * @return email
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * Get enabled
     * @return enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(final Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Get firstTime
     * @return firstTime
     */
    public Boolean getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(final Boolean firstTime) {
        this.firstTime = firstTime;
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
     * Get owner
     * @return owner
     */
    public Boolean getOwner() {
        return owner;
    }

    public void setOwner(final Boolean owner) {
        this.owner = owner;
    }

    /**
     * Get passwordLogin
     * @return passwordLogin
     */
    public SetPassword getPasswordLogin() {
        return passwordLogin;
    }

    public void setPasswordLogin(final SetPassword passwordLogin) {
        this.passwordLogin = passwordLogin;
    }

    /**
     * Get system
     * @return system
     */
    public Boolean getSystem() {
        return system;
    }

    public void setSystem(final Boolean system) {
        this.system = system;
    }

    /**
     * Get username
     * @return username
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

}

