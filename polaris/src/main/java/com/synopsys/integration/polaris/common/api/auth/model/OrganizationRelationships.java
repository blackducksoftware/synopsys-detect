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

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisComponent;
import com.synopsys.integration.polaris.common.api.common.model.ToManyRelationship;
import com.synopsys.integration.polaris.common.api.common.model.ToOneRelationship;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class OrganizationRelationships extends PolarisComponent {
    @SerializedName("activeLicense")
    private ToOneRelationship activeLicense = null;

    @SerializedName("groups")
    private ToManyRelationship groups = null;

    @SerializedName("licenseViolations")
    private ToManyRelationship licenseViolations = null;

    @SerializedName("licenses")
    private ToManyRelationship licenses = null;

    @SerializedName("owners")
    private ToManyRelationship owners = null;

    @SerializedName("role-assignments")
    private ToManyRelationship roleAssignments = null;

    @SerializedName("users")
    private ToManyRelationship users = null;

    /**
     * Get activeLicense
     * @return activeLicense
     */
    public ToOneRelationship getActiveLicense() {
        return activeLicense;
    }

    public void setActiveLicense(final ToOneRelationship activeLicense) {
        this.activeLicense = activeLicense;
    }

    /**
     * Get groups
     * @return groups
     */
    public ToManyRelationship getGroups() {
        return groups;
    }

    public void setGroups(final ToManyRelationship groups) {
        this.groups = groups;
    }

    /**
     * Get licenseViolations
     * @return licenseViolations
     */
    public ToManyRelationship getLicenseViolations() {
        return licenseViolations;
    }

    public void setLicenseViolations(final ToManyRelationship licenseViolations) {
        this.licenseViolations = licenseViolations;
    }

    /**
     * Get licenses
     * @return licenses
     */
    public ToManyRelationship getLicenses() {
        return licenses;
    }

    public void setLicenses(final ToManyRelationship licenses) {
        this.licenses = licenses;
    }

    /**
     * Get owners
     * @return owners
     */
    public ToManyRelationship getOwners() {
        return owners;
    }

    public void setOwners(final ToManyRelationship owners) {
        this.owners = owners;
    }

    /**
     * Get roleAssignments
     * @return roleAssignments
     */
    public ToManyRelationship getRoleAssignments() {
        return roleAssignments;
    }

    public void setRoleAssignments(final ToManyRelationship roleAssignments) {
        this.roleAssignments = roleAssignments;
    }

    /**
     * Get users
     * @return users
     */
    public ToManyRelationship getUsers() {
        return users;
    }

    public void setUsers(final ToManyRelationship users) {
        this.users = users;
    }

}

