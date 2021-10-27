package com.synopsys.integration.detectable.detectables.pnpm.unit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.enums.DependencyType;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.PnpmDependencyFilter;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.PnpmYamlTransformer;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmLockYaml;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmPackage;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class PnpmYamlTransformerTest {
    private PnpmLockYaml pnpmLockYaml = createPnpmLockYaml();
    PnpmYamlTransformer pnpmTransformer = new PnpmYamlTransformer(new ExternalIdFactory());
    NameVersion projectNameVersion = new NameVersion("name", "version");

    @Test
    public void testGenerateCodeLocation() throws IntegrationException {
        CodeLocation codeLocation = pnpmTransformer.generateCodeLocation(pnpmLockYaml, new PnpmDependencyFilter(Arrays.asList(DependencyType.APP, DependencyType.DEV, DependencyType.OPTIONAL)), projectNameVersion);

        Assertions.assertEquals("name", codeLocation.getExternalId().get().getName());
        Assertions.assertEquals("version", codeLocation.getExternalId().get().getVersion());

        DependencyGraph dependencyGraph = codeLocation.getDependencyGraph();
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.NPMJS, dependencyGraph);
        graphAssert.hasRootSize(3);
        graphAssert.hasRootDependency("dep", "1.0.0");
        graphAssert.hasRootDependency("devDep", "2.0.0");
        graphAssert.hasRootDependency("optDep", "3.0.0");
        graphAssert.hasParentChildRelationship("dep", "1.0.0", "transitive", "1.1.0");
    }

    @Test
    public void testExcludeDependencies() throws IntegrationException {
        DependencyGraph dependencyGraph = pnpmTransformer.generateCodeLocation(pnpmLockYaml, new PnpmDependencyFilter(Arrays.asList(DependencyType.DEV, DependencyType.OPTIONAL)), projectNameVersion).getDependencyGraph();
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.NPMJS, dependencyGraph);
        graphAssert.hasRootSize(2);
    }

    @Test
    public void testExcludeDevDependencies() throws IntegrationException {
        DependencyGraph dependencyGraph = pnpmTransformer.generateCodeLocation(pnpmLockYaml, new PnpmDependencyFilter(Arrays.asList(DependencyType.APP, DependencyType.OPTIONAL)), projectNameVersion).getDependencyGraph();
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.NPMJS, dependencyGraph);
        graphAssert.hasRootSize(2);
        graphAssert.hasNoDependency("devDep", "2.0.0");
    }

    @Test
    public void testExcludeOptionalDependencies() throws IntegrationException {
        DependencyGraph dependencyGraph = pnpmTransformer.generateCodeLocation(pnpmLockYaml, new PnpmDependencyFilter(Arrays.asList(DependencyType.APP, DependencyType.DEV)), projectNameVersion).getDependencyGraph();
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.NPMJS, dependencyGraph);
        graphAssert.hasRootSize(2);
        graphAssert.hasNoDependency("optDep", "3.0.0");
    }

    @Test
    public void testThrowExceptionOnNullPackagesSection() {
        PnpmLockYaml pnpmLockYaml = createPnpmLockYaml();
        pnpmLockYaml.packages = null;
        try {
            pnpmTransformer.generateCodeLocation(pnpmLockYaml, new PnpmDependencyFilter(Arrays.asList(DependencyType.APP, DependencyType.DEV, DependencyType.OPTIONAL)), projectNameVersion);
        } catch (IntegrationException e) {
        }
    }

    @Test
    public void testThrowExceptionIfNoDirectDepenciesInLockFile() {
        PnpmLockYaml pnpmLockYaml = createPnpmLockYaml();
        pnpmLockYaml.dependencies = null;
        pnpmLockYaml.devDependencies = null;
        pnpmLockYaml.optionalDependencies = null;
        try {
            pnpmTransformer.generateCodeLocation(pnpmLockYaml, new PnpmDependencyFilter(Arrays.asList(DependencyType.APP, DependencyType.DEV, DependencyType.OPTIONAL)), projectNameVersion);
        } catch (IntegrationException e) {
        }
    }

    @Test
    public void testNoFailureOnNullNameVersion() throws IntegrationException {
        pnpmTransformer.generateCodeLocation(pnpmLockYaml, new PnpmDependencyFilter(Arrays.asList(DependencyType.APP, DependencyType.DEV, DependencyType.OPTIONAL)), null);
    }

    private PnpmLockYaml createPnpmLockYaml() {
        PnpmLockYaml pnpmLockYaml = new PnpmLockYaml();

        Map<String, String> dependencies = new HashMap<>();
        dependencies.put("dep", "1.0.0");
        pnpmLockYaml.dependencies = dependencies;

        Map<String, String> devDependencies = new HashMap<>();
        devDependencies.put("devDep", "2.0.0");
        pnpmLockYaml.devDependencies = devDependencies;

        Map<String, String> optionalDependencies = new HashMap<>();
        optionalDependencies.put("optDep", "3.0.0");
        pnpmLockYaml.optionalDependencies = optionalDependencies;

        Map<String, PnpmPackage> packages = new HashMap<>();

        PnpmPackage dep = new PnpmPackage();
        Map<String, String> depDependencies = new HashMap<>();
        depDependencies.put("transitive", "1.1.0");
        dep.dependencies = depDependencies;
        packages.put("/dep/1.0.0", dep);

        PnpmPackage devDep = new PnpmPackage();
        devDep.dev = true;
        packages.put("/devDep/2.0.0", devDep);

        PnpmPackage optDep = new PnpmPackage();
        optDep.optional = true;
        packages.put("/optDep/3.0.0", optDep);

        PnpmPackage transitive = new PnpmPackage();
        packages.put("/transitive/1.1.0", transitive);

        pnpmLockYaml.packages = packages;

        return pnpmLockYaml;
    }
}
