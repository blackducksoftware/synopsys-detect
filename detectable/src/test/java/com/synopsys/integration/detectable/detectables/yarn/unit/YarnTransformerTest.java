package com.synopsys.integration.detectable.detectables.yarn.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.detectables.yarn.YarnDependencyType;
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
    private static ExternalIdFactory externalIdFactory = new ExternalIdFactory();
    private static final List<NameVersion> noWorkspaces = new LinkedList<>();

    private YarnTransformer createTransformer(YarnDependencyType... excludedTypes) {
        externalIdFactory = new ExternalIdFactory();
        EnumListFilter<YarnDependencyType> yarnDependencyTypeFilter = EnumListFilter.fromExcluded(excludedTypes);
        return new YarnTransformer(externalIdFactory, yarnDependencyTypeFilter);
    }

    // Not yet covered by these tests: yarn 1 workspaces' dev dependencies specified in workspace package.json

    @Test
    void testExcludeDevDependencies() throws MissingExternalIdException {
        YarnLockResult yarnLockResult = buildTestYarnLockResult(noWorkspaces, noWorkspaces, false);

        List<CodeLocation> codeLocations = createTransformer(YarnDependencyType.NON_PRODUCTION).generateCodeLocations(
            yarnLockResult,
            new ArrayList<>(0),
            ExcludedIncludedWildcardFilter.EMPTY
        );

        assertEquals(1, codeLocations.size());
        CodeLocation codeLocation = codeLocations.get(0);
        DependencyGraph dependencyGraph = codeLocation.getDependencyGraph();
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
        YarnLockResult yarnLockResult = buildTestYarnLockResult(noWorkspaces, noWorkspaces, false);

        List<CodeLocation> codeLocations = createTransformer().generateCodeLocations(yarnLockResult, new ArrayList<>(0), ExcludedIncludedWildcardFilter.EMPTY);

        assertEquals(1, codeLocations.size());
        CodeLocation codeLocation = codeLocations.get(0);
        DependencyGraph dependencyGraph = codeLocation.getDependencyGraph();
        assertEquals(2, dependencyGraph.getRootDependencies().size());

        ExternalId fooExternalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "foo", "1.0");
        assertTrue(dependencyGraph.hasDependency(fooExternalId));

        assertTrue(
            dependencyGraph.getRootDependencies().stream()
                .map(Dependency::getExternalId)
                .anyMatch(fooExternalId::equals)
        );

        ExternalId barExternalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "bar", "1.0");
        assertTrue(dependencyGraph.hasDependency(barExternalId));
        assertTrue(
            dependencyGraph.getRootDependencies().stream()
                .map(Dependency::getExternalId)
                .anyMatch(barExternalId::equals)
        );

        ExternalId yarnExternalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "yarn", "1.22.5");
        assertTrue(dependencyGraph.hasDependency(yarnExternalId));
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
        List<CodeLocation> codeLocations = createTransformer().generateCodeLocations(yarnLockResult, new ArrayList<>(0), ExcludedIncludedWildcardFilter.EMPTY);

        // Sanity check.
        assertEquals(1, codeLocations.size());
        CodeLocation codeLocation = codeLocations.get(0);
        DependencyGraph dependencyGraph = codeLocation.getDependencyGraph();
        Assertions.assertNotNull(dependencyGraph, "The dependency graph should not be null.");
        assertEquals(1, dependencyGraph.getRootDependencies().size(), "Only 'foo:1.0' should appear in the graph.");
        ExternalId fooExternalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "foo", "1.0");
        assertTrue(dependencyGraph.hasDependency(fooExternalId), "Missing the only expected dependency.");
    }

    @Test
    void testAllWorkspacesYarnV1() throws MissingExternalIdException {
        doAllWorkspacesTest(true);
    }

    @Test
    void testAllWorkspacesYarnV2() throws MissingExternalIdException {
        doAllWorkspacesTest(false);
    }

    private void doAllWorkspacesTest(boolean yarn1Project) throws MissingExternalIdException {
        // Unless filtered out, even workspaces that are not dependencies should be included
        List<NameVersion> workspacesThatAreDependencies = new LinkedList<>();
        workspacesThatAreDependencies.add(new NameVersion("workspace-isdep", "1.0.0"));
        List<NameVersion> workspacesThatAreNotDependencies = new LinkedList<>();
        workspacesThatAreNotDependencies.add(new NameVersion("workspace-notdep", "1.0.0"));
        YarnLockResult yarnLockResult = buildTestYarnLockResult(workspacesThatAreDependencies, workspacesThatAreNotDependencies, yarn1Project);

        List<CodeLocation> codeLocations = createTransformer().generateCodeLocations(yarnLockResult, new ArrayList<>(), ExcludedIncludedWildcardFilter.EMPTY);

        assertEquals(3, codeLocations.size());
        Iterator<CodeLocation> codeLocationIterator = codeLocations.iterator();
        CodeLocation rootProjectCodeLocation = codeLocationIterator.next();
        assertFalse(rootProjectCodeLocation.getExternalId().isPresent());
        DependencyGraph rootProjectDependencyGraph = rootProjectCodeLocation.getDependencyGraph();
        assertEquals(2, rootProjectDependencyGraph.getRootDependencies().size());
        List<String> dependencyNames = rootProjectDependencyGraph.getRootDependencies().stream()
            .map(Dependency::getName)
            .collect(Collectors.toList());
        assertTrue(dependencyNames.contains("foo"));
        assertTrue(dependencyNames.contains("bar"));

        for (int i = 1; i < 3; i++) {
            CodeLocation workspaceCodeLocation = codeLocationIterator.next();
            assertTrue(workspaceCodeLocation.getExternalId().get().getName().startsWith("packages/workspace-"));
            assertTrue(workspaceCodeLocation.getExternalId().get().getName().endsWith("dep"));
            assertEquals("local", workspaceCodeLocation.getExternalId().get().getVersion());
            assertEquals("npmjs", workspaceCodeLocation.getExternalId().get().getForge().getName());

            List<String> workspaceDependencyNames = workspaceCodeLocation.getDependencyGraph().getRootDependencies().stream()
                .map(Dependency::getName)
                .collect(Collectors.toList());
            String workspaceName = StringUtils.substringAfter(workspaceCodeLocation.getExternalId().get().getName(), "packages/");
            assertTrue(workspaceDependencyNames.contains(workspaceName + "-dep"));
            assertTrue(workspaceDependencyNames.contains(workspaceName + "-dev-dep"));
        }
    }

    @NotNull
    private YarnLockResult buildTestYarnLockResult(List<NameVersion> workspacesThatAreDependencies, List<NameVersion> workspacesThatAreNotDependencies, boolean yarn1project) {
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
            addWorkspacePackageJson(workspacesByName, workspace, workspaceDepName, workspaceDevDepName);
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
        List<YarnLockDependency> dependencyRefsToWkspDeps = Collections.singletonList(new YarnLockDependency(workspaceDepName, workspace.getVersion(), false));
        List<YarnLockEntryId> yarnLockEntryIdsWkspEntryIds = Arrays.asList(
            new YarnLockEntryId(workspace.getName(), workspace.getVersion()),
            new YarnLockEntryId(workspace.getName(), "workspace:packages/" + workspace.getName())
        );
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
        YarnWorkspace workspace = new YarnWorkspace(locatedWorkspacePackageJson);
        workspacesByName.add(workspace);
    }
}
