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
package com.synopsys.integration.detectable.detectables.conan.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO when ConanNode is re-worked, this class will need to be completely  re-thought
public class ConanNodeBuilder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private String ref;
    private String path;
    private String name;
    private String version;
    private String user;
    private String channel;
    private String recipeRevision;
    private String packageId;
    private String packageRevision;
    private final List<String> requiresRefs = new ArrayList<>();
    private List<Integer> requiresIndices;
    private final List<String> buildRequiresRefs = new ArrayList<>();
    private List<Integer> buildRequiresIndices;
    private boolean valid = true;
    private boolean forcedRootNode = false;

    public ConanNodeBuilder forceRootNode() {
        forcedRootNode = true;
        return this;
    }

    public ConanNodeBuilder setRef(String ref) {
        if (ref != null) {
            this.ref = ref.trim();
        }
        return this;
    }

    public ConanNodeBuilder setPath(String path) {
        if (path != null) {
            this.path = path.trim();
        }
        return this;
    }

    public ConanNodeBuilder setName(String name) {
        if (name != null) {
            this.name = name.trim();
        }
        return this;
    }

    public ConanNodeBuilder setVersion(String version) {
        if (version != null) {
            this.version = version.trim();
        }
        return this;
    }

    public ConanNodeBuilder setUser(String user) {
        if (user != null) {
            this.user = user.trim();
        }
        return this;
    }

    public ConanNodeBuilder setChannel(String channel) {
        if (channel != null) {
            this.channel = channel.trim();
        }
        return this;
    }

    public ConanNodeBuilder setRecipeRevision(String recipeRevision) {
        this.recipeRevision = recipeRevision;
        return this;
    }

    public ConanNodeBuilder setPackageId(String packageId) {
        this.packageId = packageId;
        return this;
    }

    public ConanNodeBuilder setPackageRevision(String packageRevision) {
        this.packageRevision = packageRevision;
        return this;
    }

    public ConanNodeBuilder addRequiresRef(String requiresRef) {
        this.requiresRefs.add(requiresRef);
        return this;
    }

    public ConanNodeBuilder setRequiresIndices(List<Integer> requiresIndices) {
        this.requiresIndices = requiresIndices;
        return this;
    }

    public ConanNodeBuilder addBuildRequiresRef(String buildRequiresRef) {
        this.buildRequiresRefs.add(buildRequiresRef);
        return this;
    }

    public ConanNodeBuilder setBuildRequiresIndices(List<Integer> buildRequiresIndices) {
        this.buildRequiresIndices = buildRequiresIndices;
        return this;
    }

    public ConanNodeBuilder setValid(boolean valid) {
        this.valid = valid;
        return this;
    }

    public Optional<ConanNode> build() {
        if (StringUtils.isBlank(ref) && StringUtils.isBlank(path)) {
            valid = false;
        }
        if (!valid) {
            logger.debug("This wasn't a node");
            return Optional.empty();
        }
        if (StringUtils.isBlank(ref) && StringUtils.isNotBlank(path)) {
            ref = path;
        }
        boolean isRootNode = false;
        if (forcedRootNode || (path != null)) {
            isRootNode = true;
        }
        if (requiresIndices == null) {
            requiresIndices = new ArrayList<>(0);
        }
        if (buildRequiresIndices == null) {
            buildRequiresIndices = new ArrayList<>(0);
        }
        ConanNode node = new ConanNode(ref, path, name, version, user, channel,
            recipeRevision, packageId, packageRevision,
            requiresRefs, requiresIndices, buildRequiresRefs, buildRequiresIndices,
            isRootNode);
        logger.trace("node: {}", node);
        return Optional.of(node);
    }
}
