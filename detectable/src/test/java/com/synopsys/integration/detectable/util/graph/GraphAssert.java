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
package com.synopsys.integration.detectable.util.graph;

import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class GraphAssert {
    protected final Forge forge;
    protected final DependencyGraph graph;
    protected final ExternalIdFactory externalIdFactory;

    public GraphAssert(final Forge forge, final DependencyGraph graph) {
        this.forge = forge;
        this.graph = graph;
        this.externalIdFactory = new ExternalIdFactory();
    }

    public ExternalId hasRootDependency(final ExternalId externalId) {
        return hasRootDependency(externalId, String.format("Expected '%s' to be in the root of graph.", externalId.createExternalId()));
    }

    public ExternalId hasRootDependency(final ExternalId externalId, final String message) {
        Assertions.assertTrue(graph.getRootDependencyExternalIds().contains(externalId), message);
        return externalId;
    }

    public ExternalId hasDependency(final ExternalId externalId) {
        return hasDependency(externalId, String.format("Expected '%s' to be in the graph.", externalId.createExternalId()));
    }

    public ExternalId hasDependency(final ExternalId externalId, final String message) {
        Assertions.assertTrue(graph.hasDependency(externalId), message);
        return externalId;
    }

    public ExternalId hasNoDependency(final ExternalId externalId) {
        return hasNoDependency(externalId, String.format("Did not expect '%s' to be in the graph.", externalId.createExternalId()));
    }

    public ExternalId hasNoDependency(final ExternalId externalId, final String message) {
        Assertions.assertFalse(graph.hasDependency(externalId), message);
        return externalId;
    }

    public ExternalId hasParentChildRelationship(final ExternalId parent, final ExternalId child) {
        return hasParentChildRelationship(parent, child, String.format("Expected parent '%s' to have child '%s'.", parent.createExternalId(), child.createExternalId()));
    }

    public ExternalId hasParentChildRelationship(final ExternalId parent, final ExternalId child, final String message) {
        Assertions.assertTrue(graph.getChildrenExternalIdsForParent(parent).contains(child), message);
        return child;
    }

    public void hasRelationshipCount(final ExternalId parent, final int count) {
        hasRelationshipCount(parent, count, String.format("Expected '%s' to have a relationship count of %d.", parent.createExternalId(), count));
    }

    public void hasRelationshipCount(final ExternalId parent, final int count, final String message) {
        Assertions.assertEquals(count, graph.getChildrenExternalIdsForParent(parent).size(), message);
    }

    public void hasRootSize(final int size) {
        hasRootSize(size, String.format("Graph should have a root size of %d", size));
    }

    public void hasRootSize(final int size, final String message) {
        Assertions.assertEquals(size, graph.getRootDependencies().size(), message);
    }

}
