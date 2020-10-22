package com.synopsys.integration.detectable.detectables.gradle.inspection.parse;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleGav;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.ReplacedGradleGav;

public class DependencyReplacementResolverTest {
    @Test
    public void testReplacement() {
        GradleGav resolvedGradleGav = new GradleGav("group", "name", "version");
        ReplacedGradleGav replacedGradleGav = new ReplacedGradleGav("replacedGroup", "replacedName", "replacedVersion");
        DependencyReplacementResolver dependencyReplacementResolver = DependencyReplacementResolver.createRootResolver();

        dependencyReplacementResolver.addReplacementData(replacedGradleGav, resolvedGradleGav);

        Optional<GradleGav> replacedDependency = dependencyReplacementResolver.getReplacement(replacedGradleGav);
        Assertions.assertTrue(replacedDependency.isPresent());
        Assertions.assertEquals(resolvedGradleGav, replacedDependency.get());

        Optional<GradleGav> notReplacedDependency = dependencyReplacementResolver.getReplacement(resolvedGradleGav);
        Assertions.assertFalse(notReplacedDependency.isPresent());
    }

    @Test
    public void testReplacementWithStructure() {
        GradleGav resolvedGradleGav = new GradleGav("group", "name", "version");

        ReplacedGradleGav replacedGradleGav = new ReplacedGradleGav("replacedGroup", "replacedName", "replacedVersion");
        DependencyReplacementResolver rootResolver = DependencyReplacementResolver.createRootResolver();
        rootResolver.addReplacementData(replacedGradleGav, resolvedGradleGav);

        ReplacedGradleGav secondReplacedGradleGav = new ReplacedGradleGav("subReplacedGroup", "subReplacedName", "subReplacedVersion");
        DependencyReplacementResolver subResolver = DependencyReplacementResolver.createFromParentResolver(rootResolver);
        subResolver.addReplacementData(secondReplacedGradleGav, resolvedGradleGav);

        Optional<GradleGav> rootReplacedDependency = subResolver.getReplacement(replacedGradleGav);
        Assertions.assertTrue(rootReplacedDependency.isPresent());
        Assertions.assertEquals(resolvedGradleGav, rootReplacedDependency.get());

        Optional<GradleGav> subReplacedDependency = subResolver.getReplacement(secondReplacedGradleGav);
        Assertions.assertTrue(subReplacedDependency.isPresent());
        Assertions.assertEquals(resolvedGradleGav, subReplacedDependency.get());
    }
}