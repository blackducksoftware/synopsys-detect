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
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmDependencyType;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmLockYaml;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmPackageInfo;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.process.PnpmLinkedPackageResolver;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.process.PnpmYamlTransformer;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonFiles;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonReader;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class PnpmYamlTransformerTest {
    NameVersion projectNameVersion = new NameVersion("name", "version");
    PnpmLinkedPackageResolver linkedPackageResolver = new PnpmLinkedPackageResolver(new File(""), new PackageJsonFiles(new PackageJsonReader(new Gson())));
    File pnpmLockYamlFile = new File("something");

    private PnpmYamlTransformer createTransformer(PnpmDependencyType... excludedDependencyTypes) {
        EnumListFilter<PnpmDependencyType> dependencyTypeFilter = EnumListFilter.fromExcluded(excludedDependencyTypes);
        return new PnpmYamlTransformer(dependencyTypeFilter);
    }

    @Test
    public void testGenerateCodeLocation() throws IntegrationException {
        PnpmLockYaml pnpmLockYaml = createPnpmLockYaml();
        PnpmYamlTransformer transformer = createTransformer();
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
        PnpmYamlTransformer transformer = createTransformer(PnpmDependencyType.DEV);
        PnpmLockYaml pnpmLockYaml = createPnpmLockYaml();
        DependencyGraph dependencyGraph = transformer.generateCodeLocation(pnpmLockYamlFile, pnpmLockYaml, projectNameVersion, linkedPackageResolver).getDependencyGraph();
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.NPMJS, dependencyGraph);
        graphAssert.hasRootSize(3);
        graphAssert.hasNoDependency("devDep", "2.0.0");
    }

    @Test
    public void testExcludeOptionalDependencies() throws IntegrationException {
        PnpmLockYaml pnpmLockYaml = createPnpmLockYaml();
        PnpmYamlTransformer transformer = createTransformer(PnpmDependencyType.OPTIONAL);
        DependencyGraph dependencyGraph = transformer.generateCodeLocation(pnpmLockYamlFile, pnpmLockYaml, projectNameVersion, linkedPackageResolver).getDependencyGraph();
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.NPMJS, dependencyGraph);
        graphAssert.hasRootSize(3);
        graphAssert.hasNoDependency("optDep", "3.0.0");
    }

    @Test
    public void testThrowExceptionOnNullPackagesSection() {
        PnpmLockYaml pnpmLockYaml = createPnpmLockYaml();
        PnpmYamlTransformer transformer = createTransformer();
        pnpmLockYaml.packages = null;
        assertThrows(
            IntegrationException.class,
            () -> transformer.generateCodeLocation(pnpmLockYamlFile, pnpmLockYaml, projectNameVersion, linkedPackageResolver)
        );
    }

    @Test
    public void testNoFailureOnNullNameVersion() throws IntegrationException {
        PnpmLockYaml pnpmLockYaml = createPnpmLockYaml();
        PnpmYamlTransformer transformer = createTransformer();
        transformer.generateCodeLocation(pnpmLockYamlFile, pnpmLockYaml, null, linkedPackageResolver);
    }

    @Test
    public void testParseFileDependencies() throws IntegrationException {
        PnpmLockYaml pnpmLockYaml = createPnpmLockYaml();
        PnpmYamlTransformer transformer = createTransformer(PnpmDependencyType.DEV, PnpmDependencyType.OPTIONAL);
        DependencyGraph dependencyGraph = transformer.generateCodeLocation(pnpmLockYamlFile, pnpmLockYaml, projectNameVersion, linkedPackageResolver).getDependencyGraph();
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.NPMJS, dependencyGraph);
        graphAssert.hasRootSize(2);
        graphAssert.hasDependency("fileDep", "1.0.0");
    }

    private PnpmLockYaml createPnpmLockYaml() {
        PnpmLockYaml pnpmLockYaml = new PnpmLockYaml();

        Map<String, String> dependencies = new HashMap<>();
        dependencies.put("dep", "1.0.0");
        dependencies.put("fileDep", "file:fileDep-1.0.0.tgz");
        pnpmLockYaml.dependencies = dependencies;

        Map<String, String> devDependencies = new HashMap<>();
        devDependencies.put("devDep", "2.0.0");
        pnpmLockYaml.devDependencies = devDependencies;

        Map<String, String> optionalDependencies = new HashMap<>();
        optionalDependencies.put("optDep", "3.0.0");
        pnpmLockYaml.optionalDependencies = optionalDependencies;

        Map<String, PnpmPackageInfo> packages = new HashMap<>();

        PnpmPackageInfo dep = new PnpmPackageInfo();
        Map<String, String> depDependencies = new HashMap<>();
        depDependencies.put("transitive", "1.1.0");
        dep.dependencies = depDependencies;
        packages.put("/dep/1.0.0", dep);

        PnpmPackageInfo devDep = new PnpmPackageInfo();
        devDep.dev = true;
        packages.put("/devDep/2.0.0", devDep);

        PnpmPackageInfo optDep = new PnpmPackageInfo();
        optDep.optional = true;
        packages.put("/optDep/3.0.0", optDep);

        PnpmPackageInfo transitive = new PnpmPackageInfo();
        packages.put("/transitive/1.1.0", transitive);

        PnpmPackageInfo fileDep = new PnpmPackageInfo();
        fileDep.name = "fileDep";
        fileDep.version = "1.0.0";
        packages.put("file:fileDep-1.0.0.tgz", fileDep);

        pnpmLockYaml.packages = packages;

        return pnpmLockYaml;
    }
}
