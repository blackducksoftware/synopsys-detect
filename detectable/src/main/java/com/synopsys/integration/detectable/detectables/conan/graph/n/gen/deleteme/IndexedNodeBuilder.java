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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexedNodeBuilder extends ParentNodeBuilder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<Integer> requiresIndices;
    private List<Integer> buildRequiresIndices;

    public IndexedNodeBuilder setRequiresIndices(List<Integer> requiresIndices) {
        this.requiresIndices = requiresIndices;
        return this;
    }

    public IndexedNodeBuilder setBuildRequiresIndices(List<Integer> buildRequiresIndices) {
        this.buildRequiresIndices = buildRequiresIndices;
        return this;
    }

    public Optional<IndexedNode> build() {
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
        IndexedNode node = new IndexedNode(ref, path, name, version, user, channel,
            recipeRevision, packageId, packageRevision,
            isRootNode,
            requiresIndices, buildRequiresIndices);
        logger.trace("node: {}", node);
        return Optional.of(node);
    }
}
