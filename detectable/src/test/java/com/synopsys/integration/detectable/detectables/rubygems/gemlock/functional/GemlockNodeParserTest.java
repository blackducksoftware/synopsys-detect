package com.synopsys.integration.detectable.detectables.rubygems.gemlock.functional;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.rubygems.gemlock.parse.GemlockParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;

@FunctionalTest
public class GemlockNodeParserTest {
    @Test
    public void testParsingSmallGemfileLock() {
        final String text = FunctionalTestFiles.asString("/rubygems/small_gemfile_lock");
        final List<String> gemfileLockContents = Arrays.asList(text.split("\n"));
        final GemlockParser gemlockNodeParser = new GemlockParser(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = gemlockNodeParser.parseProjectDependencies(gemfileLockContents);

        GraphCompare.assertEqualsResource("/rubygems/expectedSmallParser_graph.json", dependencyGraph);
    }

    @Test
    public void testParsingGemfileLock() {
        final String text = FunctionalTestFiles.asString("/rubygems/Gemfile.lock");
        final List<String> gemfileLockContents = Arrays.asList(text.split("\n"));
        final GemlockParser gemlockNodeParser = new GemlockParser(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = gemlockNodeParser.parseProjectDependencies(gemfileLockContents);

        GraphCompare.assertEqualsResource("/rubygems/expectedParser_graph.json", dependencyGraph);
    }

    @Test
    public void testParsingEqualsGemfileLock() {
        final String text = FunctionalTestFiles.asString("/rubygems/Gemfile_equals_version.lock");
        final List<String> gemfileLockContents = Arrays.asList(text.split("\n"));
        final GemlockParser gemlockNodeParser = new GemlockParser(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = gemlockNodeParser.parseProjectDependencies(gemfileLockContents);

        final Dependency bundler = dependencyGraph.getDependency(new ExternalIdFactory().createNameVersionExternalId(Forge.RUBYGEMS, "bundler", "1.11.2"));
        assertNotNull(bundler);
    }

    @Test
    public void testMissingVersionsGemfileLock() {
        final String text = FunctionalTestFiles.asString("/rubygems/Gemfile_missing_versions.lock");
        final List<String> gemfileLockContents = Arrays.asList(text.split("\n"));
        final GemlockParser gemlockNodeParser = new GemlockParser(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = gemlockNodeParser.parseProjectDependencies(gemfileLockContents);

        final Dependency newrelic_rpm = dependencyGraph.getDependency(new ExternalIdFactory().createNameVersionExternalId(Forge.RUBYGEMS, "newrelic_rpm", ""));
        assertNotNull(newrelic_rpm);
    }
}
