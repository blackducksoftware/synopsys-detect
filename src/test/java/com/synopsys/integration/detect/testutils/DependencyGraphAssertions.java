/**
 * synopsys-detect
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
package com.synopsys.integration.detect.testutils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class DependencyGraphAssertions {
    public static void assertHasRootMavenGavs(final DependencyGraph dependencyGraph, final String... targetGavs) {
        for (final String targetGav : targetGavs) {
            assertHasRootMavenGav(dependencyGraph, targetGav);
        }
    }

    public static void assertHasRootMavenGav(final DependencyGraph dependencyGraph, final String targetGav) {
        final ExternalId targetId = mavenGavToExternalId(targetGav);
        final Dependency dep = dependencyGraph.getDependency(targetId);
        assertNotNull(dep);
    }

    public static void assertParentHasChildMavenGavs(final String parentGav, final DependencyGraph dependencyGraph, final String... targetGavChildren) {
        for (final String targetGavChild : targetGavChildren) {
            assertParentHasChildMavenGav(parentGav, dependencyGraph, targetGavChild);
        }
    }

    public static void assertParentHasChildMavenGav(final String parentGav, final DependencyGraph dependencyGraph, final String targetGavChild) {
        final ExternalId parentId = mavenGavToExternalId(parentGav);
        final ExternalId childId = mavenGavToExternalId(targetGavChild);
        final Dependency dep = dependencyGraph.getDependency(parentId);
        assertNotNull(dep, "Graph does not have gav '" + parentGav + "'");
        final Set<ExternalId> children = dependencyGraph.getChildrenExternalIdsForParent(dep);
        assertTrue(children.contains(childId), "Parent gav '" + parentGav + "' does not have child gav '" + targetGavChild + "'");
    }

    private static ExternalId mavenGavToExternalId(final String gav) {
        final String[] split = gav.split(":");
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        return externalIdFactory.createMavenExternalId(split[0], split[1], split[2]);
    }

    public static void assertDoesNotHave(final DependencyGraph dependencyGraph, final String name) {
        assertDoesNotHave(dependencyGraph, name, null);
    }

    private static void assertDoesNotHave(final DependencyGraph dependencyGraph, final String target, final ExternalId current) {
        if (current == null) {
            for (final Dependency dep : dependencyGraph.getRootDependencies()) {
                assertDoesNotHave(dep, target);
                assertDoesNotHave(dependencyGraph, target, dep.getExternalId());
            }
        } else {
            for (final Dependency dep : dependencyGraph.getChildrenForParent(current)) {
                assertDoesNotHave(dep, target);
                assertDoesNotHave(dependencyGraph, target, dep.getExternalId());
            }
        }
    }

    public static void assertDoesNotHave(final Dependency dep, final String target) {
        assertDoesNotHaveName(dep, target);
        assertDoesNotHaveVersion(dep, target);
        assertDoesNotHave(dep.getExternalId(), target);
    }

    public static void assertDoesNotHave(final ExternalId externalId, final String target) {
        assertDoesNotHaveVersion(externalId, target);
        assertDoesNotHaveGroup(externalId, target);
        assertDoesNotHaveName(externalId, target);
    }

    public static void assertDoesNotHaveName(final Dependency dep, final String name) {
        if (dep.getName() != null) {
            assertFalse(dep.getName().contains(name), "Dependency name contains '" + name + "'");
        }
    }

    public static void assertDoesNotHaveVersion(final Dependency dep, final String name) {
        if (dep.getVersion() != null) {
            assertFalse(dep.getVersion().contains(name), "Dependency version contains '" + name + "'");
        }
    }

    public static void assertDoesNotHaveVersion(final ExternalId externalId, final String name) {
        if (externalId.getVersion() != null) {
            assertFalse(externalId.getVersion().contains(name), "External id version contains '" + name + "'");
        }
    }

    public static void assertDoesNotHaveGroup(final ExternalId externalId, final String name) {
        if (externalId.getGroup() != null) {
            assertFalse(externalId.getGroup().contains(name), "External id group contains '" + name + "'");
        }
    }

    public static void assertDoesNotHaveName(final ExternalId externalId, final String name) {
        if (externalId.getName() != null) {
            assertFalse(externalId.getName().contains(name), "External id name contains '" + name + "'");
        }
    }

    public static void assertHasMavenGav(final DependencyGraph dependencyGraph, final String gav) {
        final String[] split = gav.split(":");
        assertHasMavenGav(dependencyGraph, split[0], split[1], split[2]);
    }

    public static void assertHasMavenGav(final DependencyGraph dependencyGraph, final String org, final String name, final String version) {
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final ExternalId id = externalIdFactory.createMavenExternalId(org, name, version);
        final Dependency dep = dependencyGraph.getDependency(id);
        assertNotNull(dep, "Expected dependency '" + org + ":" + name + ":" + version + "' to exist in graph but it was null.");

    }
}
