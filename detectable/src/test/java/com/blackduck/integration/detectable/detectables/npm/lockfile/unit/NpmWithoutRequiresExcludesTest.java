package com.blackduck.integration.detectable.detectables.npm.lockfile.unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.blackduck.integration.detectable.detectables.npm.lockfile.model.NpmDependency;
import com.blackduck.integration.detectable.detectables.npm.lockfile.model.NpmProject;
import com.blackduck.integration.detectable.detectables.npm.lockfile.model.PackageLock;
import com.blackduck.integration.detectable.detectables.npm.lockfile.parse.NpmLockfileGraphTransformer;
import com.blackduck.integration.detectable.util.graph.GraphAssert;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.blackduck.integration.detectable.detectable.util.EnumListFilter;
import com.blackduck.integration.detectable.detectables.npm.NpmDependencyType;

public class NpmWithoutRequiresExcludesTest {
    @Test
    public void testDevDependencyExcluded() {
        PackageLock packageLock = new PackageLock();
        packageLock.packages = new HashMap<>();

        List<NpmDependency> resolvedDependencies = new ArrayList<>();
        resolvedDependencies.add(new NpmDependency("example", "1.0.0", true, true));
        NpmProject npmProject = new NpmProject(
            StringUtils.EMPTY,
            StringUtils.EMPTY,
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            resolvedDependencies
        );

        NpmLockfileGraphTransformer graphTransformer = new NpmLockfileGraphTransformer(
            EnumListFilter.fromExcluded(NpmDependencyType.DEV, NpmDependencyType.PEER)
        );
        DependencyGraph graph = graphTransformer.transform(packageLock, npmProject, Collections.emptyList(), null);

        GraphAssert graphAssert = new GraphAssert(Forge.NPMJS, graph);
        graphAssert.hasRootSize(0);
    }
}
