package com.blackducksoftware.integration.hub.detect.bomtool.npm

import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil

public class NpmOutputParserTest {
    private TestUtil testUtil = new TestUtil()

    @Test
    public void npmCliDependencyFinder() throws IOException {
        def parser = new NpmCliDependencyFinder()
        String testIn = testUtil.getResourceAsUTF8String('/npm/packman_proj_dependencies.json')
        DependencyNode node = parser.convertNpmJsonFileToDependencyNode(testIn)

        testUtil.testJsonResource('/npm/npmParseOutput.json', node)
    }

    @Test
    public void npmBdioCreation() throws IOException {
        def parser = new NpmCliDependencyFinder()
        String testIn = testUtil.getResourceAsUTF8String('/npm/code_jam_ui.json')
        DependencyNode node = parser.convertNpmJsonFileToDependencyNode(testIn)

        testUtil.testJsonResource('/npm/code_jam_ui_output.json', node)
    }
}
