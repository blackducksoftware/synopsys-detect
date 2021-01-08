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
package com.synopsys.integration.polaris.common.api.job.model;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisRelationships;
import com.synopsys.integration.polaris.common.api.common.model.ToOneRelationship;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class JobRelationships extends PolarisRelationships {
    @SerializedName("branch")
    private ToOneRelationship branch = null;

    @SerializedName("organization")
    private ToOneRelationship organization = null;

    @SerializedName("owner")
    private ToOneRelationship owner = null;

    @SerializedName("project")
    private ToOneRelationship project = null;

    @SerializedName("revision")
    private ToOneRelationship revision = null;

    @SerializedName("runs")
    private ToOneRelationship runs = null;

    /**
     * Get branch
     * @return branch
     */
    public ToOneRelationship getBranch() {
        return branch;
    }

    public void setBranch(ToOneRelationship branch) {
        this.branch = branch;
    }

    /**
     * Get organization
     * @return organization
     */
    public ToOneRelationship getOrganization() {
        return organization;
    }

    public void setOrganization(ToOneRelationship organization) {
        this.organization = organization;
    }

    /**
     * Get owner
     * @return owner
     */
    public ToOneRelationship getOwner() {
        return owner;
    }

    public void setOwner(ToOneRelationship owner) {
        this.owner = owner;
    }

    /**
     * Get project
     * @return project
     */
    public ToOneRelationship getProject() {
        return project;
    }

    public void setProject(ToOneRelationship project) {
        this.project = project;
    }

    /**
     * Get revision
     * @return revision
     */
    public ToOneRelationship getRevision() {
        return revision;
    }

    public void setRevision(ToOneRelationship revision) {
        this.revision = revision;
    }

    /**
     * Get runs
     * @return runs
     */
    public ToOneRelationship getRuns() {
        return runs;
    }

    public void setRuns(ToOneRelationship runs) {
        this.runs = runs;
    }

}
