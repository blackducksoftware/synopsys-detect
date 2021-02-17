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
package com.synopsys.integration.polaris.common.api.common.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.annotations.SerializedName;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class RevisionV0Relationships extends PolarisComponent {
    @SerializedName("branch")
    private RevisionV0BranchToOneRelationship branch = null;

    @SerializedName("runs")
    private JsonApiLazyRelationship runs = null;

    /**
     * Get branch
     * @return branch
     */
    public RevisionV0BranchToOneRelationship getBranch() {
        return branch;
    }

    public void setBranch(final RevisionV0BranchToOneRelationship branch) {
        this.branch = branch;
    }

    /**
     * Get runs
     * @return runs
     */
    public JsonApiLazyRelationship getRuns() {
        return runs;
    }

    public void setRuns(final JsonApiLazyRelationship runs) {
        this.runs = runs;
    }

}

