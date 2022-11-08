package com.synopsys.integration.detectable.detectables.npm.lockfile.functional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.npm.NpmDependencyType;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockFileProjectIdTransformer;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockfileGraphTransformer;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockfilePackager;
import com.synopsys.integration.detectable.detectables.npm.lockfile.result.NpmPackagerResult;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

@FunctionalTest
public class NpmPeerExclusionTest {
    ExternalId childPeer;
    ExternalId parentPeer;
    String packageJsonText;
    String packageLockText;

    @BeforeEach
    void setup() {
        packageJsonText = FunctionalTestFiles.asString("/npm/peer-exclusion-test/package.json");
        packageLockText = FunctionalTestFiles.asString("/npm/peer-exclusion-test/package-lock.json");

        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        childPeer = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "child-peer", "3.0.0");
        parentPeer = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "parent-peer", "2.0.0");
    }

    private NpmLockfilePackager createPackager(NpmDependencyType... excludedTypes) {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        NpmLockfileGraphTransformer graphTransformer = new NpmLockfileGraphTransformer(EnumListFilter.fromExcluded(excludedTypes));
        NpmLockFileProjectIdTransformer projectIdTransformer = new NpmLockFileProjectIdTransformer(gson, externalIdFactory);
        return new NpmLockfilePackager(gson, externalIdFactory, projectIdTransformer, graphTransformer);
    }

    @Test
    public void testPeerDependencyNotExists() {
        NpmPackagerResult result = createPackager(NpmDependencyType.DEV, NpmDependencyType.PEER).parseAndTransform(packageJsonText, packageLockText);
        GraphAssert graphAssert = new GraphAssert(Forge.NPMJS, result.getCodeLocation().getDependencyGraph());
        graphAssert.hasNoDependency(childPeer);
        graphAssert.hasNoDependency(parentPeer);
        graphAssert.hasRootSize(0);
    }

    @Test
    public void testPeerDependencyExists() {
        NpmPackagerResult result = createPackager(NpmDependencyType.DEV).parseAndTransform(packageJsonText, packageLockText);
        GraphAssert graphAssert = new GraphAssert(Forge.NPMJS, result.getCodeLocation().getDependencyGraph());
        graphAssert.hasDependency(childPeer);
        graphAssert.hasDependency(parentPeer);
        graphAssert.hasRootSize(1);
    }
}
