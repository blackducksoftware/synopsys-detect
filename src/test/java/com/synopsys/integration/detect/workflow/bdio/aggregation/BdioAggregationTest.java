package com.synopsys.integration.detect.workflow.bdio.aggregation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.BdioReader;
import com.synopsys.integration.bdio.BdioTransformer;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.ProjectDependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.util.NameVersion;

class BdioAggregationTest {
    private static Gson gson;
    private static BdioTransformer bdioTransformer;
    private static File sourceDir = new File("src/test/resources/workflow/bdio/aggregation/src");
    private static List<DetectCodeLocation> inputCodelocations;

    @BeforeAll
    static void setup() throws IOException {
        gson = new Gson();
        bdioTransformer = new BdioTransformer();
        List<String> inputBdioFilenames = Arrays.asList(
            "basic_multiproject_0_0_0_SNAPSHOT_com_synopsys_integration_basic_multiproject_0_0_0_SNAPSHOT_gradle_bom.jsonld",
            "basic_multiproject_0_0_0_SNAPSHOT_subprojectone_basic_multiproject_subprojectone_unspecified_gradle_bom.jsonld",
            "basic_multiproject_0_0_0_SNAPSHOT_subprojecttwo_basic_multiproject_subprojecttwo_unspecified_gradle_bom.jsonld"
        );
        sourceDir = new File("src/test/resources/workflow/bdio/aggregation/src");
        inputCodelocations = readInputCodeLocations(inputBdioFilenames, sourceDir);
    }

    @Test
    void testSubProjectMode() {
        FullAggregateGraphCreator fullAggregateGraphCreator = new FullAggregateGraphCreator();

        DependencyGraph aggregatedGraph = fullAggregateGraphCreator.aggregateCodeLocations(
            sourceDir,
            new NameVersion("aggregate-test", "test"),
            inputCodelocations
        );

        assertEquals(3, aggregatedGraph.getDirectDependencies().size());
        assertTrue(aggregatedGraph.getDirectDependencies().contains(genProjectDependency("com.synopsys.integration", "basic-multiproject", "0.0.0-SNAPSHOT")));
        assertTrue(aggregatedGraph.getDirectDependencies().contains(genProjectDependency("basic-multiproject", "subprojectone", "unspecified")));
        assertTrue(aggregatedGraph.getDirectDependencies().contains(genProjectDependency("basic-multiproject", "subprojecttwo", "unspecified")));

        Dependency subProjectOne = aggregatedGraph.getDependency(genProjectExternalId("basic-multiproject", "subprojectone", "unspecified"));
        Set<Dependency> subProjectOneDependencies = aggregatedGraph.getChildrenForParent(subProjectOne);
        assertTrue(subProjectOneDependencies.contains(Dependency.FACTORY.createMavenDependency("junit", "junit", "4.12")));
        assertTrue(subProjectOneDependencies.contains(Dependency.FACTORY.createMavenDependency("joda-time", "joda-time", "2.2")));

        assertTrue(subProjectOne instanceof ProjectDependency);
    }

    @NotNull
    private static List<DetectCodeLocation> readInputCodeLocations(List<String> inputBdioFilenames, File sourceDir) throws IOException {
        File inputBdioDir = new File("src/test/resources/workflow/bdio/aggregation/input");
        List<DetectCodeLocation> inputCodelocations = new LinkedList<>();
        for (String inputBdioFilename : inputBdioFilenames) {
            File bdioFile = new File(inputBdioDir, inputBdioFilename);
            SimpleBdioDocument simpleBdioDocument;
            try (InputStream bdioInputStream = new FileInputStream(bdioFile); BdioReader bdioReader = new BdioReader(gson, bdioInputStream)) {
                simpleBdioDocument = bdioReader.readSimpleBdioDocument();
            }

            ExternalId externalId = simpleBdioDocument.getProject().bdioExternalIdentifier.externalIdMetaData;
            ProjectDependency projectDependency = new ProjectDependency(externalId);
            DependencyGraph dependencyGraph = bdioTransformer.transformToDependencyGraph(projectDependency, simpleBdioDocument.getProject(), simpleBdioDocument.getComponents());
            DetectCodeLocation detectCodeLocation = DetectCodeLocation.forCreator(dependencyGraph, sourceDir, externalId, "testcreator");
            inputCodelocations.add(detectCodeLocation);
        }
        return inputCodelocations;
    }

    @NotNull
    private Dependency genProjectDependency(String group, String artifact, String version) {
        ExternalId extId = genProjectExternalId(group, artifact, version);
        return new Dependency(artifact, version, extId);
    }

    @NotNull
    private ExternalId genProjectExternalId(String group, String artifact, String version) {
        ExternalId extId = new ExternalId(Forge.MAVEN);
        List<String> moduleNames = Arrays.asList(group, artifact, version, "-testcreator");
        String[] moduleNamesArray = new String[moduleNames.size()];
        moduleNames.toArray(moduleNamesArray);
        extId.setModuleNames(moduleNamesArray);
        return extId;
    }
}
