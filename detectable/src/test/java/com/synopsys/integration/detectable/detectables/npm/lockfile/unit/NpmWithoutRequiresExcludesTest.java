package com.synopsys.integration.detectable.detectables.npm.lockfile.unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.npm.NpmDependencyType;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmDependency;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmProject;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.PackageLock;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockfileGraphTransformer;
import com.synopsys.integration.detectable.util.ExternalIdCreator;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

import jdk.internal.joptsimple.internal.Strings;

public class NpmWithoutRequiresExcludesTest {
    @Test
    public void testDevDependencyExcluded() {
        PackageLock packageLock = new PackageLock();
        packageLock.dependencies = new HashMap<>();

        List<NpmDependency> resolvedDependencies = new ArrayList<>();
        resolvedDependencies.add(new NpmDependency("example", "1.0.0", true, true));
        NpmProject npmProject = new NpmProject(Strings.EMPTY, Strings.EMPTY, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), resolvedDependencies);

        NpmLockfileGraphTransformer graphTransformer = new NpmLockfileGraphTransformer(
            ExternalIdCreator.sharedFactory,
            EnumListFilter.fromExcluded(NpmDependencyType.DEV, NpmDependencyType.PEER)
        );
        MutableDependencyGraph graph = graphTransformer.transform(packageLock, npmProject, Collections.emptyList());

        GraphAssert graphAssert = new GraphAssert(Forge.NPMJS, graph);
        graphAssert.hasRootSize(0);
    }
}
