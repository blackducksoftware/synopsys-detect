
package com.synopsys.integration.detect.detector.rubygems;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.synopsys.integration.detect.testutils.DependencyGraphResourceTestUtil;
import com.synopsys.integration.detect.testutils.TestUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class GemlockNodeParserTest {
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    TestUtil testUtils = new TestUtil();

    @Test
    public void testParsingSmallGemfileLock() {
        final String text = testUtils.getResourceAsUTF8String("/rubygems/small_gemfile_lock");
        final List<String> gemfileLockContents = Arrays.asList(text.split("\n"));
        final GemlockParser gemlockNodeParser = new GemlockParser(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = gemlockNodeParser.parseProjectDependencies(gemfileLockContents);

        DependencyGraphResourceTestUtil.assertGraph("/rubygems/expectedSmallParser_graph.json", dependencyGraph);
    }

    @Test
    public void testParsingGemfileLock() {
        final String text = testUtils.getResourceAsUTF8String("/rubygems/Gemfile.lock");
        final List<String> gemfileLockContents = Arrays.asList(text.split("\n"));
        final GemlockParser gemlockNodeParser = new GemlockParser(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = gemlockNodeParser.parseProjectDependencies(gemfileLockContents);

        DependencyGraphResourceTestUtil.assertGraph("/rubygems/expectedParser_graph.json", dependencyGraph);
    }

    @Test
    public void testParsingEqualsGemfileLock() {
        final String text = testUtils.getResourceAsUTF8String("/rubygems/Gemfile_equals_version.lock");
        final List<String> gemfileLockContents = Arrays.asList(text.split("\n"));
        final GemlockParser gemlockNodeParser = new GemlockParser(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = gemlockNodeParser.parseProjectDependencies(gemfileLockContents);

        Dependency bundler = dependencyGraph.getDependency(new ExternalIdFactory().createNameVersionExternalId(Forge.RUBYGEMS, "bundler", "1.11.2"));
        assertNotNull(bundler);
    }

    @Test
    public void testMissingVersionsGemfileLock() {
        final String text = testUtils.getResourceAsUTF8String("/rubygems/Gemfile_missing_versions.lock");
        final List<String> gemfileLockContents = Arrays.asList(text.split("\n"));
        final GemlockParser gemlockNodeParser = new GemlockParser(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = gemlockNodeParser.parseProjectDependencies(gemfileLockContents);

        Dependency newrelic_rpm = dependencyGraph.getDependency(new ExternalIdFactory().createNameVersionExternalId(Forge.RUBYGEMS, "newrelic_rpm", ""));
        assertNotNull(newrelic_rpm);
    }
}
