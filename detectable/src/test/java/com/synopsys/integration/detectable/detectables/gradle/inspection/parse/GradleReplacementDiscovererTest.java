package com.synopsys.integration.detectable.detectables.gradle.inspection.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleGav;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleTreeNode;

public class GradleReplacementDiscovererTest {
    @Test
    public void testPopulation() {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        GradleReplacementDiscoverer gradleReplacementDiscoverer = new GradleReplacementDiscoverer(externalIdFactory);
        DependencyReplacementResolver dependencyReplacementResolver = DependencyReplacementResolver.createRootResolver();

        List<GradleTreeNode> gradleTreeNodes = new ArrayList<>();
        GradleGav resolvedGav = new GradleGav("artifact", "version", "group");
        GradleGav replacedGav = new GradleGav("replacedArtifact", "replacedVersion", "replacedGroup");
        gradleTreeNodes.add(GradleTreeNode.newProject(0, "foo"));
        gradleTreeNodes.add(GradleTreeNode.newGav(1, "unrelatedArtifact", "unrelatedVersion", "unrelatedGroup"));
        gradleTreeNodes.add(GradleTreeNode.newGavWithReplacement(1, resolvedGav.getArtifact(), resolvedGav.getVersion(), resolvedGav.getName(), replacedGav.getArtifact(), replacedGav.getVersion(), replacedGav.getName()));

        gradleReplacementDiscoverer.populateFromTreeNodes(dependencyReplacementResolver, gradleTreeNodes);

        Dependency resolvedDependency = new Dependency(resolvedGav.getArtifact(), resolvedGav.getVersion(), externalIdFactory.createMavenExternalId(resolvedGav.getName(), resolvedGav.getArtifact(), resolvedGav.getVersion()));
        Dependency replacedDependency = new Dependency(replacedGav.getArtifact(), replacedGav.getVersion(), externalIdFactory.createMavenExternalId(replacedGav.getName(), replacedGav.getArtifact(), replacedGav.getVersion()));
        Optional<Dependency> replacement = dependencyReplacementResolver.getReplacement(replacedDependency);
        Assertions.assertTrue(replacement.isPresent());
        Assertions.assertEquals(resolvedDependency, replacement.get());

        Dependency unrelatedDependency = new Dependency(externalIdFactory.createMavenExternalId(resolvedGav.getName(), resolvedGav.getArtifact(), resolvedGav.getVersion()));
        Optional<Dependency> unrelated = dependencyReplacementResolver.getReplacement(unrelatedDependency);
        Assertions.assertFalse(unrelated.isPresent());
    }

}