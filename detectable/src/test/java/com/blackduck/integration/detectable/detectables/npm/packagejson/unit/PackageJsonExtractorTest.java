package com.blackduck.integration.detectable.detectables.npm.packagejson.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.blackduck.integration.detectable.annotations.UnitTest;
import com.blackduck.integration.detectable.detectable.codelocation.CodeLocation;
import com.blackduck.integration.detectable.detectable.util.EnumListFilter;
import com.blackduck.integration.detectable.detectables.npm.NpmDependencyType;
import com.blackduck.integration.detectable.detectables.npm.packagejson.CombinedPackageJson;
import com.blackduck.integration.detectable.detectables.npm.packagejson.PackageJsonExtractor;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.blackduck.integration.detectable.util.graph.GraphAssert;

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
    
    @Test
    void extractWithNonNumericalCharacters() {
        CombinedPackageJson packageJson = new CombinedPackageJson();
        packageJson.getDependencies().put("foo", "1.0.0 - 2.9999.9999");
        packageJson.getDependencies().put("bar", ">=1.0.2 <2.1.2");
        packageJson.getDependencies().put("qux", "<1.0.0 || >=2.3.1 <2.4.5 || >=2.5.2 <3.0.0");
        packageJson.getDependencies().put("asd", "http://asdf.com/asdf.tar.gz");
        packageJson.getDependencies().put("til", "~1.2");
        packageJson.getDependencies().put("thr", "3.3.x");
        packageJson.getDependencies().put("myproject", "1.0.0-SNAPSHOT");
        
        Extraction extraction = createExtractor(NpmDependencyType.PEER).extract(packageJson);
        CodeLocation codeLocation = extraction.getCodeLocations().get(0);
        DependencyGraph dependencyGraph = codeLocation.getDependencyGraph();
        
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        ExternalId testCharDep1 = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "foo", "1.0.0");
        ExternalId testCharDep2 = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "bar", "1.0.2");
        ExternalId testCharDep3 = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "qux", "1.0.0");
        ExternalId testCharDep4 = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "asd", "http://asdf.com/asdf.tar.gz");
        ExternalId testCharDep5 = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "til", "1.2");
        ExternalId testCharDep6 = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "thr", "3.3.0");
        ExternalId testCharDep7 = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "myproject", "1.0.0");        
        
        GraphAssert graphAssert = new GraphAssert(Forge.NPMJS, dependencyGraph);
        graphAssert.hasRootDependency(testCharDep1);
        graphAssert.hasRootDependency(testCharDep2);
        graphAssert.hasRootDependency(testCharDep3);
        graphAssert.hasRootDependency(testCharDep4);
        graphAssert.hasRootDependency(testCharDep5);
        graphAssert.hasRootDependency(testCharDep6);
        graphAssert.hasRootDependency(testCharDep7);
        graphAssert.hasRootSize(7);
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
