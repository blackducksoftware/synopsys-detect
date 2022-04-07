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

// TODO: NpmDevExclusionTest and NpmPeerExclusionTest are basically the same and should be combined. JM-01/2022
@FunctionalTest
public class NpmDevExclusionTest {
    ExternalId childDev;
    ExternalId parentDev;
    String packageJsonText;
    String packageLockText;

    @BeforeEach
    void setup() {
        packageJsonText = FunctionalTestFiles.asString("/npm/dev-exclusion-test/package.json");
        packageLockText = FunctionalTestFiles.asString("/npm/dev-exclusion-test/package-lock.json");

        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        childDev = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "child-dev", "3.0.0");
        parentDev = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "parent-dev", "2.0.0");
    }

    private NpmLockfilePackager createPackager(NpmDependencyType... excludedTypes) {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        NpmLockfileGraphTransformer graphTransformer = new NpmLockfileGraphTransformer(EnumListFilter.fromExcluded(excludedTypes));
        NpmLockFileProjectIdTransformer projectIdTransformer = new NpmLockFileProjectIdTransformer(gson, externalIdFactory);
        return new NpmLockfilePackager(gson, externalIdFactory, projectIdTransformer, graphTransformer);
    }

    @Test
    public void testDevDependencyNotExists() {
        NpmPackagerResult result = createPackager(NpmDependencyType.DEV, NpmDependencyType.PEER).parseAndTransform(packageJsonText, packageLockText);
        GraphAssert graphAssert = new GraphAssert(Forge.NPMJS, result.getCodeLocation().getDependencyGraph());
        graphAssert.hasNoDependency(childDev);
        graphAssert.hasNoDependency(parentDev);
        graphAssert.hasRootSize(0);
    }

    @Test
    public void testDevDependencyExists() {
        NpmPackagerResult result = createPackager(NpmDependencyType.PEER).parseAndTransform(packageJsonText, packageLockText);
        GraphAssert graphAssert = new GraphAssert(Forge.NPMJS, result.getCodeLocation().getDependencyGraph());
        graphAssert.hasDependency(childDev);
        graphAssert.hasDependency(parentDev);
        graphAssert.hasRootSize(1);
    }
}
