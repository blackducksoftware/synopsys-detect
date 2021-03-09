package com.synopsys.integration.detectable.detectables.yarn.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.detectables.yarn.YarnTransformer;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLock;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockResult;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntry;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryId;

@UnitTest
class YarnTransformerTest {
    private static ExternalIdFactory externalIdFactory;
    private static YarnTransformer yarnTransformer;

    @BeforeAll
    static void setup() {
        externalIdFactory = new ExternalIdFactory();
        yarnTransformer = new YarnTransformer(externalIdFactory);
    }

    @Test
    void testExcludeDevDependencies() throws MissingExternalIdException {
        YarnLockResult yarnLockResult = buildTestYarnLockResult();

        DependencyGraph dependencyGraph = yarnTransformer.transform(yarnLockResult, true, false, false, new ArrayList<>());

        assertEquals(1, dependencyGraph.getRootDependencies().size());

        Set<Dependency> rootDeps = dependencyGraph.getRootDependencies();
        assertEquals("foo", rootDeps.iterator().next().getName());

        ExternalId fooExternalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "foo", "1.0");
        assertTrue(dependencyGraph.hasDependency(fooExternalId));

        ExternalId yarnExternalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "yarn", "1.22.5");
        assertTrue(dependencyGraph.hasDependency(yarnExternalId));
    }

    @Test
    void testIncludeDevDependencies() throws MissingExternalIdException {
        YarnLockResult yarnLockResult = buildTestYarnLockResult();

        DependencyGraph dependencyGraph = yarnTransformer.transform(yarnLockResult, false, false, false, new ArrayList<>());

        assertEquals(2, dependencyGraph.getRootDependencies().size());

        ExternalId fooExternalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "foo", "1.0");
        assertTrue(dependencyGraph.hasDependency(fooExternalId));
        assertTrue(dependencyGraph.getRootDependencyExternalIds().contains(fooExternalId));

        ExternalId barExternalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "bar", "1.0");
        assertTrue(dependencyGraph.hasDependency(barExternalId));
        assertTrue(dependencyGraph.getRootDependencyExternalIds().contains(barExternalId));

        ExternalId yarnExternalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "yarn", "1.22.5");
        assertTrue(dependencyGraph.hasDependency(yarnExternalId));
    }

    @NotNull
    private YarnLockResult buildTestYarnLockResult() {
        PackageJson packageJson = new PackageJson();
        packageJson.dependencies = new HashMap<>();
        packageJson.dependencies.put("foo", "fooFuzzyVersion-1.0");
        packageJson.devDependencies.put("bar", "barFuzzyVersion-1.0");

        // yarn.lock: foo and bar both depend on yarn
        List<YarnLockEntryId> yarnLockEntryIdsFoo = Collections.singletonList(new YarnLockEntryId("foo", "fooFuzzyVersion-1.0"));
        List<YarnLockEntryId> yarnLockEntryIdsBar = Collections.singletonList(new YarnLockEntryId("bar", "barFuzzyVersion-1.0"));
        List<YarnLockEntryId> yarnLockEntryIdsYarn = Collections.singletonList(new YarnLockEntryId("yarn", "^1.22.4"));
        List<YarnLockDependency> dependencyRefToYarn = Collections.singletonList(new YarnLockDependency("yarn", "^1.22.4", false));
        List<YarnLockEntry> yarnLockEntries = new ArrayList<>();
        yarnLockEntries.add(new YarnLockEntry(false, yarnLockEntryIdsFoo, "1.0", dependencyRefToYarn));
        yarnLockEntries.add(new YarnLockEntry(false, yarnLockEntryIdsBar, "1.0", dependencyRefToYarn));
        yarnLockEntries.add(new YarnLockEntry(false, yarnLockEntryIdsYarn, "1.22.5", new ArrayList<>()));
        YarnLock yarnLock = new YarnLock(null, false, yarnLockEntries);
        YarnLockResult yarnLockResult = new YarnLockResult(packageJson, new HashMap<>(), "yarn.lock", yarnLock);
        return yarnLockResult;
    }

    @Test
    void testYarn2FollowDependencies() throws MissingExternalIdException {

    }

    @Test
    void testYarn2AddAllWorkspaces() throws MissingExternalIdException {

    }

    @Test
    void testYarn1FollowDependencies() throws MissingExternalIdException {

    }

    @Test
    void testYarn1AddAllWorkspaces() throws MissingExternalIdException {
    }

    @Test
    void doesntThrowOnMissingExternalId() throws MissingExternalIdException {
        // Ensure components not defined in the graph doesn't cause an exception to be thrown. See IDETECT-1974.

        PackageJson packageJson = new PackageJson();
        packageJson.dependencies = new HashMap<>();
        packageJson.dependencies.put("foo", "fooFuzzyVersion-1.0");

        List<YarnLockEntryId> validYarnLockEntryIds = Collections.singletonList(new YarnLockEntryId("foo", "fooFuzzyVersion-1.0"));
        List<YarnLockDependency> validYarnLockDependencies = Collections.singletonList(new YarnLockDependency("yarn", "^1.22.4", false));
        List<YarnLockEntry> yarnLockEntries = Collections.singletonList(new YarnLockEntry(false, validYarnLockEntryIds, "1.0", validYarnLockDependencies));
        YarnLock yarnLock = new YarnLock(null, false, yarnLockEntries);
        YarnLockResult yarnLockResult = new YarnLockResult(packageJson, new HashMap<>(), "yarn.lock", yarnLock);

        // This should not throw an exception.
        DependencyGraph dependencyGraph = yarnTransformer.transform(yarnLockResult, false, false, false, new ArrayList<>());

        // Sanity check.
        Assertions.assertNotNull(dependencyGraph, "The dependency graph should not be null.");
        assertEquals(1, dependencyGraph.getRootDependencies().size(), "Only 'foo:1.0' should appear in the graph.");
        ExternalId fooExternalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "foo", "1.0");
        assertTrue(dependencyGraph.hasDependency(fooExternalId), "Missing the only expected dependency.");
    }

    // TODO add a test for yarn 1 workspaces??
}
