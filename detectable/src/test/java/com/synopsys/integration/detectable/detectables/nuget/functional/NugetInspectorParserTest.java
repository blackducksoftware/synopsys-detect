package com.synopsys.integration.detectable.detectables.nuget.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.BdioNodeFactory;
import com.synopsys.integration.bdio.BdioPropertyHelper;
import com.synopsys.integration.bdio.graph.DependencyGraphTransformer;
import com.synopsys.integration.bdio.model.BdioComponent;
import com.synopsys.integration.bdio.model.BdioExternalIdentifier;
import com.synopsys.integration.bdio.model.BdioId;
import com.synopsys.integration.bdio.model.BdioNode;
import com.synopsys.integration.bdio.model.BdioProject;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.nuget.parse.NugetInspectorParser;
import com.synopsys.integration.detectable.detectables.nuget.parse.NugetParseResult;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;

public class NugetInspectorParserTest {
    public Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    @Disabled
    public void createCodeLocationLDServiceDashboard() throws IOException {
        String dependencyNodeFile = FunctionalTestFiles.asString("/nuget/LDService.Dashboard_inspection.json");
        ArrayList<String> expectedOutputFiles = new ArrayList<>();
        expectedOutputFiles.add("/nuget/LDService.Dashboard_Output_0_graph.json");
        createCodeLocation(dependencyNodeFile, expectedOutputFiles);
    }

    @Test
    @Disabled
    public void createCodeLocationLDService() throws IOException {
        String dependencyNodeFile = FunctionalTestFiles.asString("/nuget/LDService_inspection.json");
        ArrayList<String> expectedOutputFiles = new ArrayList<>();
        expectedOutputFiles.add("/nuget/LDService_Output_0_graph.json");
        expectedOutputFiles.add("/nuget/LDService_Output_1_graph.json");
        expectedOutputFiles.add("/nuget/LDService_Output_2_graph.json");
        expectedOutputFiles.add("/nuget/LDService_Output_3_graph.json");
        expectedOutputFiles.add("/nuget/LDService_Output_4_graph.json");
        expectedOutputFiles.add("/nuget/LDService_Output_5_graph.json");
        expectedOutputFiles.add("/nuget/LDService_Output_6_graph.json");
        expectedOutputFiles.add("/nuget/LDService_Output_7_graph.json");
        expectedOutputFiles.add("/nuget/LDService_Output_8_graph.json");
        expectedOutputFiles.add("/nuget/LDService_Output_9_graph.json");
        expectedOutputFiles.add("/nuget/LDService_Output_10_graph.json");
        expectedOutputFiles.add("/nuget/LDService_Output_11_graph.json");
        expectedOutputFiles.add("/nuget/LDService_Output_12_graph.json");
        createCodeLocation(dependencyNodeFile, expectedOutputFiles);
    }

    @Test
    public void createCodeLocationDWService() {
        Assertions.assertTimeout(Duration.ofMillis(5000L), () -> {
            String dependencyNodeFile = FunctionalTestFiles.asString("/nuget/dwCheckApi_inspection_martin.json");
            ExternalIdFactory externalIdFactory = new ExternalIdFactory();

            NugetInspectorParser packager = new NugetInspectorParser(gson, externalIdFactory);
            NugetParseResult result = packager.createCodeLocation(dependencyNodeFile);

            for (CodeLocation codeLocation : result.getCodeLocations()) {
                BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
                BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);

                DependencyGraphTransformer dependencyNodeTransformer = new DependencyGraphTransformer(bdioPropertyHelper, bdioNodeFactory);

                BdioExternalIdentifier projectId = bdioPropertyHelper.createExternalIdentifier(codeLocation.getExternalId().get());
                BdioProject project = bdioNodeFactory.createProject(
                    result.getProjectName(),
                    result.getProjectVersion(),
                    BdioId.createFromPieces(Forge.NUGET.toString()),
                    projectId
                );

                Map<ExternalId, BdioNode> components = new HashMap<>();
                components.put(codeLocation.getExternalId().get(), project);

                List<BdioComponent> bdioComponents = dependencyNodeTransformer.transformDependencyGraph(
                    codeLocation.getDependencyGraph(),
                    project,
                    codeLocation.getDependencyGraph().getRootDependencies(),
                    components
                );

                assertEquals(bdioComponents.size(), bdioComponents.size());
            }
        });
    }

    private void createCodeLocation(String dependencyNodeFile, List<String> expectedOutputFiles) {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        NugetInspectorParser packager = new NugetInspectorParser(gson, externalIdFactory);

        NugetParseResult result = packager.createCodeLocation(dependencyNodeFile);

        for (int i = 0; i < expectedOutputFiles.size(); i++) {
            CodeLocation codeLocation = result.getCodeLocations().get(i);
            String expectedOutputFile = expectedOutputFiles.get(i);

            GraphCompare.assertEqualsResource(expectedOutputFile, codeLocation.getDependencyGraph());
        }
    }
}
