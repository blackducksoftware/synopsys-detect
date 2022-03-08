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
    public static void assertHasRootMavenGavs(DependencyGraph dependencyGraph, String... targetGavs) {
        for (String targetGav : targetGavs) {
            assertHasRootMavenGav(dependencyGraph, targetGav);
        }
    }

    public static void assertHasRootMavenGav(DependencyGraph dependencyGraph, String targetGav) {
        ExternalId targetId = mavenGavToExternalId(targetGav);
        Dependency dep = dependencyGraph.getDependency(targetId);
        assertNotNull(dep);
    }

    public static void assertParentHasChildMavenGavs(String parentGav, DependencyGraph dependencyGraph, String... targetGavChildren) {
        for (String targetGavChild : targetGavChildren) {
            assertParentHasChildMavenGav(parentGav, dependencyGraph, targetGavChild);
        }
    }

    public static void assertParentHasChildMavenGav(String parentGav, DependencyGraph dependencyGraph, String targetGavChild) {
        ExternalId parentId = mavenGavToExternalId(parentGav);
        ExternalId childId = mavenGavToExternalId(targetGavChild);
        Dependency dep = dependencyGraph.getDependency(parentId);
        assertNotNull(dep, "Graph does not have gav '" + parentGav + "'");
        Set<ExternalId> children = dependencyGraph.getChildrenExternalIdsForParent(dep);
        assertTrue(children.contains(childId), "Parent gav '" + parentGav + "' does not have child gav '" + targetGavChild + "'");
    }

    private static ExternalId mavenGavToExternalId(String gav) {
        String[] split = gav.split(":");
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        return externalIdFactory.createMavenExternalId(split[0], split[1], split[2]);
    }

    public static void assertDoesNotHave(DependencyGraph dependencyGraph, String name) {
        assertDoesNotHave(dependencyGraph, name, null);
    }

    private static void assertDoesNotHave(DependencyGraph dependencyGraph, String target, ExternalId current) {
        if (current == null) {
            for (Dependency dep : dependencyGraph.getRootDependencies()) {
                assertDoesNotHave(dep, target);
                assertDoesNotHave(dependencyGraph, target, dep.getExternalId());
            }
        } else {
            for (Dependency dep : dependencyGraph.getChildrenForParent(current)) {
                assertDoesNotHave(dep, target);
                assertDoesNotHave(dependencyGraph, target, dep.getExternalId());
            }
        }
    }

    public static void assertDoesNotHave(Dependency dep, String target) {
        assertDoesNotHaveName(dep, target);
        assertDoesNotHaveVersion(dep, target);
        assertDoesNotHave(dep.getExternalId(), target);
    }

    public static void assertDoesNotHave(ExternalId externalId, String target) {
        assertDoesNotHaveVersion(externalId, target);
        assertDoesNotHaveGroup(externalId, target);
        assertDoesNotHaveName(externalId, target);
    }

    public static void assertDoesNotHaveName(Dependency dep, String name) {
        if (dep.getName() != null) {
            assertFalse(dep.getName().contains(name), "Dependency name contains '" + name + "'");
        }
    }

    public static void assertDoesNotHaveVersion(Dependency dep, String name) {
        if (dep.getVersion() != null) {
            assertFalse(dep.getVersion().contains(name), "Dependency version contains '" + name + "'");
        }
    }

    public static void assertDoesNotHaveVersion(ExternalId externalId, String name) {
        if (externalId.getVersion() != null) {
            assertFalse(externalId.getVersion().contains(name), "External id version contains '" + name + "'");
        }
    }

    public static void assertDoesNotHaveGroup(ExternalId externalId, String name) {
        if (externalId.getGroup() != null) {
            assertFalse(externalId.getGroup().contains(name), "External id group contains '" + name + "'");
        }
    }

    public static void assertDoesNotHaveName(ExternalId externalId, String name) {
        if (externalId.getName() != null) {
            assertFalse(externalId.getName().contains(name), "External id name contains '" + name + "'");
        }
    }

    public static void assertHasMavenGav(DependencyGraph dependencyGraph, String gav) {
        String[] split = gav.split(":");
        assertHasMavenGav(dependencyGraph, split[0], split[1], split[2]);
    }

    public static void assertHasMavenGav(DependencyGraph dependencyGraph, String org, String name, String version) {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        ExternalId id = externalIdFactory.createMavenExternalId(org, name, version);
        Dependency dep = dependencyGraph.getDependency(id);
        assertNotNull(dep, "Expected dependency '" + org + ":" + name + ":" + version + "' to exist in graph but it was null.");

    }
}
