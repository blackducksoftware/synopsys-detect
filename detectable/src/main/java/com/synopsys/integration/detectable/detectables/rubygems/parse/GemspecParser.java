package com.synopsys.integration.detectable.detectables.rubygems.parse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;

public class GemspecParser {
    private final IntLogger logger = new Slf4jIntLogger(LoggerFactory.getLogger(getClass()));

    private final ExternalIdFactory externalIdFactory;
    private final GemspecLineParser gemspecLineParser;

    public GemspecParser(final ExternalIdFactory externalIdFactory, final GemspecLineParser gemspecLineParser) {
        this.externalIdFactory = externalIdFactory;
        this.gemspecLineParser = gemspecLineParser;
    }

    public DependencyGraph parse(final InputStream inputStream, final boolean includeRuntimeDependencies, final boolean includeDevelopmentDependencies) throws IOException {
        final MutableMapDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (!gemspecLineParser.shouldParseLine(line)) {
                continue;
            }

            final Optional<GemspecDependency> gemspecDependencyOptional = gemspecLineParser.parseLine(line);
            if (!gemspecDependencyOptional.isPresent()) {
                continue;
            }

            final GemspecDependency gemspecDependency = gemspecDependencyOptional.get();

            if (!includeRuntimeDependencies && gemspecDependency.getGemspecDependencyType() == GemspecDependencyType.RUNTIME) {
                logger.info(String.format("Excluding component '%s' from graph because it is a runtime dependency", gemspecDependency.getName()));
                continue;
            } else if (!includeDevelopmentDependencies && gemspecDependency.getGemspecDependencyType() == GemspecDependencyType.DEVELOPMENT) {
                logger.info(String.format("Excluding component '%s' from graph because it is a development dependency", gemspecDependency.getName()));
                continue;
            }

            final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, gemspecDependency.getName(), gemspecDependency.getVersion().orElse("No version"));
            final Dependency dependency = new Dependency(externalId);

            dependencyGraph.addChildrenToRoot(dependency);
        }

        return dependencyGraph;
    }
}
