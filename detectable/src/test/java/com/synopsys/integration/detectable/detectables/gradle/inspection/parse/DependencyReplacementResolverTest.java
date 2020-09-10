package com.synopsys.integration.detectable.detectables.gradle.inspection.parse;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class DependencyReplacementResolverTest {
    @Test
    public void testReplacement() {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        Dependency resolved = new Dependency(externalIdFactory.createMavenExternalId("group", "name", "version"));
        Dependency replaced = new Dependency(externalIdFactory.createMavenExternalId("replacedGroup", "replacedName", "replacedVersion"));

        DependencyReplacementResolver dependencyReplacementResolver = DependencyReplacementResolver.createRootResolver();

        dependencyReplacementResolver.addReplacementData(replaced, resolved);

        Optional<Dependency> replacedDependency = dependencyReplacementResolver.getReplacement(replaced);
        Assertions.assertTrue(replacedDependency.isPresent());
        Assertions.assertEquals(resolved, replacedDependency.get());

        Optional<Dependency> notReplacedDependency = dependencyReplacementResolver.getReplacement(resolved);
        Assertions.assertFalse(notReplacedDependency.isPresent());
    }

    @Test
    public void testReplacementWithStructure() {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        Dependency resolvedDependency = new Dependency(externalIdFactory.createMavenExternalId("group", "name", "version"));
        Dependency replacedDependency = new Dependency(externalIdFactory.createMavenExternalId("replacedGroup", "replacedName", "replacedVersion"));

        DependencyReplacementResolver rootResolver = DependencyReplacementResolver.createRootResolver();
        rootResolver.addReplacementData(replacedDependency, resolvedDependency);

        Dependency secondReplacedDependency = new Dependency(externalIdFactory.createMavenExternalId("subReplacedGroup", "subReplacedName", "subReplacedVersion"));
        DependencyReplacementResolver subResolver = DependencyReplacementResolver.createFromParentResolver(rootResolver);
        subResolver.addReplacementData(secondReplacedDependency, resolvedDependency);

        Optional<Dependency> rootReplacedDependency = subResolver.getReplacement(replacedDependency);
        Assertions.assertTrue(rootReplacedDependency.isPresent());
        Assertions.assertEquals(resolvedDependency, rootReplacedDependency.get());

        Optional<Dependency> subReplacedDependency = subResolver.getReplacement(secondReplacedDependency);
        Assertions.assertTrue(subReplacedDependency.isPresent());
        Assertions.assertEquals(resolvedDependency, subReplacedDependency.get());
    }
}