package com.blackducksoftware.integration.hub.detect.detector.nuget;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.hub.bdio.BdioNodeFactory;
import com.synopsys.integration.hub.bdio.BdioPropertyHelper;
import com.synopsys.integration.hub.bdio.graph.DependencyGraphTransformer;
import com.synopsys.integration.hub.bdio.model.BdioComponent;
import com.synopsys.integration.hub.bdio.model.BdioExternalIdentifier;
import com.synopsys.integration.hub.bdio.model.BdioNode;
import com.synopsys.integration.hub.bdio.model.BdioProject;
import com.synopsys.integration.hub.bdio.model.Forge;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalId;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class NugetInspectorPackagerTest {
    public Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void createCodeLocationLDServiceDashboard() throws IOException {
        final File dependencyNodeFile = new File(getClass().getResource("/nuget/LDService.Dashboard_inspection.json").getFile());
        final ArrayList<String> expectedOutputFiles = new ArrayList<>();
        expectedOutputFiles.add("/nuget/LDService.Dashboard_Output_0_graph.json");
        createCodeLocation(dependencyNodeFile, expectedOutputFiles);
    }

    @Test
    public void createCodeLocationLDService() throws IOException {
        final File dependencyNodeFile = new File(getClass().getResource("/nuget/LDService_inspection.json").getFile());
        final ArrayList<String> expectedOutputFiles = new ArrayList<>();
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

    @Test(timeout = 5000L)
    public void createCodeLocationDWService() throws IOException {
        final File dependencyNodeFile = new File(getClass().getResource("/nuget/dwCheckApi_inspection_martin.json").getFile());
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

        final NugetInspectorPackager packager = new NugetInspectorPackager(gson, externalIdFactory);
        final NugetParseResult result = packager.createDetectCodeLocation(dependencyNodeFile);

        for (final DetectCodeLocation codeLocation : result.codeLocations) {
            final BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
            final BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);

            final DependencyGraphTransformer dependencyNodeTransformer = new DependencyGraphTransformer(bdioPropertyHelper, bdioNodeFactory);

            final BdioExternalIdentifier projectId = bdioPropertyHelper.createExternalIdentifier(codeLocation.getExternalId());
            final BdioProject project = bdioNodeFactory.createProject(result.projectName, result.projectVersion, Forge.NUGET.toString(), projectId);

            final Map<ExternalId, BdioNode> components = new HashMap<>();
            components.put(codeLocation.getExternalId(), project);

            final List<BdioComponent> bdioComponents = dependencyNodeTransformer.transformDependencyGraph(codeLocation.getDependencyGraph(), project, codeLocation.getDependencyGraph().getRootDependencies(), components);

            assertEquals(bdioComponents.size(), bdioComponents.size());
        }
    }

    private void createCodeLocation(final File dependencyNodeFile, final List<String> expectedOutputFiles) throws IOException {
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final NugetInspectorPackager packager = new NugetInspectorPackager(gson, externalIdFactory);

        final NugetParseResult result = packager.createDetectCodeLocation(dependencyNodeFile);

        for (int i = 0; i < expectedOutputFiles.size(); i++) {
            final DetectCodeLocation codeLocation = result.codeLocations.get(i);
            final String expectedOutputFile = expectedOutputFiles.get(i);

            DependencyGraphResourceTestUtil.assertGraph(expectedOutputFile, codeLocation.getDependencyGraph());
        }
    }
}
