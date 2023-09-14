package com.synopsys.integration.detectable.detectables.npm.packagejson.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.npm.NpmDependencyType;
import com.synopsys.integration.detectable.detectables.npm.packagejson.CombinedPackageJson;
import com.synopsys.integration.detectable.detectables.npm.packagejson.PackageJsonExtractor;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

@UnitTest
class PackageJsonExtractorTest {
    private ExternalId testDep1;
    private ExternalId testDep2;
    private ExternalId testDevDep1;
    private ExternalId testDevDep2;

    @BeforeEach
    void setUp() {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        testDep1 = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "name1", "version1");
        testDep2 = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "name2", "version2");
        testDevDep1 = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "nameDev1", "versionDev1");
        testDevDep2 = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "nameDev2", "versionDev2");
    }

    private PackageJsonExtractor createExtractor(NpmDependencyType... excludedTypes) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        EnumListFilter<NpmDependencyType> npmDependencyTypeFilter = EnumListFilter.fromExcluded(excludedTypes);
        return new PackageJsonExtractor(gson, new ExternalIdFactory(), npmDependencyTypeFilter);
    }

    @Test
    void extractWithNoDevOrPeerDependencies() {
        CombinedPackageJson packageJson = createPackageJson();
        Extraction extraction = createExtractor(NpmDependencyType.DEV, NpmDependencyType.PEER).extract(packageJson);
        assertEquals(1, extraction.getCodeLocations().size());
        CodeLocation codeLocation = extraction.getCodeLocations().get(0);
        DependencyGraph dependencyGraph = codeLocation.getDependencyGraph();

        GraphAssert graphAssert = new GraphAssert(Forge.NPMJS, dependencyGraph);
        graphAssert.hasRootDependency(testDep1);
        graphAssert.hasRootDependency(testDep2);
        graphAssert.hasNoDependency(testDevDep1);
        graphAssert.hasNoDependency(testDevDep2);
        graphAssert.hasRootSize(2);
    }

    @Test
    void extractWithDevNoPeerDependencies() {
        CombinedPackageJson packageJson = createPackageJson();
        Extraction extraction = createExtractor(NpmDependencyType.PEER).extract(packageJson);
        assertEquals(1, extraction.getCodeLocations().size());
        CodeLocation codeLocation = extraction.getCodeLocations().get(0);
        DependencyGraph dependencyGraph = codeLocation.getDependencyGraph();

        GraphAssert graphAssert = new GraphAssert(Forge.NPMJS, dependencyGraph);
        graphAssert.hasRootDependency(testDep1);
        graphAssert.hasRootDependency(testDep2);
        graphAssert.hasRootDependency(testDevDep1);
        graphAssert.hasRootDependency(testDevDep2);
        graphAssert.hasRootSize(4);
    }

    private CombinedPackageJson createPackageJson() {
        CombinedPackageJson combinedPackageJson = new CombinedPackageJson();

        combinedPackageJson.setName("test");
        combinedPackageJson.setVersion("test-version");

        combinedPackageJson.getDependencies().put(testDep1.getName(), testDep1.getVersion());
        combinedPackageJson.getDependencies().put(testDep2.getName(), testDep2.getVersion());
        combinedPackageJson.getDevDependencies().put(testDevDep1.getName(), testDevDep1.getVersion());
        combinedPackageJson.getDevDependencies().put(testDevDep2.getName(), testDevDep2.getVersion());

        return combinedPackageJson;
    }
}
