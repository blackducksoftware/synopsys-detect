package com.synopsys.integration.detectable.detectables.rubygems.gemspec.unit;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.rubygems.GemspecDependencyType;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.parse.GemspecLineParser;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.parse.GemspecParser;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

class GemspecParserTest {
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    private final ExternalId externalId1 = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "fakegem1", "~> 0.7.1");
    private final ExternalId externalId2 = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "fakegem2", "1.0.0");
    private final ExternalId externalId3 = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "fakegem3", ">= 2.0.0, <3.0.0");

    private GemspecParser gemspecParser(GemspecDependencyType... excludedTypes) {
        GemspecLineParser gemspecLineParser = new GemspecLineParser();
        return new GemspecParser(gemspecLineParser, EnumListFilter.fromExcluded(excludedTypes));
    }

    @Test
    void parseWithJustNormalDependencies() throws IOException {
        InputStream gemspecInputStream = createGemspecInputStream();
        DependencyGraph dependencyGraph = gemspecParser(GemspecDependencyType.RUNTIME, GemspecDependencyType.DEV).parse(gemspecInputStream);

        GraphAssert graphAssert = new GraphAssert(Forge.RUBYGEMS, dependencyGraph);
        graphAssert.hasRootDependency(externalId1);
        graphAssert.hasNoDependency(externalId2);
        graphAssert.hasNoDependency(externalId3);
    }

    @Test
    void parseWithRuntimeDependencies() throws IOException {
        InputStream gemspecInputStream = createGemspecInputStream();
        DependencyGraph dependencyGraph = gemspecParser(GemspecDependencyType.DEV).parse(gemspecInputStream);

        GraphAssert graphAssert = new GraphAssert(Forge.RUBYGEMS, dependencyGraph);
        graphAssert.hasRootDependency(externalId1);
        graphAssert.hasRootDependency(externalId2);
        graphAssert.hasNoDependency(externalId3);
    }

    @Test
    void parseWithDevelopmentDependencies() throws IOException {
        InputStream gemspecInputStream = createGemspecInputStream();
        DependencyGraph dependencyGraph = gemspecParser(GemspecDependencyType.RUNTIME).parse(gemspecInputStream);

        GraphAssert graphAssert = new GraphAssert(Forge.RUBYGEMS, dependencyGraph);
        graphAssert.hasRootDependency(externalId1);
        graphAssert.hasNoDependency(externalId2);
        graphAssert.hasRootDependency(externalId3);
    }

    @Test
    void parseWithAllDependencies() throws IOException {
        InputStream gemspecInputStream = createGemspecInputStream();
        DependencyGraph dependencyGraph = gemspecParser().parse(gemspecInputStream);

        GraphAssert graphAssert = new GraphAssert(Forge.RUBYGEMS, dependencyGraph);
        graphAssert.hasRootDependency(externalId1);
        graphAssert.hasRootDependency(externalId2);
        graphAssert.hasRootDependency(externalId3);
    }

    private InputStream createGemspecInputStream() {
        String gemspec = "Some garbage line" + System.lineSeparator()
            + "s.add_dependency \"" + externalId1.getName() + "\", \"" + externalId1.getVersion() + "\"" + System.lineSeparator()
            + "s.add_runtime_dependency \"" + externalId2.getName() + "\", \"" + externalId2.getVersion() + "\"" + System.lineSeparator()
            + "s.add_development_dependency \"" + externalId3.getName() + "\", \"" + externalId3.getVersion() + "\"" + System.lineSeparator();

        return IOUtils.toInputStream(gemspec, StandardCharsets.UTF_8);
    }
}