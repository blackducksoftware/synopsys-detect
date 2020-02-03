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
        assert graph.getRootDependencyExternalIds().contains(externalId);
        return externalId;
    }

    public ExternalId hasDependency(final ExternalId externalId) {
        assert graph.hasDependency(externalId);
        return externalId;
    }

    public ExternalId hasNoDependency(final ExternalId externalId) {
        assert !graph.hasDependency(externalId);
        return externalId;
    }

    public ExternalId hasParentChildRelationship(final ExternalId parent, final ExternalId child) {
        assert graph.getChildrenExternalIdsForParent(parent).contains(child);
        return child;
    }

    public void hasRelationshipCount(final ExternalId parent, final int count) {
        assert graph.getChildrenExternalIdsForParent(parent).size() == count;
    }

    public void hasRootSize(final int size) {
        assert graph.getRootDependencies().size() == size;
    }

}
