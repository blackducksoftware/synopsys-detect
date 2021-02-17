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

import java.util.UUID;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class MicrosoftLogin extends PolarisComponent {
    @SerializedName("attributes")
    private MicrosoftLoginAttributes attributes = null;

    @SerializedName("id")
    private UUID id;

    @SerializedName("relationships")
    private MicrosoftLoginRelationships relationships = null;

    @SerializedName("type")
    private String type;

    /**
     * Get attributes
     * @return attributes
     */
    public MicrosoftLoginAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(final MicrosoftLoginAttributes attributes) {
        this.attributes = attributes;
    }

    /**
     * Get id
     * @return id
     */
    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    /**
     * Get relationships
     * @return relationships
     */
    public MicrosoftLoginRelationships getRelationships() {
        return relationships;
    }

    public void setRelationships(final MicrosoftLoginRelationships relationships) {
        this.relationships = relationships;
    }

    /**
     * Get type
     * @return type
     */
    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

}

