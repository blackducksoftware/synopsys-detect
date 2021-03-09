package com.synopsys.integration.detectable.detectables.yarn.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import com.synopsys.integration.util.NameVersion;

@UnitTest
class YarnTransformerTest {
    public static final String WORKSPACE_DEP_SUFFIX = "-dep";
    private static ExternalIdFactory externalIdFactory;
    private static YarnTransformer yarnTransformer;
    private static final List<NameVersion> noWorkspaces = new LinkedList<>();

    @BeforeAll
    static void setup() {
        externalIdFactory = new ExternalIdFactory();
        yarnTransformer = new YarnTransformer(externalIdFactory);
    }

    @Test
    void testExcludeDevDependencies() throws MissingExternalIdException {
        YarnLockResult yarnLockResult = buildTestYarnLockResult(noWorkspaces, noWorkspaces, true);

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
        YarnLockResult yarnLockResult = buildTestYarnLockResult(noWorkspaces, noWorkspaces, true);

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

    // TODO V1 tests too
    // TODO add tests where all workspaces are added as deps

    @Test
    void testYarn2WorkspacesFollowDependencies() throws MissingExternalIdException {

        List<NameVersion> workspaces = new LinkedList<>();
        workspaces.add(new NameVersion("workspace-a", "1.0.0"));
        YarnLockResult yarnLockResult = buildTestYarnLockResult(workspaces, noWorkspaces, true);
        // TODO add workspaces to test data below
        DependencyGraph dependencyGraph = yarnTransformer.transform(yarnLockResult, false, false, false, workspaces);
        assertEquals(3, dependencyGraph.getRootDependencies().size());
        ExternalId workspaceExternalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, workspaces.get(0).getName(),
            workspaces.get(0).getVersion());
        Set<Dependency> actualWorkspaceDeps = dependencyGraph.getChildrenForParent(workspaceExternalId);
        Dependency actualWorkspaceDep = actualWorkspaceDeps.iterator().next();
        assertEquals(workspaces.get(0).getName() + WORKSPACE_DEP_SUFFIX, actualWorkspaceDep.getName());
        assertEquals(workspaces.get(0).getVersion(), actualWorkspaceDep.getVersion());
    }

    // TODO populate these tests

    @Test
    void testYarn2WorkspacesAddAllWorkspaces() throws MissingExternalIdException {

    }

    @Test
    void testYarn1WorkspacesFollowDependencies() throws MissingExternalIdException {

    }

    @Test
    void testYarn1WorkspacesAddAllWorkspaces() throws MissingExternalIdException {
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

    // TODO should be able to pass workspaces that ARE deps, and workspaces that are NOT deps
    @NotNull
    private YarnLockResult buildTestYarnLockResult(List<NameVersion> workspacesThatAreDependencies, List<NameVersion> workspacesThatAreNotDependencies, boolean yarn2project) {
        PackageJson packageJson = new PackageJson();
        packageJson.dependencies = new HashMap<>();
        packageJson.dependencies.put("foo", "fooFuzzyVersion-1.0");
        for (NameVersion workspace : workspacesThatAreDependencies) {
            packageJson.dependencies.put(workspace.getName(), workspace.getVersion());
        }
        packageJson.devDependencies.put("bar", "barFuzzyVersion-1.0");

        // TODO if adding, should add project to yarn.lock too

        // yarn.lock: foo and bar both depend on yarn
        List<YarnLockEntryId> yarnLockEntryIdsFoo = Collections.singletonList(new YarnLockEntryId("foo", "fooFuzzyVersion-1.0"));
        List<YarnLockEntryId> yarnLockEntryIdsBar = Collections.singletonList(new YarnLockEntryId("bar", "barFuzzyVersion-1.0"));
        List<YarnLockEntryId> yarnLockEntryIdsYarn = Collections.singletonList(new YarnLockEntryId("yarn", "^1.22.4"));
        List<YarnLockDependency> dependencyRefToYarn = Collections.singletonList(new YarnLockDependency("yarn", "^1.22.4", false));
        List<YarnLockEntry> yarnLockEntries = new ArrayList<>();

        if (yarn2project) {
            List<YarnLockEntryId> projectEntryIds = Collections.singletonList(new YarnLockEntryId("project", "1.0.0"));
            List<YarnLockDependency> projectDependencies = new LinkedList<>();
            projectDependencies.add(new YarnLockDependency("foo", "fooFuzzyVersion-1.0", false));
            projectDependencies.add(new YarnLockDependency("bar", "barFuzzyVersion-1.0", false));
            // TODO add workspaces here as deps of project
            for (NameVersion workspace : workspacesThatAreDependencies) {
                projectDependencies.add(new YarnLockDependency(workspace.getName(), workspace.getVersion(), false));
            }
            yarnLockEntries.add(new YarnLockEntry(false, projectEntryIds, "1.0.0", projectDependencies));
        }
        Map<String, PackageJson> workspacePackageJsons = new HashMap<>();
        for (NameVersion workspace : workspacesThatAreDependencies) {
            String workspaceDepName = workspace.getName() + WORKSPACE_DEP_SUFFIX;
            List<YarnLockEntryId> yarnLockEntryIdsWkspEntryIds = Collections.singletonList(new YarnLockEntryId(workspace.getName(), workspace.getVersion()));
            List<YarnLockDependency> dependencyRefsToWkspDeps = Collections.singletonList(new YarnLockDependency(workspaceDepName, workspace.getVersion(), false));
            // Generate workspace PackageJson
            PackageJson workspacePackageJson = new PackageJson();
            workspacePackageJson.name = workspace.getName();
            workspacePackageJson.version = workspace.getVersion();
            workspacePackageJson.dependencies = new HashMap<>();
            workspacePackageJson.dependencies.put(workspaceDepName, workspace.getVersion());

            // TODO Need to understand/mimic YarnLockExtractor's algorithm for deciding when to generate workspacePackageJsons list
            /// TODO later? workspacePackageJson.devDependencies.put("wksp dev dep", "version");
            workspacePackageJsons.put(workspace.getName(), workspacePackageJson);

            if (yarn2project) {
                yarnLockEntries.add(new YarnLockEntry(false, yarnLockEntryIdsWkspEntryIds, workspace.getVersion(), dependencyRefsToWkspDeps));
            }
            // Add the workspace's dependency to yarn.lock
            List<YarnLockEntryId> wkspDepIds = Collections.singletonList(new YarnLockEntryId(workspaceDepName, workspace.getVersion()));
            yarnLockEntries.add(new YarnLockEntry(false, wkspDepIds, workspace.getVersion(), new ArrayList<>(0)));
        }
        yarnLockEntries.add(new YarnLockEntry(false, yarnLockEntryIdsFoo, "1.0", dependencyRefToYarn));
        yarnLockEntries.add(new YarnLockEntry(false, yarnLockEntryIdsBar, "1.0", dependencyRefToYarn));
        yarnLockEntries.add(new YarnLockEntry(false, yarnLockEntryIdsYarn, "1.22.5", new ArrayList<>()));
        String yarnLockVersion = null;
        if (yarn2project) {
            yarnLockVersion = "4";
        }
        YarnLock yarnLock = new YarnLock(yarnLockVersion, yarn2project, yarnLockEntries);
        YarnLockResult yarnLockResult = new YarnLockResult(packageJson, workspacePackageJsons, "yarn.lock", yarnLock);
        return yarnLockResult;
    }
}
