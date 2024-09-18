package com.blackduck.integration.detectable.detectables.npm.lockfile.functional;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import com.blackduck.integration.detectable.annotations.FunctionalTest;
import com.blackduck.integration.detectable.detectables.npm.lockfile.parse.NpmLockFileProjectIdTransformer;
import com.blackduck.integration.detectable.detectables.npm.lockfile.parse.NpmLockfileGraphTransformer;
import com.blackduck.integration.detectable.detectables.npm.lockfile.parse.NpmLockfilePackager;
import com.blackduck.integration.detectable.detectables.npm.lockfile.result.NpmPackagerResult;
import com.blackduck.integration.detectable.util.FunctionalTestFiles;
import com.blackduck.integration.detectable.util.graph.GraphAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.blackduck.integration.detectable.detectable.util.EnumListFilter;
import com.blackduck.integration.detectable.detectables.npm.NpmDependencyType;

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
    public void testDevDependencyNotExists() throws IOException {
        NpmPackagerResult result = createPackager(NpmDependencyType.DEV, NpmDependencyType.PEER).parseAndTransform(null, packageJsonText, packageLockText);
        GraphAssert graphAssert = new GraphAssert(Forge.NPMJS, result.getCodeLocation().getDependencyGraph());
        graphAssert.hasNoDependency(childDev);
        graphAssert.hasNoDependency(parentDev);
        graphAssert.hasRootSize(0);
    }

    @Test
    public void testDevDependencyExists() throws IOException {
        NpmPackagerResult result = createPackager(NpmDependencyType.PEER).parseAndTransform(null, packageJsonText, packageLockText);
        GraphAssert graphAssert = new GraphAssert(Forge.NPMJS, result.getCodeLocation().getDependencyGraph());
        graphAssert.hasDependency(childDev);
        graphAssert.hasDependency(parentDev);
        graphAssert.hasRootSize(1);
    }
    
    @Test
    public void testWorkspaceDevDependencyNotExists() throws IOException {        
        String rootPackagePath = System.getProperty("user.dir") + "/src/test/resources/detectables/functional/npm/dev-exclusion-workspace-test/package.json";
        String packageJsonText = FunctionalTestFiles.asString("/npm/dev-exclusion-workspace-test/package.json");
        String packageLockText = FunctionalTestFiles.asString("/npm/dev-exclusion-workspace-test/package-lock.json");
        
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        ExternalId workspaceDev = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "chai", "1.10.0");
        
        NpmPackagerResult result = createPackager(NpmDependencyType.DEV).parseAndTransform(rootPackagePath, packageJsonText, packageLockText);
        boolean hasRootDependency = result.getCodeLocation().getDependencyGraph().getRootDependencies().stream()
                .map(Dependency::getExternalId)
                .anyMatch(workspaceDev::equals);
        Assertions.assertFalse(hasRootDependency);
    }
}
