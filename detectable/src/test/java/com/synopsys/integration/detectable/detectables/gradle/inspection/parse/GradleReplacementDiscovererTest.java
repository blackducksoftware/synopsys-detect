package com.synopsys.integration.detectable.detectables.gradle.inspection.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleGav;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleTreeNode;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.ReplacedGradleGav;

public class GradleReplacementDiscovererTest {
    @Test
    public void testPopulation() {
        GradleReplacementDiscoverer gradleReplacementDiscoverer = new GradleReplacementDiscoverer();
        DependencyReplacementResolver dependencyReplacementResolver = DependencyReplacementResolver.createRootResolver();

        List<GradleTreeNode> gradleTreeNodes = new ArrayList<>();
        GradleGav resolvedGav = new GradleGav("group", "artifact", "version");
        ReplacedGradleGav replacedGav = new ReplacedGradleGav("replacedGroup", "replacedArtifact", "replacedVersion");
        ReplacedGradleGav replacedNoVersionGav = new ReplacedGradleGav("replacedGroup", "replacedArtifact");
        GradleGav unrelatedGradleGav = new GradleGav("unrelatedGroup", "unrelatedArtifact", "unrelatedVersion");

        gradleTreeNodes.add(GradleTreeNode.newProject(0, "foo"));
        gradleTreeNodes.add(GradleTreeNode.newGav(1, resolvedGav));
        gradleTreeNodes.add(GradleTreeNode.newGavWithReplacement(1, resolvedGav, replacedGav));
        gradleTreeNodes.add(GradleTreeNode.newGavWithReplacement(1, resolvedGav, replacedNoVersionGav));
        gradleTreeNodes.add(GradleTreeNode.newGav(1, unrelatedGradleGav));

        gradleReplacementDiscoverer.populateFromTreeNodes(dependencyReplacementResolver, gradleTreeNodes);

        Optional<GradleGav> replacement = dependencyReplacementResolver.getReplacement(replacedGav);
        Assertions.assertTrue(replacement.isPresent());
        Assertions.assertEquals(resolvedGav, replacement.get());

        Optional<GradleGav> replacementNoVersion = dependencyReplacementResolver.getReplacement(replacedNoVersionGav);
        Assertions.assertTrue(replacementNoVersion.isPresent());
        Assertions.assertEquals(resolvedGav, replacementNoVersion.get());

        Optional<GradleGav> unrelated = dependencyReplacementResolver.getReplacement(unrelatedGradleGav);
        Assertions.assertFalse(unrelated.isPresent());
    }

}