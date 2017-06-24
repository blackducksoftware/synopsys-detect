package com.blackducksoftware.integration.hub.detect.bomtool.npm

import static org.junit.Assert.assertTrue

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.Application
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.util.ProjectInfoGatherer
import com.google.gson.Gson

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest
public class NpmOutputParserTest {

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    @Test
    public void npmCliDependencyFinder() throws IOException {
        def parser = new NpmCliDependencyFinder()
        def testIn = new File(getClass().getResource("/npm/packman_proj_dependencies.json").getFile())

        parser.setGson(new Gson())
        parser.setNodeTransformer(new NameVersionNodeTransformer())
        parser.setProjectInfoGatherer(projectInfoGatherer)

        DependencyNode node = parser.convertNpmJsonFileToDependencyNode(testIn, "")
        def testOut = new File(getClass().getResource("/npm/npmParseOutput.txt").getFile())

        assertTrue(node.toString().contentEquals(testOut.text))
    }
}
