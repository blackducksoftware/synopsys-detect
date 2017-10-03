package com.blackducksoftware.integration.hub.detect.bomtool.nuget

import static org.junit.Assert.*

import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert

import com.blackducksoftware.integration.hub.bdio.BdioNodeFactory
import com.blackducksoftware.integration.hub.bdio.BdioPropertyHelper
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraphTransformer
import com.blackducksoftware.integration.hub.bdio.model.BdioComponent
import com.blackducksoftware.integration.hub.bdio.model.BdioProject
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.google.gson.Gson
import com.google.gson.GsonBuilder

public class NugetInspectorPackagerTest {
    public Gson gson = new GsonBuilder().setPrettyPrinting().create()

    @Test
    public void createCodeLocationLDServiceDashboard() throws IOException {
        def dependencyNodeFile = new File(getClass().getResource("/nuget/LDService.Dashboard_inspection.json").getFile())
        def expectedOutputFile = new File(getClass().getResource("/nuget/LDService.Dashboard_Output.json").getFile())
        createCodeLocation(dependencyNodeFile, expectedOutputFile)
    }

    @Test
    public void createCodeLocationLDService() throws IOException {
        def dependencyNodeFile = new File(getClass().getResource("/nuget/LDService_inspection.json").getFile())
        def expectedOutputFile = new File(getClass().getResource("/nuget/LDService_Output.json").getFile())
        createCodeLocation(dependencyNodeFile, expectedOutputFile)
    }

    @Test(timeout=5000l)
    public void createCodeLocationDWService() throws IOException {
        def dependencyNodeFile = new File(getClass().getResource("/nuget/dwCheckApi_inspection_martin.json").getFile())
        def expectedOutputFile = new File(getClass().getResource("/nuget/LDService_Output.json").getFile())
        //createCodeLocation(dependencyNodeFile, expectedOutputFile)
        NameVersionNodeTransformer nameVersionNodeTransformer = new NameVersionNodeTransformer()
        nameVersionNodeTransformer.externalIdFactory = new ExternalIdFactory()
        def packager = new NugetInspectorPackager()
        packager.gson = new Gson()
        packager.nameVersionNodeTransformer = nameVersionNodeTransformer
        packager.externalIdFactory = nameVersionNodeTransformer.externalIdFactory
        List<DetectCodeLocation> codeLocations = packager.createDetectCodeLocation(dependencyNodeFile)

        for (DetectCodeLocation codeLocation : codeLocations){
            BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper()
            BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper)

            DependencyGraphTransformer dependencyNodeTransformer = new DependencyGraphTransformer(bdioNodeFactory, bdioPropertyHelper)

            final BdioProject project = bdioNodeFactory.createProject(codeLocation.bomToolProjectName, codeLocation.bomToolProjectVersionName, codeLocation.bomToolProjectExternalId.createDataId(), Forge.NUGET.toString(), codeLocation.bomToolProjectExternalId.createDataId())

            final List<BdioComponent> bdioComponents = dependencyNodeTransformer.addComponentsGraph(project, codeLocation.dependencies)

            assertEquals(bdioComponents.size(), bdioComponents.size())
        }
    }

    private void createCodeLocation(File dependencyNodeFile, File expectedOutputFile) throws IOException {
        NameVersionNodeTransformer nameVersionNodeTransformer = new NameVersionNodeTransformer()
        nameVersionNodeTransformer.externalIdFactory = new ExternalIdFactory()
        def packager = new NugetInspectorPackager()

        packager.gson = new Gson()
        packager.nameVersionNodeTransformer = nameVersionNodeTransformer
        packager.externalIdFactory = nameVersionNodeTransformer.externalIdFactory

        List<DetectCodeLocation> codeLocation = packager.createDetectCodeLocation(dependencyNodeFile)
        String actual = gson.toJson(codeLocation)
        String expected = expectedOutputFile.text

        JSONAssert.assertEquals(expected, actual, false)
    }
}
