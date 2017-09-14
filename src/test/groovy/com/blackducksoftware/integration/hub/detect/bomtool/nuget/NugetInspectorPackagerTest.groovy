package com.blackducksoftware.integration.hub.detect.bomtool.nuget

import org.junit.Assert
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert

import com.blackducksoftware.integration.hub.bdio.simple.BdioNodeFactory
import com.blackducksoftware.integration.hub.bdio.simple.BdioPropertyHelper
import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeTransformer
import com.blackducksoftware.integration.hub.bdio.simple.model.BdioComponent
import com.blackducksoftware.integration.hub.bdio.simple.model.BdioProject
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
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
        def packager = new NugetInspectorPackager()
        packager.gson = new Gson()
        packager.nameVersionNodeTransformer = new NameVersionNodeTransformer()
        List<DetectCodeLocation> codeLocations = packager.createDetectCodeLocation(dependencyNodeFile)

        for (DetectCodeLocation codeLocation : codeLocations){
            BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
            BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);

            DependencyNodeTransformer dependencyNodeTransformer = new DependencyNodeTransformer(bdioNodeFactory, bdioPropertyHelper);

            final BdioProject project = bdioNodeFactory.createProject(codeLocation.bomToolProjectName, codeLocation.bomToolProjectVersionName, codeLocation.bomToolProjectExternalId.createDataId(), Forge.NUGET.toString(), codeLocation.bomToolProjectExternalId.createDataId())

            final List<BdioComponent> bdioComponents = dependencyNodeTransformer.addComponentsGraph(project, codeLocation.dependencies)

            Assert.assertEquals(bdioComponents.size(), bdioComponents.size())
        }
    }

    private void createCodeLocation(File dependencyNodeFile, File expectedOutputFile) throws IOException {
        def packager = new NugetInspectorPackager()

        packager.gson = new Gson()
        packager.nameVersionNodeTransformer = new NameVersionNodeTransformer()

        List<DetectCodeLocation> codeLocation = packager.createDetectCodeLocation(dependencyNodeFile)
        String actual = gson.toJson(codeLocation)
        String expected = expectedOutputFile.text

        JSONAssert.assertEquals(expected, actual, false)
    }
}
