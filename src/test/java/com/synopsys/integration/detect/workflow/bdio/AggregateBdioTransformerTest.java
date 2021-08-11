package com.synopsys.integration.detect.workflow.bdio;

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

import com.synopsys.integration.detect.workflow.bdio.aggregation.AggregateModeDirectOperation;
import com.synopsys.integration.detect.workflow.bdio.aggregation.AggregateModeTransitiveOperation;
import com.synopsys.integration.detect.workflow.bdio.aggregation.FullAggregateGraph;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.BdioReader;
import com.synopsys.integration.bdio.BdioTransformer;
import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;

class AggregateBdioTransformerTest {
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
    void testTransitiveMode() throws DetectUserFriendlyException {

        DependencyGraph aggregatedGraph = new AggregateModeTransitiveOperation(new FullAggregateGraph(new SimpleBdioFactory())).aggregateCodeLocations(sourceDir, inputCodelocations);

        assertEquals(3, aggregatedGraph.getRootDependencies().size());
        assertTrue(aggregatedGraph.getRootDependencies().contains(genProjectDependency("com.synopsys.integration", "basic-multiproject", "0.0.0-SNAPSHOT")));
        assertTrue(aggregatedGraph.getRootDependencies().contains(genProjectDependency("basic-multiproject", "subprojectone", "unspecified")));
        assertTrue(aggregatedGraph.getRootDependencies().contains(genProjectDependency("basic-multiproject", "subprojecttwo", "unspecified")));

        Dependency subProjectOne = aggregatedGraph.getDependency(genProjectExternalId("basic-multiproject", "subprojectone", "unspecified"));
        Set<Dependency> subProjectOneDependencies = aggregatedGraph.getChildrenForParent(subProjectOne);
        assertTrue(subProjectOneDependencies.contains(genComponentDependency("junit", "junit", "4.12")));
        assertTrue(subProjectOneDependencies.contains(genComponentDependency("joda-time", "joda-time", "2.2")));
    }

    @Test
    void testDirectMode() throws DetectUserFriendlyException {

        DependencyGraph aggregatedGraph = new AggregateModeDirectOperation(new SimpleBdioFactory()).aggregateCodeLocations(sourceDir, inputCodelocations);

        assertEquals(2, aggregatedGraph.getRootDependencies().size());
        assertTrue(aggregatedGraph.getRootDependencies().contains(genComponentDependency("junit", "junit", "4.12")));
        assertTrue(aggregatedGraph.getRootDependencies().contains(genComponentDependency("joda-time", "joda-time", "2.2")));
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
            DependencyGraph dependencyGraph = bdioTransformer.transformToDependencyGraph(simpleBdioDocument.getProject(), simpleBdioDocument.getComponents());
            ExternalId externalId = simpleBdioDocument.getProject().bdioExternalIdentifier.externalIdMetaData;
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
        List<String> moduleNames = Arrays.asList(group, artifact, version, "testcreator");
        String[] moduleNamesArray = new String[moduleNames.size()];
        moduleNames.toArray(moduleNamesArray);
        extId.setModuleNames(moduleNamesArray);
        return extId;
    }

    @NotNull
    private Dependency genComponentDependency(String group, String artifact, String version) {
        ExternalId extId = genComponentExternalId(group, artifact, version);
        return new Dependency(artifact, version, extId);
    }

    @NotNull
    private ExternalId genComponentExternalId(String group, String artifact, String version) {
        ExternalId extId = new ExternalId(Forge.MAVEN);
        extId.setGroup(group);
        extId.setName(artifact);
        extId.setVersion(version);
        return extId;
    }
}
