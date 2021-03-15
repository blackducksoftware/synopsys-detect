/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.annotations.SerializedName;

import java.time.OffsetDateTime;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class OrganizationAttributes extends PolarisComponent {
    @SerializedName("date-created")
    private OffsetDateTime dateCreated;

    @SerializedName("description")
    private String description;

    @SerializedName("github-authentication")
    private GitHubAuthentication githubAuthentication = null;

    @SerializedName("microsoft-authentication")
    private MicrosoftAuthentication microsoftAuthentication = null;

    @SerializedName("organizationname")
    private String organizationname;

    @SerializedName("password-authentication")
    private PasswordAuthentication passwordAuthentication = null;

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
     * Get description
     * @return description
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Get githubAuthentication
     * @return githubAuthentication
     */
    public GitHubAuthentication getGithubAuthentication() {
        return githubAuthentication;
    }

    public void setGithubAuthentication(final GitHubAuthentication githubAuthentication) {
        this.githubAuthentication = githubAuthentication;
    }

    /**
     * Get microsoftAuthentication
     * @return microsoftAuthentication
     */
    public MicrosoftAuthentication getMicrosoftAuthentication() {
        return microsoftAuthentication;
    }

    public void setMicrosoftAuthentication(final MicrosoftAuthentication microsoftAuthentication) {
        this.microsoftAuthentication = microsoftAuthentication;
    }

    /**
     * Get organizationname
     * @return organizationname
     */
    public String getOrganizationname() {
        return organizationname;
    }

    public void setOrganizationname(final String organizationname) {
        this.organizationname = organizationname;
    }

    /**
     * Get passwordAuthentication
     * @return passwordAuthentication
     */
    public PasswordAuthentication getPasswordAuthentication() {
        return passwordAuthentication;
    }

    public void setPasswordAuthentication(final PasswordAuthentication passwordAuthentication) {
        this.passwordAuthentication = passwordAuthentication;
    }

}

