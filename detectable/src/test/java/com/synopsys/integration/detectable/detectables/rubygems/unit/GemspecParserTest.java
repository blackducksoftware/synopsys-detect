package com.synopsys.integration.detectable.detectables.rubygems.unit;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.rubygems.parse.GemspecLineParser;
import com.synopsys.integration.detectable.detectables.rubygems.parse.GemspecParser;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

class GemspecParserTest {
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
    private final GemspecLineParser gemspecLineParser = new GemspecLineParser();
    private final GemspecParser gemspecParser = new GemspecParser(externalIdFactory, gemspecLineParser);

    private final ExternalId externalId1 = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "fakegem1", "~> 0.7.1");
    private final ExternalId externalId2 = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "fakegem2", "1.0.0");
    private final ExternalId externalId3 = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "fakegem3", ">= 2.0.0, <3.0.0");

    @Test
    void parseWithJustNormalDependencies() throws IOException {
        final InputStream gemspecInputStream = createGemspecInputStream();
        final DependencyGraph dependencyGraph = gemspecParser.parse(gemspecInputStream, false, false);

        final GraphAssert graphAssert = new GraphAssert(Forge.RUBYGEMS, dependencyGraph);
        graphAssert.hasRootDependency(externalId1);
        graphAssert.hasNoDependency(externalId2);
        graphAssert.hasNoDependency(externalId3);
    }

    @Test
    void parseWithRuntimeDependencies() throws IOException {
        final GemspecParser gemspecParser = new GemspecParser(externalIdFactory, gemspecLineParser);
        final InputStream gemspecInputStream = createGemspecInputStream();
        final DependencyGraph dependencyGraph = gemspecParser.parse(gemspecInputStream, true, false);

        final GraphAssert graphAssert = new GraphAssert(Forge.RUBYGEMS, dependencyGraph);
        graphAssert.hasRootDependency(externalId1);
        graphAssert.hasRootDependency(externalId2);
        graphAssert.hasNoDependency(externalId3);
    }

    @Test
    void parseWithDevelopmentDependencies() throws IOException {
        final GemspecParser gemspecParser = new GemspecParser(externalIdFactory, gemspecLineParser);
        final InputStream gemspecInputStream = createGemspecInputStream();
        final DependencyGraph dependencyGraph = gemspecParser.parse(gemspecInputStream, false, true);

        final GraphAssert graphAssert = new GraphAssert(Forge.RUBYGEMS, dependencyGraph);
        graphAssert.hasRootDependency(externalId1);
        graphAssert.hasNoDependency(externalId2);
        graphAssert.hasRootDependency(externalId3);
    }

    @Test
    void parseWithAllDependencies() throws IOException {
        final GemspecParser gemspecParser = new GemspecParser(externalIdFactory, gemspecLineParser);
        final InputStream gemspecInputStream = createGemspecInputStream();
        final DependencyGraph dependencyGraph = gemspecParser.parse(gemspecInputStream, true, true);

        final GraphAssert graphAssert = new GraphAssert(Forge.RUBYGEMS, dependencyGraph);
        graphAssert.hasRootDependency(externalId1);
        graphAssert.hasRootDependency(externalId2);
        graphAssert.hasRootDependency(externalId3);
    }

    private InputStream createGemspecInputStream() {
        final String gemspec = "Some garbage line" + System.lineSeparator()
                                   + "s.add_dependency \"" + externalId1.name + "\", \"" + externalId1.version + "\"" + System.lineSeparator()
                                   + "s.add_runtime_dependency \"" + externalId2.name + "\", \"" + externalId2.version + "\"" + System.lineSeparator()
                                   + "s.add_development_dependency \"" + externalId3.name + "\", \"" + externalId3.version + "\"" + System.lineSeparator();

        return IOUtils.toInputStream(gemspec, StandardCharsets.UTF_8);
    }
}