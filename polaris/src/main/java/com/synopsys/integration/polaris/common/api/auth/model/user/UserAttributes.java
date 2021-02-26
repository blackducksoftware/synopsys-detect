/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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

