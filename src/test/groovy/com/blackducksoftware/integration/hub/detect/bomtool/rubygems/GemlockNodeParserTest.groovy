package com.blackducksoftware.integration.hub.detect.bomtool.rubygems

import java.nio.charset.StandardCharsets

import org.junit.Test
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphTestUtil
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class GemlockNodeParserTest {
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    @Test
    public void testParsingSmallGemfileLock() {
        String gemfileLockContents = getClass().getResourceAsStream('/rubygems/small_gemfile_lock').getText(StandardCharsets.UTF_8.toString())
        NameVersionNodeTransformer nameVersionNodeTransformer = new NameVersionNodeTransformer()
        GemlockNodeParser gemlockNodeParser = new GemlockNodeParser()
        DependencyGraph dependencyGraph = gemlockNodeParser.parseProjectDependencies(nameVersionNodeTransformer, gemfileLockContents)

        DependencyGraphTestUtil.assertGraph('/rubygems/expectedSmallParser_graph.json', dependencyGraph);
    }

    @Test
    public void testParsingGemfileLock() {
        String gemfileLockContents = getClass().getResourceAsStream('/rubygems/Gemfile.lock').getText(StandardCharsets.UTF_8.name())
        NameVersionNodeTransformer nameVersionNodeTransformer = new NameVersionNodeTransformer()
        GemlockNodeParser gemlockNodeParser = new GemlockNodeParser()
        DependencyGraph dependencyGraph = gemlockNodeParser.parseProjectDependencies(nameVersionNodeTransformer, gemfileLockContents)


        DependencyGraphTestUtil.assertGraph('/rubygems/expectedParser_graph.json', dependencyGraph);
    }
}
