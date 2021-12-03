package com.synopsys.integration.detectable.detectables.npm.packagejson.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.npm.packagejson.PackageJsonExtractor;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

@FunctionalTest
public class PackageJsonExtractorFunctionalTest {
    private PackageJsonExtractor packageJsonExtractor;
    private InputStream packageJsonInputStream;

    private ExternalId testDep1;
    private ExternalId testDep2;
    private ExternalId testDevDep1;
    private ExternalId testDevDep2;
    private ExternalId testPeerDep1;
    private ExternalId testPeerDep2;

    @BeforeEach
    void setUp() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();

        testDep1 = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "name1", "version1");
        testDep2 = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "name2", "version2");
        testDevDep1 = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "nameDev1", "versionDev1");
        testDevDep2 = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "nameDev2", "versionDev2");
        testPeerDep1 = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "namePeer1", "versionPeer1");
        testPeerDep2 = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "namePeer2", "versionPeer2");

        packageJsonExtractor = new PackageJsonExtractor(gson, externalIdFactory);
        packageJsonInputStream = FunctionalTestFiles.asInputStream("/npm/package.json");
    }

    @Test
    void extractWithNoDevDependencies() {
        Extraction extraction = packageJsonExtractor.extract(packageJsonInputStream, false, false);
        assertEquals(1, extraction.getCodeLocations().size());
        CodeLocation codeLocation = extraction.getCodeLocations().get(0);
        DependencyGraph dependencyGraph = codeLocation.getDependencyGraph();

        GraphAssert graphAssert = new GraphAssert(Forge.RUBYGEMS, dependencyGraph);
        graphAssert.hasRootDependency(testDep1);
        graphAssert.hasRootDependency(testDep2);
        graphAssert.hasNoDependency(testDevDep1);
        graphAssert.hasNoDependency(testDevDep2);
        graphAssert.hasNoDependency(testPeerDep1);
        graphAssert.hasNoDependency(testPeerDep2);
        graphAssert.hasRootSize(2);
    }

    @Test
    void extractWithDevDependencies() {
        Extraction extraction = packageJsonExtractor.extract(packageJsonInputStream, true, false);
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
    void extractWithPeerDependencies() {
        Extraction extraction = packageJsonExtractor.extract(packageJsonInputStream, false, true);
        assertEquals(1, extraction.getCodeLocations().size());
        CodeLocation codeLocation = extraction.getCodeLocations().get(0);
        DependencyGraph dependencyGraph = codeLocation.getDependencyGraph();

        GraphAssert graphAssert = new GraphAssert(Forge.RUBYGEMS, dependencyGraph);
        graphAssert.hasRootDependency(testDep1);
        graphAssert.hasRootDependency(testDep2);
        graphAssert.hasRootDependency(testPeerDep1);
        graphAssert.hasRootDependency(testPeerDep2);
        graphAssert.hasNoDependency(testDevDep1);
        graphAssert.hasNoDependency(testDevDep2);
        graphAssert.hasRootSize(4);
    }

    @AfterEach
    void tearDown() throws IOException {
        packageJsonInputStream.close();
    }
}
