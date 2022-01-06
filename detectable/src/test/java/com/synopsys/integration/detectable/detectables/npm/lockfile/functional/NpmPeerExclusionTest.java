package com.synopsys.integration.detectable.detectables.npm.lockfile.functional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockFileProjectIdTransformer;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockfilePackager;
import com.synopsys.integration.detectable.detectables.npm.lockfile.result.NpmPackagerResult;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockfileGraphTransformer;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

@FunctionalTest
public class NpmPeerExclusionTest {
    ExternalId childPeer;
    ExternalId parentPeer;
    NpmLockfilePackager npmLockfilePackager;
    String packageJsonText;
    String packageLockText;

    @BeforeEach
    void setup() {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        NpmLockfileGraphTransformer graphTransformer = new NpmLockfileGraphTransformer(gson, externalIdFactory);
        NpmLockFileProjectIdTransformer projectIdTransformer = new NpmLockFileProjectIdTransformer(gson, externalIdFactory);
        npmLockfilePackager = new NpmLockfilePackager(gson, externalIdFactory, projectIdTransformer, graphTransformer);

        packageJsonText = FunctionalTestFiles.asString("/npm/peer-exclusion-test/package.json");
        packageLockText = FunctionalTestFiles.asString("/npm/peer-exclusion-test/package-lock.json");

        childPeer = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "child-peer", "3.0.0");
        parentPeer = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "parent-peer", "2.0.0");
    }

    @Test
    public void testPeerDependencyNotExists() {
        NpmPackagerResult result = npmLockfilePackager.parseAndTransform(packageJsonText, packageLockText, false, false);
        GraphAssert graphAssert = new GraphAssert(Forge.NPMJS, result.getCodeLocation().getDependencyGraph());
        graphAssert.hasNoDependency(childPeer);
        graphAssert.hasNoDependency(parentPeer);
        graphAssert.hasRootSize(0);
    }

    @Test
    public void testPeerDependencyExists() {
        NpmPackagerResult result = npmLockfilePackager.parseAndTransform(packageJsonText, packageLockText, false, true);
        GraphAssert graphAssert = new GraphAssert(Forge.NPMJS, result.getCodeLocation().getDependencyGraph());
        graphAssert.hasDependency(childPeer);
        graphAssert.hasDependency(parentPeer);
        graphAssert.hasRootSize(1);
    }
}
