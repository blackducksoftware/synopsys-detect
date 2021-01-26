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
package com.synopsys.integration.polaris.common.api;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.auth.PolarisResourceLinks;

public class PolarisResource<A extends PolarisAttributes, R extends PolarisRelationships> extends PolarisComponent {
    @SerializedName("type")
    private String type;
    @SerializedName("id")
    private String id;
    @SerializedName("attributes")
    private A attributes = null;
    @SerializedName("relationships")
    private R relationships = null;
    @SerializedName("links")
    private PolarisResourceLinks links;

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public A getAttributes() {
        return attributes;
    }

    public void setAttributes(final A attributes) {
        this.attributes = attributes;
    }

    public R getRelationships() {
        return relationships;
    }

    public void setRelationships(final R relationships) {
        this.relationships = relationships;
    }

    public PolarisResourceLinks getLinks() {
        return links;
    }

    public void setLinks(final PolarisResourceLinks links) {
        this.links = links;
    }

}
