package com.blackducksoftware.integration.hub.detect.bomtool.nuget

import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert

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
