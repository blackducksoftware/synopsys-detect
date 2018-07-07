
package com.blackducksoftware.integration.hub.detect.bomtool.rubygems

import java.nio.charset.StandardCharsets

import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class GemlockNodeParserTest {
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    @Test
    public void testParsingSmallGemfileLock() {
        List<String> gemfileLockContents = getClass().getResourceAsStream('/rubygems/small_gemfile_lock').getText(StandardCharsets.UTF_8.toString()).split('\n').toList()
        GemlockParser gemlockNodeParser = new GemlockParser(new ExternalIdFactory())
        DependencyGraph dependencyGraph = gemlockNodeParser.parseProjectDependencies(gemfileLockContents)

        DependencyGraphResourceTestUtil.assertGraph('/rubygems/expectedSmallParser_graph.json', dependencyGraph)
    }

    @Test
    public void testParsingGemfileLock() {
        List<String> gemfileLockContents = getClass().getResourceAsStream('/rubygems/Gemfile.lock').getText(StandardCharsets.UTF_8.name()).split('\n').toList()
        GemlockParser gemlockNodeParser = new GemlockParser(new ExternalIdFactory())
        DependencyGraph dependencyGraph = gemlockNodeParser.parseProjectDependencies(gemfileLockContents)


        DependencyGraphResourceTestUtil.assertGraph('/rubygems/expectedParser_graph.json', dependencyGraph)
    }
}
