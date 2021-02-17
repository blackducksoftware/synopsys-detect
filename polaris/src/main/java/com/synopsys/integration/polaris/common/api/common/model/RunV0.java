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

import com.google.gson.annotations.SerializedName;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class RunV0 extends ObjectContainer {
    @SerializedName("attributes")
    private RunV0Attributes attributes = null;

    @SerializedName("relationships")
    private RunV0Relationships relationships = null;

    @SerializedName("links")
    private JsonApiSelfLinks links = null;

    @SerializedName("meta")
    private MetaWithOrganizationTrash meta = null;

    /**
     * Get attributes
     * @return attributes
     */
    public RunV0Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(final RunV0Attributes attributes) {
        this.attributes = attributes;
    }

    /**
     * Get relationships
     * @return relationships
     */
    public RunV0Relationships getRelationships() {
        return relationships;
    }

    public void setRelationships(final RunV0Relationships relationships) {
        this.relationships = relationships;
    }

    /**
     * Get links
     * @return links
     */
    public JsonApiSelfLinks getLinks() {
        return links;
    }

    public void setLinks(final JsonApiSelfLinks links) {
        this.links = links;
    }

    /**
     * Get meta
     * @return meta
     */
    public MetaWithOrganizationTrash getMeta() {
        return meta;
    }

    public void setMeta(final MetaWithOrganizationTrash meta) {
        this.meta = meta;
    }

}

