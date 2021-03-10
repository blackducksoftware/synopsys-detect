package com.synopsys.integration.detectable.detectables.yarn.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    // Not yet covered by these tests: yarn 1 workspaces' dev dependencies specified in workspace package.json

    @Test
    void testExcludeDevDependencies() throws MissingExternalIdException {
        YarnLockResult yarnLockResult = buildTestYarnLockResult(noWorkspaces, noWorkspaces, true, false);

        DependencyGraph dependencyGraph = yarnTransformer.transform(yarnLockResult, true, false, false, new LinkedList<>());

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
        YarnLockResult yarnLockResult = buildTestYarnLockResult(noWorkspaces, noWorkspaces, true, false);

        DependencyGraph dependencyGraph = yarnTransformer.transform(yarnLockResult, false, false, false, new LinkedList<>());

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

    @Test
    void testYarn2WorkspacesJustDependencies() throws MissingExternalIdException {
        List<NameVersion> workspacesThatAreDependencies = new LinkedList<>();
        workspacesThatAreDependencies.add(new NameVersion("workspace-isdep", "1.0.0"));
        List<NameVersion> workspacesThatAreNotDependencies = new LinkedList<>();
        workspacesThatAreNotDependencies.add(new NameVersion("workspace-notdep", "1.0.0"));
        YarnLockResult yarnLockResult = buildTestYarnLockResult(workspacesThatAreDependencies, workspacesThatAreNotDependencies, true, false);

        DependencyGraph dependencyGraph = yarnTransformer.transform(yarnLockResult, false, false, false, workspacesThatAreDependencies);

        assertEquals(3, dependencyGraph.getRootDependencies().size());
        ExternalId workspaceExternalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, workspacesThatAreDependencies.get(0).getName(),
            workspacesThatAreDependencies.get(0).getVersion());
        Set<Dependency> actualWorkspaceDeps = dependencyGraph.getChildrenForParent(workspaceExternalId);
        Dependency actualWorkspaceDep = actualWorkspaceDeps.iterator().next();
        assertEquals(workspacesThatAreDependencies.get(0).getName() + WORKSPACE_DEP_SUFFIX, actualWorkspaceDep.getName());
        assertEquals(workspacesThatAreDependencies.get(0).getVersion(), actualWorkspaceDep.getVersion());
    }

    @Test
    void testYarn2WorkspacesAll() throws MissingExternalIdException {
        List<NameVersion> workspacesThatAreDependencies = new LinkedList<>();
        workspacesThatAreDependencies.add(new NameVersion("workspace-isdep", "1.0.0"));
        List<NameVersion> workspacesThatAreNotDependencies = new LinkedList<>();
        workspacesThatAreNotDependencies.add(new NameVersion("workspace-notdep", "1.0.0"));
        YarnLockResult yarnLockResult = buildTestYarnLockResult(workspacesThatAreDependencies, workspacesThatAreNotDependencies, true, true);
        List<NameVersion> allWorkspaces = new LinkedList<>(workspacesThatAreDependencies);
        allWorkspaces.addAll(workspacesThatAreNotDependencies);

        DependencyGraph dependencyGraph = yarnTransformer.transform(yarnLockResult, false, true, false, allWorkspaces);

        assertEquals(4, dependencyGraph.getRootDependencies().size());
        ExternalId workspaceExternalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, workspacesThatAreDependencies.get(0).getName(),
            workspacesThatAreDependencies.get(0).getVersion());
        Set<Dependency> actualWorkspaceDeps = dependencyGraph.getChildrenForParent(workspaceExternalId);
        Dependency actualWorkspaceDep = actualWorkspaceDeps.iterator().next();
        assertEquals(workspacesThatAreDependencies.get(0).getName() + WORKSPACE_DEP_SUFFIX, actualWorkspaceDep.getName());
        assertEquals(workspacesThatAreDependencies.get(0).getVersion(), actualWorkspaceDep.getVersion());

        workspaceExternalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, workspacesThatAreNotDependencies.get(0).getName(),
            workspacesThatAreNotDependencies.get(0).getVersion());
        actualWorkspaceDeps = dependencyGraph.getChildrenForParent(workspaceExternalId);
        actualWorkspaceDep = actualWorkspaceDeps.iterator().next();
        assertEquals(workspacesThatAreNotDependencies.get(0).getName() + WORKSPACE_DEP_SUFFIX, actualWorkspaceDep.getName());
        assertEquals(workspacesThatAreNotDependencies.get(0).getVersion(), actualWorkspaceDep.getVersion());
    }

    @Test
    void testYarn1WorkspacesJustDependencies() throws MissingExternalIdException {
        List<NameVersion> workspacesThatAreDependencies = new LinkedList<>();
        workspacesThatAreDependencies.add(new NameVersion("workspace-isdep", "1.0.0"));
        List<NameVersion> workspacesThatAreNotDependencies = new LinkedList<>();
        workspacesThatAreNotDependencies.add(new NameVersion("workspace-notdep", "1.0.0"));
        YarnLockResult yarnLockResult = buildTestYarnLockResult(workspacesThatAreDependencies, workspacesThatAreNotDependencies, false, false);

        DependencyGraph dependencyGraph = yarnTransformer.transform(yarnLockResult, false, false, true, workspacesThatAreDependencies);

        assertEquals(3, dependencyGraph.getRootDependencies().size());
        String targetWorkspaceId = workspacesThatAreDependencies.get(0).getName() + "@" + workspacesThatAreDependencies.get(0).getVersion();
        boolean foundWorkspaceDep = false;
        boolean foundWorkspaceDevDep = false;
        for (Dependency workspaceCandidate : dependencyGraph.getRootDependencies()) {
            if (targetWorkspaceId.equals(workspaceCandidate.getExternalId().getName())) {
                Set<Dependency> actualWorkspaceDeps = dependencyGraph.getChildrenForParent(workspaceCandidate);
                for (Dependency actualWorkspaceDep : actualWorkspaceDeps) {
                    if (actualWorkspaceDep.getExternalId().getName().equals(workspacesThatAreDependencies.get(0).getName() + WORKSPACE_DEP_SUFFIX)) {
                        assertEquals(workspacesThatAreDependencies.get(0).getVersion(), actualWorkspaceDep.getVersion());
                        foundWorkspaceDep = true;
                    } else if (actualWorkspaceDep.getExternalId().getName().equals(workspacesThatAreDependencies.get(0).getName() + "-dev" + WORKSPACE_DEP_SUFFIX)) {
                        assertEquals(workspacesThatAreDependencies.get(0).getVersion(), actualWorkspaceDep.getVersion());
                        foundWorkspaceDevDep = true;
                    }
                }
            }
        }
        assertTrue(foundWorkspaceDep);
        assertTrue(foundWorkspaceDevDep);
    }

    @Test
    void testYarn1WorkspacesAll() throws MissingExternalIdException {
        List<NameVersion> workspacesThatAreDependencies = new LinkedList<>();
        workspacesThatAreDependencies.add(new NameVersion("workspace-isdep", "1.0.0"));
        List<NameVersion> workspacesThatAreNotDependencies = new LinkedList<>();
        workspacesThatAreNotDependencies.add(new NameVersion("workspace-notdep", "1.0.0"));
        YarnLockResult yarnLockResult = buildTestYarnLockResult(workspacesThatAreDependencies, workspacesThatAreNotDependencies, false, true);

        DependencyGraph dependencyGraph = yarnTransformer.transform(yarnLockResult, false, true, true, workspacesThatAreDependencies);

        assertEquals(4, dependencyGraph.getRootDependencies().size());
        String targetWorkspaceId = workspacesThatAreNotDependencies.get(0).getName() + "@" + workspacesThatAreNotDependencies.get(0).getVersion();
        boolean foundWorkspaceDep = false;
        for (Dependency workspaceCandidate : dependencyGraph.getRootDependencies()) {
            if (targetWorkspaceId.equals(workspaceCandidate.getExternalId().getName())) {
                Set<Dependency> actualWorkspaceDeps = dependencyGraph.getChildrenForParent(workspaceCandidate);
                Dependency actualWorkspaceDep = actualWorkspaceDeps.iterator().next();
                assertEquals(workspacesThatAreNotDependencies.get(0).getName() + WORKSPACE_DEP_SUFFIX, actualWorkspaceDep.getName());
                assertEquals(workspacesThatAreNotDependencies.get(0).getVersion(), actualWorkspaceDep.getVersion());
                foundWorkspaceDep = true;
            }
        }
        assertTrue(foundWorkspaceDep);
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
        DependencyGraph dependencyGraph = yarnTransformer.transform(yarnLockResult, false, false, false, new LinkedList<>());

        // Sanity check.
        Assertions.assertNotNull(dependencyGraph, "The dependency graph should not be null.");
        assertEquals(1, dependencyGraph.getRootDependencies().size(), "Only 'foo:1.0' should appear in the graph.");
        ExternalId fooExternalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "foo", "1.0");
        assertTrue(dependencyGraph.hasDependency(fooExternalId), "Missing the only expected dependency.");
    }

    @NotNull
    private YarnLockResult buildTestYarnLockResult(List<NameVersion> workspacesThatAreDependencies, List<NameVersion> workspacesThatAreNotDependencies, boolean yarn2project, boolean includeAllWorkspaceDependencies) {
        PackageJson packageJson = new PackageJson();
        packageJson.dependencies = new HashMap<>();
        packageJson.dependencies.put("foo", "fooFuzzyVersion-1.0");
        for (NameVersion workspace : workspacesThatAreDependencies) {
            packageJson.dependencies.put(workspace.getName(), workspace.getVersion());
        }
        packageJson.devDependencies.put("bar", "barFuzzyVersion-1.0");

        // yarn.lock: foo and bar both depend on yarn
        List<YarnLockEntryId> yarnLockEntryIdsFoo = Collections.singletonList(new YarnLockEntryId("foo", "fooFuzzyVersion-1.0"));
        List<YarnLockEntryId> yarnLockEntryIdsBar = Collections.singletonList(new YarnLockEntryId("bar", "barFuzzyVersion-1.0"));
        List<YarnLockEntryId> yarnLockEntryIdsYarn = Collections.singletonList(new YarnLockEntryId("yarn", "^1.22.4"));
        List<YarnLockDependency> dependencyRefToYarn = Collections.singletonList(new YarnLockDependency("yarn", "^1.22.4", false));
        List<YarnLockEntry> yarnLockEntries = new LinkedList<>();

        if (yarn2project) {
            List<YarnLockEntryId> projectEntryIds = Collections.singletonList(new YarnLockEntryId("project", "1.0.0"));
            List<YarnLockDependency> projectDependencies = new LinkedList<>();
            projectDependencies.add(new YarnLockDependency("foo", "fooFuzzyVersion-1.0", false));
            projectDependencies.add(new YarnLockDependency("bar", "barFuzzyVersion-1.0", false));
            for (NameVersion workspaceThatIsDependency : workspacesThatAreDependencies) {
                projectDependencies.add(new YarnLockDependency(workspaceThatIsDependency.getName(), workspaceThatIsDependency.getVersion(), false));
            }
            yarnLockEntries.add(new YarnLockEntry(false, projectEntryIds, "1.0.0", projectDependencies));
        }
        Map<String, PackageJson> workspacePackageJsons = new HashMap<>();
        List<NameVersion> allWorkspaces = new LinkedList<>(workspacesThatAreDependencies);
        allWorkspaces.addAll(workspacesThatAreNotDependencies);
        for (NameVersion workspace : allWorkspaces) {
            String workspaceDepName = workspace.getName() + WORKSPACE_DEP_SUFFIX;
            String workspaceDevDepName = workspace.getName() + "-dev" + WORKSPACE_DEP_SUFFIX;
            if (!yarn2project || includeAllWorkspaceDependencies) {
                addWorkspacePackageJson(workspacePackageJsons, workspace, workspaceDepName, workspaceDevDepName);
            }
            if (yarn2project) {
                addWorkspaceToYarnLockEntries(yarnLockEntries, workspace, workspaceDepName);
            }
            addDependencyOfWorkspaceToYarnLockEntries(yarnLockEntries, workspace, workspaceDepName);
        }
        yarnLockEntries.add(new YarnLockEntry(false, yarnLockEntryIdsFoo, "1.0", dependencyRefToYarn));
        yarnLockEntries.add(new YarnLockEntry(false, yarnLockEntryIdsBar, "1.0", dependencyRefToYarn));
        yarnLockEntries.add(new YarnLockEntry(false, yarnLockEntryIdsYarn, "1.22.5", new LinkedList<>()));
        String yarnLockVersion = null;
        if (yarn2project) {
            yarnLockVersion = "4";
        }
        YarnLock yarnLock = new YarnLock(yarnLockVersion, yarn2project, yarnLockEntries);
        YarnLockResult yarnLockResult = new YarnLockResult(packageJson, workspacePackageJsons, "yarn.lock", yarnLock);
        return yarnLockResult;
    }

    private void addWorkspaceToYarnLockEntries(List<YarnLockEntry> yarnLockEntries, NameVersion workspace, String workspaceDepName) {
        List<YarnLockDependency> dependencyRefsToWkspDeps = Collections.singletonList(new YarnLockDependency(workspaceDepName, workspace.getVersion(), false));
        List<YarnLockEntryId> yarnLockEntryIdsWkspEntryIds = Collections.singletonList(new YarnLockEntryId(workspace.getName(), workspace.getVersion()));
        yarnLockEntries.add(new YarnLockEntry(false, yarnLockEntryIdsWkspEntryIds, workspace.getVersion(), dependencyRefsToWkspDeps));
    }

    private void addDependencyOfWorkspaceToYarnLockEntries(List<YarnLockEntry> yarnLockEntries, NameVersion workspace, String workspaceDepName) {
        List<YarnLockEntryId> wkspDepIds = Collections.singletonList(new YarnLockEntryId(workspaceDepName, workspace.getVersion()));
        yarnLockEntries.add(new YarnLockEntry(false, wkspDepIds, workspace.getVersion(), new LinkedList<>()));
    }

    private void addWorkspacePackageJson(Map<String, PackageJson> workspacePackageJsons, NameVersion workspace, String workspaceDepName, String workspaceDevDepName) {
        PackageJson workspacePackageJson = new PackageJson();
        workspacePackageJson.name = workspace.getName();
        workspacePackageJson.version = workspace.getVersion();
        workspacePackageJson.dependencies = new HashMap<>();
        workspacePackageJson.dependencies.put(workspaceDepName, workspace.getVersion());
        workspacePackageJson.devDependencies.put(workspaceDevDepName, workspace.getVersion());
        workspacePackageJsons.put(workspace.getName(), workspacePackageJson);
    }
}
