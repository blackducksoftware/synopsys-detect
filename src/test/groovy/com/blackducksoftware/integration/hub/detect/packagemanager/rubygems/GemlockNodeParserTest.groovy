package com.blackducksoftware.integration.hub.detect.packagemanager.rubygems

import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.rubygems.GemlockNodeParser
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class GemlockNodeParserTest {
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    @Test
    public void testParsingSmallGemfileLock() {
        String gemfileLockContents = getClass().getResourceAsStream('/rubygems/small_gemfile_lock').getText(StandardCharsets.UTF_8.name())
        GemlockNodeParser gemlockNodeParser = new GemlockNodeParser()
        DependencyNode root = new DependencyNode('testName', 'testVersion', new NameVersionExternalId(Forge.RUBYGEMS, 'testName', 'testVersion'))
        gemlockNodeParser.parseProjectDependencies(root, gemfileLockContents)

        final String actual = gson.toJson(root);
        final String expected = IOUtils.toString(getClass().getResourceAsStream("/rubygems/expectedSmallParser.json"), StandardCharsets.UTF_8);
        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    public void testParsingGemfileLock() {
        String gemfileLockContents = getClass().getResourceAsStream('/rubygems/Gemfile.lock').getText(StandardCharsets.UTF_8.name())
        GemlockNodeParser gemlockNodeParser = new GemlockNodeParser()
        DependencyNode root = new DependencyNode('testName', 'testVersion', new NameVersionExternalId(Forge.RUBYGEMS, 'testName', 'testVersion'))
        gemlockNodeParser.parseProjectDependencies(root, gemfileLockContents)

        final String actual = gson.toJson(root);
        final String expected = IOUtils.toString(getClass().getResourceAsStream("/rubygems/expectedParser.json"), StandardCharsets.UTF_8);
        JSONAssert.assertEquals(expected, actual, false);
    }
}
