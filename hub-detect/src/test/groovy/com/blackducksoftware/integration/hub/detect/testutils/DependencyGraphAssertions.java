/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.detect.testutils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
        assertNotNull("Graph does not have gav '" + parentGav + "'", dep);
        final Set<ExternalId> children = dependencyGraph.getChildrenExternalIdsForParent(dep);
        assertTrue("Parent gav '" + parentGav + "' does not have child gav '" + targetGavChild + "'", children.contains(childId));
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
                assertDoesNotHave(dependencyGraph, target, dep.externalId);
            }
        } else {
            for (final Dependency dep : dependencyGraph.getChildrenForParent(current)) {
                assertDoesNotHave(dep, target);
                assertDoesNotHave(dependencyGraph, target, dep.externalId);
            }
        }
    }

    public static void assertDoesNotHave(final Dependency dep, final String target) {
        assertDoesNotHaveName(dep, target);
        assertDoesNotHaveVersion(dep, target);
        assertDoesNotHave(dep.externalId, target);
    }

    public static void assertDoesNotHave(final ExternalId externalId, final String target) {
        assertDoesNotHaveVersion(externalId, target);
        assertDoesNotHaveGroup(externalId, target);
        assertDoesNotHaveName(externalId, target);
    }

    public static void assertDoesNotHaveName(final Dependency dep, final String name) {
        if (dep.name != null) {
            assertFalse("Dependency name contains '" + name + "'", dep.name.contains(name));
        }
    }

    public static void assertDoesNotHaveVersion(final Dependency dep, final String name) {
        if (dep.version != null) {
            assertFalse("Dependency version contains '" + name + "'", dep.version.contains(name));
        }
    }

    public static void assertDoesNotHaveVersion(final ExternalId externalId, final String name) {
        if (externalId.version != null) {
            assertFalse("External id version contains '" + name + "'", externalId.version.contains(name));
        }
    }

    public static void assertDoesNotHaveGroup(final ExternalId externalId, final String name) {
        if (externalId.group != null) {
            assertFalse("External id group contains '" + name + "'", externalId.group.contains(name));
        }
    }

    public static void assertDoesNotHaveName(final ExternalId externalId, final String name) {
        if (externalId.name != null) {
            assertFalse("External id name contains '" + name + "'", externalId.name.contains(name));
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
        assertNotNull("Expected dependency '" + org + ":" + name + ":" + version + "' to exist in graph but it was null.", dep);

    }
}
