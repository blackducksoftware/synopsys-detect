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
import java.util.StringTokenizer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConanNodeBuilder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private String ref;
    private String filename;
    private String name;
    private String version;
    private String user;
    private String channel;
    private String recipeRevision;
    private String packageId;
    private String packageRevision;
    private final List<String> requiresRefs = new ArrayList<>();
    private final List<String> buildRequiresRefs = new ArrayList<>();
    private final List<String> requiredByRefs = new ArrayList<>();

    public ConanNodeBuilder setRef(String ref) {
        ref = ref.trim();
        this.ref = ref;
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

    public ConanNodeBuilder addBuildRequiresRef(String buildRequiresRef) {
        this.buildRequiresRefs.add(buildRequiresRef);
        return this;
    }

    public ConanNodeBuilder addRequiredByRef(String requiredByRef) {
        this.requiredByRefs.add(requiredByRef);
        return this;
    }

    public Optional<ConanNode> build() {
        if (StringUtils.isBlank(ref) || StringUtils.isBlank(packageId)) {
            logger.debug("This wasn't a node");
            return Optional.empty();
        }
        // if rootNode: conanfile.{txt,py}[ (projectname/version)]
        // else       : package/version[@user/channel]
        if (ref.startsWith("conanfile.")) {
            StringTokenizer tokenizer = new StringTokenizer(ref, " \t()/");
            filename = tokenizer.nextToken();
            if (tokenizer.hasMoreTokens()) {
                name = tokenizer.nextToken();
                if (tokenizer.hasMoreTokens()) {
                    version = tokenizer.nextToken();
                }
            }
            logger.info(String.format("filename: %s; name: %s; version: %s", filename, name, version));
        } else {
            StringTokenizer tokenizer = new StringTokenizer(ref, "/@");
            name = tokenizer.nextToken();
            if (tokenizer.hasMoreTokens()) {
                version = tokenizer.nextToken();
                if (tokenizer.hasMoreTokens()) {
                    user = tokenizer.nextToken();
                    if (tokenizer.hasMoreTokens()) {
                        channel = tokenizer.nextToken();
                    }
                }
            }
            logger.info(String.format("name: %s; version: %s; user: %s; channel: %s", name, version, user, channel));
        }
        boolean isRootNode = false;
        if ((filename != null) && CollectionUtils.isEmpty(requiredByRefs)) {
            isRootNode = true;
        } else if (CollectionUtils.isEmpty(requiredByRefs)) {
            logger.warn(String.format("Node %s doesn't look like a root node, but its requiredBy list is empty; treating it as a non-root node", ref));
            // TODO this may need to change after requiredBy parsing implemented
            isRootNode = false;
        } else {
            isRootNode = false;
        }
        ConanNode node = new ConanNode(ref, filename, name, version, user, channel,
            recipeRevision, packageId, packageRevision, requiresRefs, buildRequiresRefs, requiredByRefs, isRootNode);
        return Optional.of(node);
    }
}
