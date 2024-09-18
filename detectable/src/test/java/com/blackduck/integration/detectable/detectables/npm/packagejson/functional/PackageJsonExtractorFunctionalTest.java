package com.blackduck.integration.detectable.detectables.npm.packagejson.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

import com.blackduck.integration.detectable.annotations.FunctionalTest;
import com.blackduck.integration.detectable.util.graph.GraphAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.blackduck.integration.detectable.detectable.codelocation.CodeLocation;
import com.blackduck.integration.detectable.detectable.util.EnumListFilter;
import com.blackduck.integration.detectable.detectables.npm.NpmDependencyType;
import com.blackduck.integration.detectable.detectables.npm.packagejson.PackageJsonExtractor;
import com.blackduck.integration.detectable.extraction.Extraction;

@FunctionalTest
public class PackageJsonExtractorFunctionalTest {
    private File packageJsonFile;

    private ExternalId testDep1;
    private ExternalId testDep2;
    private ExternalId testDevDep1;
    private ExternalId testDevDep2;
    private ExternalId testPeerDep1;
    private ExternalId testPeerDep2;

    @BeforeEach
    void setUp() {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        testDep1 = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "name1", "version1");
        testDep2 = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "name2", "version2");
        testDevDep1 = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "nameDev1", "versionDev1");
        testDevDep2 = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "nameDev2", "versionDev2");
        testPeerDep1 = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "namePeer1", "versionPeer1");
        testPeerDep2 = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "namePeer2", "versionPeer2");

        packageJsonFile = new File("src/test/resources/detectables/functional/npm/package.json");
    }

    private PackageJsonExtractor createExtractor(NpmDependencyType... excludedTypes) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        EnumListFilter<NpmDependencyType> npmDependencyTypeFilter = EnumListFilter.fromExcluded(excludedTypes);
        return new PackageJsonExtractor(gson, new ExternalIdFactory(), npmDependencyTypeFilter);
    }

    @Test
    void extractWithNoDevDependencies() throws IOException {
        Extraction extraction = createExtractor(NpmDependencyType.PEER, NpmDependencyType.DEV).extract(packageJsonFile);
        assertEquals(1, extraction.getCodeLocations().size());
        CodeLocation codeLocation = extraction.getCodeLocations().get(0);
        DependencyGraph dependencyGraph = codeLocation.getDependencyGraph();

        GraphAssert graphAssert = new GraphAssert(Forge.NPMJS, dependencyGraph);
        graphAssert.hasRootDependency(testDep1);
        graphAssert.hasRootDependency(testDep2);
        graphAssert.hasNoDependency(testDevDep1);
        graphAssert.hasNoDependency(testDevDep2);
        graphAssert.hasNoDependency(testPeerDep1);
        graphAssert.hasNoDependency(testPeerDep2);
        graphAssert.hasRootSize(2);
    }

    @Test
    void extractWithDevDependencies() throws IOException {
        Extraction extraction = createExtractor(NpmDependencyType.PEER).extract(packageJsonFile);
        assertEquals(1, extraction.getCodeLocations().size());
        CodeLocation codeLocation = extraction.getCodeLocations().get(0);
        DependencyGraph dependencyGraph = codeLocation.getDependencyGraph();

        GraphAssert graphAssert = new GraphAssert(Forge.RUBYGEMS, dependencyGraph);
        graphAssert.hasRootDependency(testDep1);
        graphAssert.hasRootDependency(testDep2);
        graphAssert.hasRootDependency(testDevDep1);
        graphAssert.hasRootDependency(testDevDep2);
        graphAssert.hasNoDependency(testPeerDep1);
        graphAssert.hasNoDependency(testPeerDep2);
        graphAssert.hasRootSize(4);
    }

    @Test
    void extractWithPeerDependencies() throws IOException {
        Extraction extraction = createExtractor(NpmDependencyType.DEV).extract(packageJsonFile);
        assertEquals(1, extraction.getCodeLocations().size());
        CodeLocation codeLocation = extraction.getCodeLocations().get(0);
        DependencyGraph dependencyGraph = codeLocation.getDependencyGraph();

        GraphAssert graphAssert = new GraphAssert(Forge.NPMJS, dependencyGraph);
        graphAssert.hasRootDependency(testDep1);
        graphAssert.hasRootDependency(testDep2);
        graphAssert.hasRootDependency(testPeerDep1);
        graphAssert.hasRootDependency(testPeerDep2);
        graphAssert.hasNoDependency(testDevDep1);
        graphAssert.hasNoDependency(testDevDep2);
        graphAssert.hasRootSize(4);
    }
}
