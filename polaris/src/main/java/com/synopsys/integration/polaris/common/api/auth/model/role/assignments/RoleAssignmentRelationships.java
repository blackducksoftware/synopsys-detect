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
package com.synopsys.integration.polaris.common.api.auth.model.role.assignments;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisRelationshipSingle;
import com.synopsys.integration.polaris.common.api.PolarisRelationships;

public class RoleAssignmentRelationships extends PolarisRelationships {
    @SerializedName("group")
    private PolarisRelationshipSingle group = null;

    @SerializedName("organization")
    private PolarisRelationshipSingle organization = null;

    @SerializedName("role")
    private PolarisRelationshipSingle role = null;

    @SerializedName("user")
    private PolarisRelationshipSingle user = null;

    /**
     * Get group
     * @return group
     */
    public PolarisRelationshipSingle getGroup() {
        return group;
    }

    public void setGroup(final PolarisRelationshipSingle group) {
        this.group = group;
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

    /**
     * Get role
     * @return role
     */
    public PolarisRelationshipSingle getRole() {
        return role;
    }

    public void setRole(final PolarisRelationshipSingle role) {
        this.role = role;
    }

    /**
     * Get user
     * @return user
     */
    public PolarisRelationshipSingle getUser() {
        return user;
    }

    public void setUser(final PolarisRelationshipSingle user) {
        this.user = user;
    }

}

