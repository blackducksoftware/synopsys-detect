package com.blackducksoftware.integration.hub.detect.bomtool.nuget

import org.junit.Ignore
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert

import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.google.gson.Gson
import com.google.gson.GsonBuilder

@Ignore
public class NugetInspectorPackagerTest {
    public Gson gson = new GsonBuilder().setPrettyPrinting().create()

    @Test
    public void createCodeLocationConsoleApp() throws IOException {
        //output from < 1.1.0 or the Nuget inspector
        // project
        def dependencyNodeFile = new File(getClass().getResource("/nuget/ConsoleApp1_dependency_node.json").getFile())
        def expectedOutputFile = new File(getClass().getResource("/nuget/ConsoleApp1_Output.json").getFile())
        createCodeLocation(dependencyNodeFile, expectedOutputFile)
    }

    @Test
    public void createCodeLocationIntegrationNugetInspector() throws IOException {
        //output from < 1.1.0 or the Nuget inspector
        //solution
        def dependencyNodeFile = new File(getClass().getResource("/nuget/integration-nuget-inspector_dependency_node.json").getFile())
        def expectedOutputFile = new File(getClass().getResource("/nuget/integration-nuget-inspector_Output.json").getFile())
        createCodeLocation(dependencyNodeFile, expectedOutputFile)
    }

    @Test
    public void createCodeLocationLDServiceDashboard() throws IOException {
        //output from >= 1.1.0 or the Nuget inspector
        // project
        def dependencyNodeFile = new File(getClass().getResource("/nuget/LDService.Dashboard_dependency_node.json").getFile())
        def expectedOutputFile = new File(getClass().getResource("/nuget/LDService.Dashboard_Output.json").getFile())
        createCodeLocation(dependencyNodeFile, expectedOutputFile)
    }

    @Test
    public void createCodeLocationLDService() throws IOException {
        //output from >= 1.1.0 or the Nuget inspector
        //solution
        def dependencyNodeFile = new File(getClass().getResource("/nuget/LDService_dependency_node.json").getFile())
        def expectedOutputFile = new File(getClass().getResource("/nuget/LDService_Output.json").getFile())
        createCodeLocation(dependencyNodeFile, expectedOutputFile)
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
