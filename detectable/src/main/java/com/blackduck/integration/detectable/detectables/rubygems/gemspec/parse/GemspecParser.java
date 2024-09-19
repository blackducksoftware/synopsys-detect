package com.blackduck.integration.detectable.detectables.rubygems.gemspec.parse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

import org.slf4j.LoggerFactory;

import com.blackduck.integration.bdio.graph.BasicDependencyGraph;
import com.blackduck.integration.bdio.graph.DependencyGraph;
import com.blackduck.integration.bdio.model.Forge;
import com.blackduck.integration.bdio.model.dependency.Dependency;
import com.blackduck.integration.detectable.detectable.util.EnumListFilter;
import com.blackduck.integration.log.IntLogger;
import com.blackduck.integration.log.Slf4jIntLogger;

public class GemspecParser {
    private final IntLogger logger = new Slf4jIntLogger(LoggerFactory.getLogger(getClass()));

    private final GemspecLineParser gemspecLineParser;
    private final EnumListFilter<com.blackduck.integration.detectable.detectables.rubygems.GemspecDependencyType> dependencyTypeFilter;

    public GemspecParser(
        GemspecLineParser gemspecLineParser,
        EnumListFilter<com.blackduck.integration.detectable.detectables.rubygems.GemspecDependencyType> dependencyTypeFilter
    ) {
        this.gemspecLineParser = gemspecLineParser;
        this.dependencyTypeFilter = dependencyTypeFilter;
    }

    public DependencyGraph parse(InputStream inputStream) throws IOException {
        DependencyGraph dependencyGraph = new BasicDependencyGraph();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!gemspecLineParser.shouldParseLine(line)) {
                    continue;
                }

                Optional<GemspecDependency> gemspecDependencyOptional = gemspecLineParser.parseLine(line);
                if (!gemspecDependencyOptional.isPresent()) {
                    continue;
                }

                GemspecDependency gemspecDependency = gemspecDependencyOptional.get();

                if (dependencyTypeFilter.shouldExclude(com.blackduck.integration.detectable.detectables.rubygems.GemspecDependencyType.RUNTIME)
                    && gemspecDependency.getGemspecDependencyType() == GemspecDependencyType.RUNTIME
                ) {
                    logger.debug(String.format("Excluding component '%s' from graph because it is a runtime dependency", gemspecDependency.getName()));
                    continue;
                } else if (dependencyTypeFilter.shouldExclude(com.blackduck.integration.detectable.detectables.rubygems.GemspecDependencyType.DEV)
                    && gemspecDependency.getGemspecDependencyType() == GemspecDependencyType.DEVELOPMENT) {
                    logger.debug(String.format("Excluding component '%s' from graph because it is a development dependency", gemspecDependency.getName()));
                    continue;
                }
                String name = gemspecDependency.getName();
                String version = gemspecDependency.getVersion().orElse("No version");
                Dependency dependency = Dependency.FACTORY.createNameVersionDependency(Forge.RUBYGEMS, name, version);

                dependencyGraph.addChildrenToRoot(dependency);
            }
        }

        return dependencyGraph;
    }
}
