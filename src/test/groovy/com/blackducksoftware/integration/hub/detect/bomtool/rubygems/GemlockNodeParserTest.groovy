<<<<<<< HEAD
package com.blackducksoftware.integration.hub.detect.bomtool.rubygems

import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class GemlockNodeParserTest {
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    @Test
    public void testParsingSmallGemfileLock() {
        String gemfileLockContents = getClass().getResourceAsStream('/rubygems/small_gemfile_lock').getText(StandardCharsets.UTF_8.toString())
        NameVersionNodeTransformer nameVersionNodeTransformer = new NameVersionNodeTransformer()
        nameVersionNodeTransformer.externalIdFactory = new ExternalIdFactory()
        GemlockNodeParser gemlockNodeParser = new GemlockNodeParser()
        List<DependencyNode> dependencyNodes = gemlockNodeParser.parseProjectDependencies(nameVersionNodeTransformer, gemfileLockContents)

        final String actual = gson.toJson(dependencyNodes);
        final String expected = IOUtils.toString(getClass().getResourceAsStream("/rubygems/expectedSmallParser.json"), StandardCharsets.UTF_8);
        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    public void testParsingGemfileLock() {
        String gemfileLockContents = getClass().getResourceAsStream('/rubygems/Gemfile.lock').getText(StandardCharsets.UTF_8.name())
        NameVersionNodeTransformer nameVersionNodeTransformer = new NameVersionNodeTransformer()
        nameVersionNodeTransformer.externalIdFactory = new ExternalIdFactory()
        GemlockNodeParser gemlockNodeParser = new GemlockNodeParser()
        List<DependencyNode> dependencyNodes = gemlockNodeParser.parseProjectDependencies(nameVersionNodeTransformer, gemfileLockContents)

        final String actual = gson.toJson(dependencyNodes);
        final String expected = IOUtils.toString(getClass().getResourceAsStream("/rubygems/expectedParser.json"), StandardCharsets.UTF_8);
        JSONAssert.assertEquals(expected, actual, false);
    }
}
=======
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
>>>>>>> master
