package com.blackducksoftware.integration.hub.detect.bomtool.npm

import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.google.gson.Gson
import com.google.gson.GsonBuilder

public class NpmOutputParserTest {
    public Gson gson = new GsonBuilder().setPrettyPrinting().create()

    @Test
    public void npmCliDependencyFinder() throws IOException {
        def parser = new NpmCliDependencyFinder()
        def testIn = new File(getClass().getResource("/npm/packman_proj_dependencies.json").getFile())

        parser.setGson(new Gson())
        parser.setNodeTransformer(new NameVersionNodeTransformer())

        DependencyNode node = parser.convertNpmJsonFileToDependencyNode(testIn)
        String actual = gson.toJson(node)
        String expected = new File(getClass().getResource("/npm/npmParseOutput.txt").getFile()).text

        JSONAssert.assertEquals(expected, actual, false)
    }
}
