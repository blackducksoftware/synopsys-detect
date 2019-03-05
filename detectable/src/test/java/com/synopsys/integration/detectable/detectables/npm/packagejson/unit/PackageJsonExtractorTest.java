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
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.npm.packagejson.PackageJsonExtractor;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

@UnitTest
class PackageJsonExtractorTest {
    private PackageJsonExtractor packageJsonExtractor;

    private ExternalId testDep1;
    private ExternalId testDep2;
    private ExternalId testDevDep1;
    private ExternalId testDevDep2;

    @BeforeEach
    void setUp() {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

        testDep1 = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "name1", "version1");
        testDep2 = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "name2", "version2");
        testDevDep1 = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "nameDev1", "versionDev1");
        testDevDep2 = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "nameDev2", "versionDev2");
        packageJsonExtractor = new PackageJsonExtractor(gson, externalIdFactory);
    }

    @Test
    void extractWithNoDevDependencies() {
        final PackageJson packageJson = createPackageJson();
        final Extraction extraction = packageJsonExtractor.extract(packageJson, false);
        assertEquals(1, extraction.getCodeLocations().size());
        final CodeLocation codeLocation = extraction.getCodeLocations().get(0);
        final DependencyGraph dependencyGraph = codeLocation.getDependencyGraph();

        final GraphAssert graphAssert = new GraphAssert(Forge.RUBYGEMS, dependencyGraph);
        graphAssert.hasRootDependency(testDep1);
        graphAssert.hasRootDependency(testDep2);
        graphAssert.hasNoDependency(testDevDep1);
        graphAssert.hasNoDependency(testDevDep2);
        graphAssert.hasRootSize(2);
    }

    @Test
    void extractWithDevDependencies() {
        final PackageJson packageJson = createPackageJson();
        final Extraction extraction = packageJsonExtractor.extract(packageJson, true);
        assertEquals(1, extraction.getCodeLocations().size());
        final CodeLocation codeLocation = extraction.getCodeLocations().get(0);
        final DependencyGraph dependencyGraph = codeLocation.getDependencyGraph();

        final GraphAssert graphAssert = new GraphAssert(Forge.RUBYGEMS, dependencyGraph);
        graphAssert.hasRootDependency(testDep1);
        graphAssert.hasRootDependency(testDep2);
        graphAssert.hasRootDependency(testDevDep1);
        graphAssert.hasRootDependency(testDevDep2);
        graphAssert.hasRootSize(4);
    }

    private PackageJson createPackageJson() {
        final PackageJson packageJson = new PackageJson();

        packageJson.name = "test";
        packageJson.version = "test-version";
        packageJson.dependencies = new HashMap<>();
        packageJson.devDependencies = new HashMap<>();

        packageJson.dependencies.put(testDep1.name, testDep1.version);
        packageJson.dependencies.put(testDep2.name, testDep2.version);
        packageJson.devDependencies.put(testDevDep1.name, testDevDep1.version);
        packageJson.devDependencies.put(testDevDep2.name, testDevDep2.version);

        return packageJson;
    }
}