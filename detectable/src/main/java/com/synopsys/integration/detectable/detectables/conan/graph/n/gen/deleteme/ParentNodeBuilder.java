/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.conan.graph.n.gen.deleteme;

public abstract class ParentNodeBuilder {
    protected String ref;
    protected String path;
    protected String name;
    protected String version;
    protected String user;
    protected String channel;
    protected String recipeRevision;
    protected String packageId;
    protected String packageRevision;
    protected boolean valid = true;
    protected boolean forcedRootNode = false;

    public ParentNodeBuilder forceRootNode() {
        forcedRootNode = true;
        return this;
    }

    public ParentNodeBuilder setRef(String ref) {
        if (ref != null) {
            this.ref = ref.trim();
        }
        return this;
    }

    public ParentNodeBuilder setPath(String path) {
        if (path != null) {
            this.path = path.trim();
        }
        return this;
    }

    public ParentNodeBuilder setName(String name) {
        if (name != null) {
            this.name = name.trim();
        }
        return this;
    }

    public ParentNodeBuilder setVersion(String version) {
        if (version != null) {
            this.version = version.trim();
        }
        return this;
    }

    public ParentNodeBuilder setUser(String user) {
        if (user != null) {
            this.user = user.trim();
        }
        return this;
    }

    public ParentNodeBuilder setChannel(String channel) {
        if (channel != null) {
            this.channel = channel.trim();
        }
        return this;
    }

    public ParentNodeBuilder setRecipeRevision(String recipeRevision) {
        this.recipeRevision = recipeRevision;
        return this;
    }

    public ParentNodeBuilder setPackageId(String packageId) {
        this.packageId = packageId;
        return this;
    }

    public ParentNodeBuilder setPackageRevision(String packageRevision) {
        this.packageRevision = packageRevision;
        return this;
    }

    public ParentNodeBuilder setValid(boolean valid) {
        this.valid = valid;
        return this;
    }
}
