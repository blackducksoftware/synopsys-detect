package com.blackducksoftware.integration.hub.detect.bomtool.nuget

import com.blackducksoftware.integration.hub.bdio.BdioNodeFactory
import com.blackducksoftware.integration.hub.bdio.BdioPropertyHelper
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraphTransformer
import com.blackducksoftware.integration.hub.bdio.model.BdioComponent
import com.blackducksoftware.integration.hub.bdio.model.BdioProject
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.nuget.parse.NugetInspectorPackager
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Test

import static org.junit.Assert.assertEquals

public class NugetInspectorPackagerTest {
    public Gson gson = new GsonBuilder().setPrettyPrinting().create()

    @Test
    public void createCodeLocationLDServiceDashboard() throws IOException {
        def dependencyNodeFile = new File(getClass().getResource("/nuget/LDService.Dashboard_inspection.json").getFile())
        def expectedOutputFiles = new ArrayList<String>();
        expectedOutputFiles.add("/nuget/LDService.Dashboard_Output_0_graph.json");
        createCodeLocation(dependencyNodeFile, expectedOutputFiles)
    }

    @Test
    public void createCodeLocationLDService() throws IOException {
        def dependencyNodeFile = new File(getClass().getResource("/nuget/LDService_inspection.json").getFile())
        def expectedOutputFiles = new ArrayList<String>();
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
        createCodeLocation(dependencyNodeFile, expectedOutputFiles)
    }

    @Test(timeout=5000L)
    public void createCodeLocationDWService() throws IOException {
        def dependencyNodeFile = new File(getClass().getResource("/nuget/dwCheckApi_inspection_martin.json").getFile())
        NameVersionNodeTransformer nameVersionNodeTransformer = new NameVersionNodeTransformer()
        nameVersionNodeTransformer.externalIdFactory = new ExternalIdFactory()
        def packager = new NugetInspectorPackager()
        packager.gson = new Gson()
        packager.nameVersionNodeTransformer = nameVersionNodeTransformer
        packager.externalIdFactory = nameVersionNodeTransformer.externalIdFactory
        List<DetectCodeLocation> codeLocations = packager.createDetectCodeLocation(dependencyNodeFile)

        for (DetectCodeLocation codeLocation : codeLocations) {
            BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper()
            BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper)

            DependencyGraphTransformer dependencyNodeTransformer = new DependencyGraphTransformer(bdioPropertyHelper, bdioNodeFactory)

            def projectId = bdioPropertyHelper.createExternalIdentifier(codeLocation.bomToolProjectExternalId);
            final BdioProject project = bdioNodeFactory.createProject(codeLocation.bomToolProjectName, codeLocation.bomToolProjectVersionName, Forge.NUGET.toString(), projectId)

            Map<ExternalId, BdioComponent> components = new HashMap<>();
            components.put(codeLocation.bomToolProjectExternalId, project);

            final List<BdioComponent> bdioComponents = dependencyNodeTransformer.transformDependencyGraph(codeLocation.dependencyGraph, project, codeLocation.dependencyGraph.getRootDependencies(), components)

            assertEquals(bdioComponents.size(), bdioComponents.size())
        }
    }

    private void createCodeLocation(File dependencyNodeFile, List<String> expectedOutputFiles) throws IOException {
        NameVersionNodeTransformer nameVersionNodeTransformer = new NameVersionNodeTransformer()
        nameVersionNodeTransformer.externalIdFactory = new ExternalIdFactory()
        def packager = new NugetInspectorPackager()

        packager.gson = new Gson()
        packager.nameVersionNodeTransformer = nameVersionNodeTransformer
        packager.externalIdFactory = nameVersionNodeTransformer.externalIdFactory

        List<DetectCodeLocation> codeLocations = packager.createDetectCodeLocation(dependencyNodeFile)

        for (def i = 0; i < expectedOutputFiles.size(); i++) {
            def codeLocation = codeLocations[i];
            def expectedOutputFile = expectedOutputFiles[i];


            DependencyGraphResourceTestUtil.assertGraph(expectedOutputFile, codeLocation.dependencyGraph);
        }
    }
}
