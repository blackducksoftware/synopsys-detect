package com.synopsys.integration.detect.workflow.bdio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.BdioReader;
import com.synopsys.integration.bdio.BdioTransformer;
import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;

public class AggregateBdioTransformerTest {

    @Test
    void test() throws DetectUserFriendlyException, IOException {
        List<String> inputBdioFilenames = Arrays.asList(
            "basic_multiproject_0_0_0_SNAPSHOT_com_synopsys_integration_basic_multiproject_0_0_0_SNAPSHOT_gradle_bom.jsonld",
            "basic_multiproject_0_0_0_SNAPSHOT_subprojectone_basic_multiproject_subprojectone_unspecified_gradle_bom.jsonld",
            "basic_multiproject_0_0_0_SNAPSHOT_subprojecttwo_basic_multiproject_subprojecttwo_unspecified_gradle_bom.jsonld"
        );
        Gson gson = new Gson();
        AggregateBdioTransformer transformer = new AggregateBdioTransformer(new SimpleBdioFactory());

        File sourceDir = new File("src/test/resources/workflow/bdio/aggregation/src");
        List<DetectCodeLocation> codelocations = new LinkedList<>();

        BdioTransformer bdioTransformer = new BdioTransformer();

        File inputBdioDir = new File("src/test/resources/workflow/bdio/aggregation/input");
        for (String inputBdioFilename : inputBdioFilenames) {

            File bdioFile = new File(inputBdioDir, inputBdioFilename);
            SimpleBdioDocument simpleBdioDocument = null;
            try (InputStream bdioInputStream = new FileInputStream(bdioFile); BdioReader bdioReader = new BdioReader(gson, bdioInputStream)) {
                simpleBdioDocument = bdioReader.readSimpleBdioDocument();
            }
            DependencyGraph dependencyGraph = bdioTransformer.transformToDependencyGraph(simpleBdioDocument.getProject(), simpleBdioDocument.getComponents());
            ExternalId externalId = simpleBdioDocument.getProject().bdioExternalIdentifier.externalIdMetaData;
            DetectCodeLocation detectCodeLocation = DetectCodeLocation.forCreator(dependencyGraph, sourceDir, externalId, "testCreator");
            codelocations.add(detectCodeLocation);
        }

        DependencyGraph aggregatedGraph = transformer.aggregateCodeLocations(sourceDir, codelocations, AggregateMode.TRANSITIVE);
        
        System.out.printf("aggregaged graph # root dependencies: %d\n", aggregatedGraph.getRootDependencies().size());
    }
}
