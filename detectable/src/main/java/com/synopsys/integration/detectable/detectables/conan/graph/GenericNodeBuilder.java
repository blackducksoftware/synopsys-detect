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

public class GenericNodeBuilder<T> {
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
    private final List<T> requiresRefs = new ArrayList<>();
    private final List<T> buildRequiresRefs = new ArrayList<>();
    private boolean valid = true;
    private boolean forcedRootNode = false;

    public GenericNodeBuilder() {}

    public GenericNodeBuilder(GenericNode initializingNode) {
        this.forcedRootNode = initializingNode.isRootNode();
        this.ref = initializingNode.getRef();
        this.path = initializingNode.getPath();
        this.name = initializingNode.getName();
        this.version = initializingNode.getVersion();
        this.user = initializingNode.getUser();
        this.channel = initializingNode.getChannel();
        this.recipeRevision = initializingNode.getRecipeRevision();
        this.packageId = initializingNode.getPackageId();
        this.packageRevision = initializingNode.getPackageRevision();
    }

    public GenericNodeBuilder<T> forceRootNode() {
        forcedRootNode = true;
        return this;
    }

    public GenericNodeBuilder<T> setRef(String ref) {
        if (ref != null) {
            this.ref = ref.trim();
        }
        return this;
    }

    public GenericNodeBuilder<T> setPath(String path) {
        if (path != null) {
            this.path = path.trim();
        }
        return this;
    }

    public GenericNodeBuilder<T> setName(String name) {
        if (name != null) {
            this.name = name.trim();
        }
        return this;
    }

    public GenericNodeBuilder<T> setVersion(String version) {
        if (version != null) {
            this.version = version.trim();
        }
        return this;
    }

    public GenericNodeBuilder<T> setUser(String user) {
        if (user != null) {
            this.user = user.trim();
        }
        return this;
    }

    public GenericNodeBuilder<T> setChannel(String channel) {
        if (channel != null) {
            this.channel = channel.trim();
        }
        return this;
    }

    public GenericNodeBuilder<T> setRecipeRevision(String recipeRevision) {
        this.recipeRevision = recipeRevision;
        return this;
    }

    public GenericNodeBuilder<T> setPackageId(String packageId) {
        this.packageId = packageId;
        return this;
    }

    public GenericNodeBuilder<T> setPackageRevision(String packageRevision) {
        this.packageRevision = packageRevision;
        return this;
    }

    public GenericNodeBuilder<T> addRequiresRef(T requiresRef) {
        this.requiresRefs.add(requiresRef);
        return this;
    }

    public GenericNodeBuilder<T> addBuildRequiresRef(T buildRequiresRef) {
        this.buildRequiresRefs.add(buildRequiresRef);
        return this;
    }

    public GenericNodeBuilder<T> setValid(boolean valid) {
        this.valid = valid;
        return this;
    }

    public Optional<GenericNode<T>> build() {
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
        GenericNode<T> node = new GenericNode<>(ref, path, name, version, user, channel,
            recipeRevision, packageId, packageRevision,
            requiresRefs, buildRequiresRefs,
            isRootNode);
        logger.trace("node: {}", node);
        return Optional.of(node);
    }
}
