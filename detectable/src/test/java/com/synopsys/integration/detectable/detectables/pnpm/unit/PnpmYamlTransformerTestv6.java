package com.synopsys.integration.detectable.detectables.pnpm.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmDependencyInfo;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmDependencyType;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmLockYamlv6;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmPackageInfov6;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.process.PnpmLinkedPackageResolver;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.process.PnpmYamlTransformerv6;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonFiles;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonReader;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class PnpmYamlTransformerTestv6 {
    NameVersion projectNameVersion = new NameVersion("name", "version");
    PnpmLinkedPackageResolver linkedPackageResolver = new PnpmLinkedPackageResolver(new File(""), new PackageJsonFiles(new PackageJsonReader(new Gson())));
    File pnpmLockYamlFile = new File("something");

    private PnpmYamlTransformerv6 createTransformer(PnpmDependencyType... excludedDependencyTypes) {
        EnumListFilter<PnpmDependencyType> dependencyTypeFilter = EnumListFilter.fromExcluded(excludedDependencyTypes);
        return new PnpmYamlTransformerv6(dependencyTypeFilter);
    }

    @Test
    public void testGenerateCodeLocation() throws IntegrationException {
        PnpmLockYamlv6 pnpmLockYaml = createPnpmLockYaml();
        PnpmYamlTransformerv6 transformer = createTransformer();
        CodeLocation codeLocation = transformer.generateCodeLocation(pnpmLockYamlFile, pnpmLockYaml, projectNameVersion, linkedPackageResolver);

        assertTrue(codeLocation.getExternalId().isPresent(), "Expected the codelocation to produce an ExternalId.");
        assertEquals("name", codeLocation.getExternalId().get().getName());
        assertEquals("version", codeLocation.getExternalId().get().getVersion());

        DependencyGraph dependencyGraph = codeLocation.getDependencyGraph();
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.NPMJS, dependencyGraph);
        graphAssert.hasRootSize(4);
        graphAssert.hasRootDependency("dep", "1.0.0");
        graphAssert.hasRootDependency("devDep", "2.0.0");
        graphAssert.hasRootDependency("optDep", "3.0.0");
        graphAssert.hasParentChildRelationship("dep", "1.0.0", "transitive", "1.1.0");
    }

    @Test
    public void testExcludeDevDependencies() throws IntegrationException {
        PnpmYamlTransformerv6 transformer = createTransformer(PnpmDependencyType.DEV);
        PnpmLockYamlv6 pnpmLockYaml = createPnpmLockYaml();
        DependencyGraph dependencyGraph = transformer.generateCodeLocation(pnpmLockYamlFile, pnpmLockYaml, projectNameVersion, linkedPackageResolver).getDependencyGraph();
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.NPMJS, dependencyGraph);
        graphAssert.hasRootSize(3);
        graphAssert.hasNoDependency("devDep", "2.0.0");
    }

    @Test
    public void testExcludeOptionalDependencies() throws IntegrationException {
        PnpmLockYamlv6 pnpmLockYaml = createPnpmLockYaml();
        PnpmYamlTransformerv6 transformer = createTransformer(PnpmDependencyType.OPTIONAL);
        DependencyGraph dependencyGraph = transformer.generateCodeLocation(pnpmLockYamlFile, pnpmLockYaml, projectNameVersion, linkedPackageResolver).getDependencyGraph();
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.NPMJS, dependencyGraph);
        graphAssert.hasRootSize(3);
        graphAssert.hasNoDependency("optDep", "3.0.0");
    }

    @Test
    public void testThrowExceptionOnNullPackagesSection() {
        PnpmLockYamlv6 pnpmLockYaml = createPnpmLockYaml();
        PnpmYamlTransformerv6 transformer = createTransformer();
        pnpmLockYaml.packages = null;
        assertThrows(
            IntegrationException.class,
            () -> transformer.generateCodeLocation(pnpmLockYamlFile, pnpmLockYaml, projectNameVersion, linkedPackageResolver)
        );
    }

    @Test
    public void testNoFailureOnNullNameVersion() throws IntegrationException {
        PnpmLockYamlv6 pnpmLockYaml = createPnpmLockYaml();
        PnpmYamlTransformerv6 transformer = createTransformer();
        transformer.generateCodeLocation(pnpmLockYamlFile, pnpmLockYaml, null, linkedPackageResolver);
    }

    @Test
    public void testParseFileDependencies() throws IntegrationException {
        PnpmLockYamlv6 pnpmLockYaml = createPnpmLockYaml();
        PnpmYamlTransformerv6 transformer = createTransformer(PnpmDependencyType.DEV, PnpmDependencyType.OPTIONAL);
        DependencyGraph dependencyGraph = transformer.generateCodeLocation(pnpmLockYamlFile, pnpmLockYaml, projectNameVersion, linkedPackageResolver).getDependencyGraph();
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.NPMJS, dependencyGraph);
        graphAssert.hasRootSize(2);
        graphAssert.hasDependency("fileDep", "1.0.0");
    }

    private PnpmLockYamlv6 createPnpmLockYaml() {
        PnpmLockYamlv6 pnpmLockYaml = new PnpmLockYamlv6();

        Map<String, PnpmDependencyInfo> dependencies = new HashMap<>();
        PnpmDependencyInfo dependency1 = new PnpmDependencyInfo();
        dependency1.version = "1.0.0";
        dependencies.put("dep", dependency1);
        PnpmDependencyInfo dependency2 = new PnpmDependencyInfo();
        dependency2.version = "file:fileDep-1.0.0.tgz";
        dependencies.put("fileDep", dependency2);
        pnpmLockYaml.dependencies = dependencies;

        Map<String, PnpmDependencyInfo> devDependencies = new HashMap<>();
        PnpmDependencyInfo devDependency1 = new PnpmDependencyInfo();
        devDependency1.version = "2.0.0";
        devDependencies.put("devDep", devDependency1);
        pnpmLockYaml.devDependencies = devDependencies;

        Map<String, PnpmDependencyInfo> optionalDependencies = new HashMap<>();
        PnpmDependencyInfo optDependency1 = new PnpmDependencyInfo();
        optDependency1.version = "3.0.0";
        optionalDependencies.put("optDep", optDependency1);
        pnpmLockYaml.optionalDependencies = optionalDependencies;

        Map<String, PnpmPackageInfov6> packages = new HashMap<>();

        PnpmPackageInfov6 dep = new PnpmPackageInfov6();
        Map<String, String> depDependencies = new HashMap<>();
        depDependencies.put("transitive", "1.1.0");
        dep.dependencies = depDependencies;
        packages.put("/dep@1.0.0", dep);

        PnpmPackageInfov6 devDep = new PnpmPackageInfov6();
        devDep.dev = true;
        packages.put("/devDep@2.0.0", devDep);

        PnpmPackageInfov6 optDep = new PnpmPackageInfov6();
        optDep.optional = true;
        packages.put("/optDep@3.0.0", optDep);

        PnpmPackageInfov6 transitive = new PnpmPackageInfov6();
        packages.put("/transitive@1.1.0", transitive);

        PnpmPackageInfov6 fileDep = new PnpmPackageInfov6();
        fileDep.name = "fileDep";
        fileDep.version = "1.0.0";
        packages.put("file:fileDep-1.0.0.tgz", fileDep);

        pnpmLockYaml.packages = packages;

        return pnpmLockYaml;
    }
}
