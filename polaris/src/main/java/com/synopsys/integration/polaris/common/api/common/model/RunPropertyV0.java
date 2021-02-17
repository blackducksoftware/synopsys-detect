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

public class RunPropertyV0 extends ObjectContainer {
    @SerializedName("attributes")
    private RunPropertyV0Attributes attributes = null;

    @SerializedName("links")
    private JsonApiSelfLinks links = null;

    @SerializedName("relationships")
    private RunPropertyV0Relationships relationships = null;

    @SerializedName("meta")
    private MetaWithOrganizationTrash meta = null;

    /**
     * Get attributes
     * @return attributes
     */
    public RunPropertyV0Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(final RunPropertyV0Attributes attributes) {
        this.attributes = attributes;
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
     * Get relationships
     * @return relationships
     */
    public RunPropertyV0Relationships getRelationships() {
        return relationships;
    }

    public void setRelationships(final RunPropertyV0Relationships relationships) {
        this.relationships = relationships;
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

