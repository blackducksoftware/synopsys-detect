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
package com.synopsys.integration.polaris.common.api.auth.model.group;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisRelationship;
import com.synopsys.integration.polaris.common.api.PolarisRelationshipMultiple;
import com.synopsys.integration.polaris.common.api.PolarisRelationshipSingle;
import com.synopsys.integration.polaris.common.api.PolarisRelationships;

public class GroupRelationships extends PolarisRelationships {
    @SerializedName("ancestors")
    private PolarisRelationshipMultiple ancestors = null;

    @SerializedName("children")
    private PolarisRelationship children = null;

    @SerializedName("organization")
    private PolarisRelationshipSingle organization = null;

    @SerializedName("parent")
    private PolarisRelationshipSingle parent = null;

    /**
     * Get ancestors
     * @return ancestors
     */
    public PolarisRelationshipMultiple getAncestors() {
        return ancestors;
    }

    public void setAncestors(final PolarisRelationshipMultiple ancestors) {
        this.ancestors = ancestors;
    }

    /**
     * Get children
     * @return children
     */
    public PolarisRelationship getChildren() {
        return children;
    }

    public void setChildren(final PolarisRelationship children) {
        this.children = children;
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
     * Get parent
     * @return parent
     */
    public PolarisRelationshipSingle getParent() {
        return parent;
    }

    public void setParent(final PolarisRelationshipSingle parent) {
        this.parent = parent;
    }

}

