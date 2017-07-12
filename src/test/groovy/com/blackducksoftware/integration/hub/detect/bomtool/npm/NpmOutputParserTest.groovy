package com.blackducksoftware.integration.hub.detect.bomtool.npm

import org.junit.Test
import org.junit.runner.RunWith
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.Application
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.util.ProjectInfoGatherer
import com.google.gson.Gson
import com.google.gson.GsonBuilder

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest
public class NpmOutputParserTest {
    public Gson gson = new GsonBuilder().setPrettyPrinting().create()

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

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
