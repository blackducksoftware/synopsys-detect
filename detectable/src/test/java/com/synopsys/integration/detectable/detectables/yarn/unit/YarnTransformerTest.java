package com.synopsys.integration.detectable.detectables.yarn.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
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
import com.synopsys.integration.detectable.detectables.yarn.packagejson.NullSafePackageJson;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.WorkspacePackageJson;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLock;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockResult;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntry;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryId;
import com.synopsys.integration.detectable.detectables.yarn.workspace.YarnWorkspace;
import com.synopsys.integration.detectable.detectables.yarn.workspace.YarnWorkspaces;
import com.synopsys.integration.util.ExcludedIncludedWildcardFilter;
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
        YarnLockResult yarnLockResult = buildTestYarnLockResult(noWorkspaces, noWorkspaces, false, false);

        DependencyGraph dependencyGraph = yarnTransformer.generateDependencyGraph(yarnLockResult, true, false, new ArrayList<>(0), ExcludedIncludedWildcardFilter.EMPTY);

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
        YarnLockResult yarnLockResult = buildTestYarnLockResult(noWorkspaces, noWorkspaces, false, false);

        DependencyGraph dependencyGraph = yarnTransformer.generateDependencyGraph(yarnLockResult, false, false, new ArrayList<>(0), ExcludedIncludedWildcardFilter.EMPTY);

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
        YarnLockResult yarnLockResult = buildTestYarnLockResult(workspacesThatAreDependencies, workspacesThatAreNotDependencies, false, false);

        DependencyGraph dependencyGraph = yarnTransformer.generateDependencyGraph(yarnLockResult, false, false, workspacesThatAreDependencies, ExcludedIncludedWildcardFilter.EMPTY);

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
        YarnLockResult yarnLockResult = buildTestYarnLockResult(workspacesThatAreDependencies, workspacesThatAreNotDependencies, false, true);
        List<NameVersion> allWorkspaces = new LinkedList<>(workspacesThatAreDependencies);
        allWorkspaces.addAll(workspacesThatAreNotDependencies);

        DependencyGraph dependencyGraph = yarnTransformer.generateDependencyGraph(yarnLockResult, false, true, allWorkspaces, ExcludedIncludedWildcardFilter.EMPTY);

        assertEquals(4, dependencyGraph.getRootDependencies().size());
        // TODO make this a static class field
        Forge worspaceForge = new Forge("/", "detect-yarn-workspace");
        ExternalId workspaceExternalId = externalIdFactory.createNameVersionExternalId(worspaceForge, workspacesThatAreDependencies.get(0).getName(),
            workspacesThatAreDependencies.get(0).getVersion());
        Set<Dependency> actualWorkspaceDeps = dependencyGraph.getChildrenForParent(workspaceExternalId);
        Dependency actualWorkspaceDep = actualWorkspaceDeps.iterator().next();
        assertEquals(workspacesThatAreDependencies.get(0).getName() + WORKSPACE_DEP_SUFFIX, actualWorkspaceDep.getName());
        assertEquals(workspacesThatAreDependencies.get(0).getVersion(), actualWorkspaceDep.getVersion());
        workspaceExternalId = externalIdFactory.createNameVersionExternalId(worspaceForge, workspacesThatAreNotDependencies.get(0).getName(),
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
        YarnLockResult yarnLockResult = buildTestYarnLockResult(workspacesThatAreDependencies, workspacesThatAreNotDependencies, true, false);

        DependencyGraph dependencyGraph = yarnTransformer.generateDependencyGraph(yarnLockResult, false, true, workspacesThatAreDependencies, null);

        assertEquals(3, dependencyGraph.getRootDependencies().size());
        String targetWorkspaceName = workspacesThatAreDependencies.get(0).getName();
        boolean foundWorkspaceDep = false;
        boolean foundWorkspaceDevDep = false;
        for (Dependency workspaceCandidate : dependencyGraph.getRootDependencies()) {
            if (targetWorkspaceName.equals(workspaceCandidate.getExternalId().getName())) {
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
        YarnLockResult yarnLockResult = buildTestYarnLockResult(workspacesThatAreDependencies, workspacesThatAreNotDependencies, true, true);

        DependencyGraph dependencyGraph = yarnTransformer.generateDependencyGraph(yarnLockResult, false, true, workspacesThatAreDependencies, ExcludedIncludedWildcardFilter.EMPTY);

        assertEquals(4, dependencyGraph.getRootDependencies().size());
        String targetWorkspaceName = workspacesThatAreNotDependencies.get(0).getName();
        boolean foundWorkspaceDep = false;
        for (Dependency workspaceCandidate : dependencyGraph.getRootDependencies()) {
            if (targetWorkspaceName.equals(workspaceCandidate.getExternalId().getName())) {
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

        PackageJson rawPackageJson = new PackageJson();
        rawPackageJson.dependencies = new HashMap<>();
        rawPackageJson.dependencies.put("foo", "fooFuzzyVersion-1.0");
        NullSafePackageJson packageJson = new NullSafePackageJson(rawPackageJson);

        List<YarnLockEntryId> validYarnLockEntryIds = Collections.singletonList(new YarnLockEntryId("foo", "fooFuzzyVersion-1.0"));
        List<YarnLockDependency> validYarnLockDependencies = Collections.singletonList(new YarnLockDependency("yarn", "^1.22.4", false));
        List<YarnLockEntry> yarnLockEntries = Collections.singletonList(new YarnLockEntry(false, validYarnLockEntryIds, "1.0", validYarnLockDependencies));
        YarnLock yarnLock = new YarnLock(null, true, yarnLockEntries);
        YarnLockResult yarnLockResult = new YarnLockResult(packageJson, YarnWorkspaces.EMPTY, yarnLock);

        // This should not throw an exception.
        DependencyGraph dependencyGraph = yarnTransformer.generateDependencyGraph(yarnLockResult, false, false, new ArrayList<>(0), ExcludedIncludedWildcardFilter.EMPTY);

        // Sanity check.
        Assertions.assertNotNull(dependencyGraph, "The dependency graph should not be null.");
        assertEquals(1, dependencyGraph.getRootDependencies().size(), "Only 'foo:1.0' should appear in the graph.");
        ExternalId fooExternalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "foo", "1.0");
        assertTrue(dependencyGraph.hasDependency(fooExternalId), "Missing the only expected dependency.");
    }

    @NotNull
    private YarnLockResult buildTestYarnLockResult(List<NameVersion> workspacesThatAreDependencies, List<NameVersion> workspacesThatAreNotDependencies, boolean yarn1project, boolean includeAllWorkspaceDependencies) {
        PackageJson rawPackageJson = new PackageJson();
        rawPackageJson.dependencies = new HashMap<>();
        rawPackageJson.dependencies.put("foo", "fooFuzzyVersion-1.0");
        for (NameVersion workspace : workspacesThatAreDependencies) {
            rawPackageJson.dependencies.put(workspace.getName(), workspace.getVersion());
        }
        rawPackageJson.devDependencies.put("bar", "barFuzzyVersion-1.0");
        NullSafePackageJson packageJson = new NullSafePackageJson(rawPackageJson);

        // yarn.lock: foo and bar both depend on yarn
        List<YarnLockEntryId> yarnLockEntryIdsFoo = Collections.singletonList(new YarnLockEntryId("foo", "fooFuzzyVersion-1.0"));
        List<YarnLockEntryId> yarnLockEntryIdsBar = Collections.singletonList(new YarnLockEntryId("bar", "barFuzzyVersion-1.0"));
        List<YarnLockEntryId> yarnLockEntryIdsYarn = Collections.singletonList(new YarnLockEntryId("yarn", "^1.22.4"));
        List<YarnLockDependency> dependencyRefToYarn = Collections.singletonList(new YarnLockDependency("yarn", "^1.22.4", false));
        List<YarnLockEntry> yarnLockEntries = new LinkedList<>();

        if (!yarn1project) {
            List<YarnLockEntryId> projectEntryIds = Collections.singletonList(new YarnLockEntryId("project", "1.0.0"));
            List<YarnLockDependency> projectDependencies = new LinkedList<>();
            projectDependencies.add(new YarnLockDependency("foo", "fooFuzzyVersion-1.0", false));
            projectDependencies.add(new YarnLockDependency("bar", "barFuzzyVersion-1.0", false));
            for (NameVersion workspaceThatIsDependency : workspacesThatAreDependencies) {
                projectDependencies.add(new YarnLockDependency(workspaceThatIsDependency.getName(), workspaceThatIsDependency.getVersion(), false));
            }
            yarnLockEntries.add(new YarnLockEntry(false, projectEntryIds, "1.0.0", projectDependencies));
        }
        Collection<YarnWorkspace> workspacesByName = new LinkedList<>();
        List<NameVersion> allWorkspaces = new LinkedList<>(workspacesThatAreDependencies);
        allWorkspaces.addAll(workspacesThatAreNotDependencies);
        for (NameVersion workspace : allWorkspaces) {
            String workspaceDepName = workspace.getName() + WORKSPACE_DEP_SUFFIX;
            String workspaceDevDepName = workspace.getName() + "-dev" + WORKSPACE_DEP_SUFFIX;
            if (yarn1project || includeAllWorkspaceDependencies) {
                addWorkspacePackageJson(workspacesByName, workspace, workspaceDepName, workspaceDevDepName);
            }
            if (!yarn1project) {
                addWorkspaceToYarnLockEntries(yarnLockEntries, workspace, workspaceDepName);
            }
            addDependencyOfWorkspaceToYarnLockEntries(yarnLockEntries, workspace, workspaceDepName);
            addDependencyOfWorkspaceToYarnLockEntries(yarnLockEntries, workspace, workspaceDevDepName);
        }
        yarnLockEntries.add(new YarnLockEntry(false, yarnLockEntryIdsFoo, "1.0", dependencyRefToYarn));
        yarnLockEntries.add(new YarnLockEntry(false, yarnLockEntryIdsBar, "1.0", dependencyRefToYarn));
        yarnLockEntries.add(new YarnLockEntry(false, yarnLockEntryIdsYarn, "1.22.5", new LinkedList<>()));
        String yarnLockVersion = null;
        if (!yarn1project) {
            yarnLockVersion = "4";
        }
        YarnLock yarnLock = new YarnLock(yarnLockVersion, yarn1project, yarnLockEntries);
        YarnWorkspaces workspaceData = new YarnWorkspaces(workspacesByName);
        return new YarnLockResult(packageJson, workspaceData, yarnLock);
    }

    private void addWorkspaceToYarnLockEntries(List<YarnLockEntry> yarnLockEntries, NameVersion workspace, String workspaceDepName) {
        List<YarnLockDependency> dependencyRefsToWkspDeps = Arrays.asList(new YarnLockDependency(workspaceDepName, workspace.getVersion(), false));
        List<YarnLockEntryId> yarnLockEntryIdsWkspEntryIds = Arrays.asList(
            new YarnLockEntryId(workspace.getName(), workspace.getVersion()),
            new YarnLockEntryId(workspace.getName(), "workspace:packages/" + workspace.getName()));
        yarnLockEntries.add(new YarnLockEntry(false, yarnLockEntryIdsWkspEntryIds, workspace.getVersion(), dependencyRefsToWkspDeps));
    }

    private void addDependencyOfWorkspaceToYarnLockEntries(List<YarnLockEntry> yarnLockEntries, NameVersion workspace, String workspaceDepName) {
        List<YarnLockEntryId> wkspDepIds = Collections.singletonList(new YarnLockEntryId(workspaceDepName, workspace.getVersion()));
        yarnLockEntries.add(new YarnLockEntry(false, wkspDepIds, workspace.getVersion(), new LinkedList<>()));
    }

    private void addWorkspacePackageJson(Collection<YarnWorkspace> workspacesByName, NameVersion workspaceNameVersion, String workspaceDepName, String workspaceDevDepName) {
        PackageJson rawWorkspacePackageJson = new PackageJson();
        rawWorkspacePackageJson.name = workspaceNameVersion.getName();
        rawWorkspacePackageJson.version = workspaceNameVersion.getVersion();
        rawWorkspacePackageJson.dependencies = new HashMap<>();
        rawWorkspacePackageJson.dependencies.put(workspaceDepName, workspaceNameVersion.getVersion());
        rawWorkspacePackageJson.devDependencies.put(workspaceDevDepName, workspaceNameVersion.getVersion());
        NullSafePackageJson workspacePackageJson = new NullSafePackageJson(rawWorkspacePackageJson);
        WorkspacePackageJson locatedWorkspacePackageJson = new WorkspacePackageJson(null, workspacePackageJson, "packages/" + workspaceNameVersion.getName());
        YarnWorkspace workspace = new YarnWorkspace(new ExternalIdFactory(), locatedWorkspacePackageJson);
        workspacesByName.add(workspace);
    }
}
