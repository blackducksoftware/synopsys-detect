package com.blackducksoftware.integration.hub.detect.bomtool.nuget

import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.google.gson.Gson
import com.google.gson.GsonBuilder

public class NugetInspectorPackagerTest {
    public Gson gson = new GsonBuilder().setPrettyPrinting().create()

    @Test
    public void createCodeLocationConsoleApp() throws IOException {
        def dependencyNodeFile = new File(getClass().getResource("/nuget/ConsoleApp1_dependency_node.json").getFile())
        def expectedOutputFile = new File(getClass().getResource("/nuget/ConsoleApp1_Output.json").getFile())
        createCodeLocation(dependencyNodeFile, expectedOutputFile)
    }

    private void createCodeLocation(File dependencyNodeFile, File expectedOutputFile) throws IOException {
        def packager = new NugetInspectorPackager()

        packager.gson = new Gson()
        packager.nameVersionNodeTransformer = new NameVersionNodeTransformer()

        DependencyNode node = packager.createDetectCodeLocation(dependencyNodeFile)
        String actual = gson.toJson(node)
        String expected = expectedOutputFile.text

        JSONAssert.assertEquals(expected, actual, false)
    }
}
