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

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisRelationship;
import com.synopsys.integration.polaris.common.api.PolarisRelationshipMultiple;
import com.synopsys.integration.polaris.common.api.PolarisRelationshipSingle;
import com.synopsys.integration.polaris.common.api.PolarisRelationships;

public class UserRelationships extends PolarisRelationships {
    @SerializedName("email-details")
    private PolarisRelationship emailDetails = null;
    @SerializedName("entitlements")
    private PolarisRelationship entitlements = null;
    @SerializedName("githublogins")
    private PolarisRelationship githublogins = null;
    @SerializedName("groups")
    private PolarisRelationshipMultiple groups = null;
    @SerializedName("microsoftlogins")
    private PolarisRelationship microsoftlogins = null;
    @SerializedName("organization")
    private PolarisRelationshipSingle organization = null;

    /**
     * Get emailDetails
     * @return emailDetails
     */
    public PolarisRelationship getEmailDetails() {
        return emailDetails;
    }

    public void setEmailDetails(final PolarisRelationship emailDetails) {
        this.emailDetails = emailDetails;
    }

    /**
     * Get entitlements
     * @return entitlements
     */
    public PolarisRelationship getEntitlements() {
        return entitlements;
    }

    public void setEntitlements(final PolarisRelationship entitlements) {
        this.entitlements = entitlements;
    }

    /**
     * Get githublogins
     * @return githublogins
     */
    public PolarisRelationship getGithublogins() {
        return githublogins;
    }

    public void setGithublogins(final PolarisRelationship githublogins) {
        this.githublogins = githublogins;
    }

    /**
     * Get groups
     * @return groups
     */
    public PolarisRelationshipMultiple getGroups() {
        return groups;
    }

    public void setGroups(final PolarisRelationshipMultiple groups) {
        this.groups = groups;
    }

    /**
     * Get microsoftlogins
     * @return microsoftlogins
     */
    public PolarisRelationship getMicrosoftlogins() {
        return microsoftlogins;
    }

    public void setMicrosoftlogins(final PolarisRelationship microsoftlogins) {
        this.microsoftlogins = microsoftlogins;
    }

    /**
     * Get organization
     * @return organization
     */
    public PolarisRelationshipSingle getOrganization() {
        return organization;
    }

    public void setOrganization(final PolarisRelationshipSingle organization) {
        this.organization = organization;
    }

}

