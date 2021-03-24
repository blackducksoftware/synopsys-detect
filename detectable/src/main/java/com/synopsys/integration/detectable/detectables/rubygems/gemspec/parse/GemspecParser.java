/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.rubygems.gemspec.parse;

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

    public GemspecParser(ExternalIdFactory externalIdFactory, GemspecLineParser gemspecLineParser) {
        this.externalIdFactory = externalIdFactory;
        this.gemspecLineParser = gemspecLineParser;
    }

    public DependencyGraph parse(InputStream inputStream, boolean includeRuntimeDependencies, boolean includeDevelopmentDependencies) throws IOException {
        MutableMapDependencyGraph dependencyGraph = new MutableMapDependencyGraph();

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

                if (!includeRuntimeDependencies && gemspecDependency.getGemspecDependencyType() == GemspecDependencyType.RUNTIME) {
                    logger.debug(String.format("Excluding component '%s' from graph because it is a runtime dependency", gemspecDependency.getName()));
                    continue;
                } else if (!includeDevelopmentDependencies && gemspecDependency.getGemspecDependencyType() == GemspecDependencyType.DEVELOPMENT) {
                    logger.debug(String.format("Excluding component '%s' from graph because it is a development dependency", gemspecDependency.getName()));
                    continue;
                }
                String name = gemspecDependency.getName();
                String version = gemspecDependency.getVersion().orElse("No version");

                ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, name, version);
                Dependency dependency = new Dependency(name, version, externalId);

                dependencyGraph.addChildrenToRoot(dependency);
            }
        }

        return dependencyGraph;
    }
}
