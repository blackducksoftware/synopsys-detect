package com.synopsys.integration.detectable.detectables.gradle.gogradle;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class GoGradleLockParser {
    private final ExternalIdFactory externalIdFactory;

    public GoGradleLockParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parse(final File goGradleLockFile) throws IOException {
        final MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        final YAMLMapper mapper = new YAMLMapper();
        for (final JsonNode scopeNodes : mapper.readTree(goGradleLockFile)) {
            for (final JsonNode scopeNodeContent : scopeNodes) {
                for (final JsonNode dependencyNode : scopeNodeContent) {
                    final Optional<String> name = Optional.ofNullable(dependencyNode.get("name")).map(JsonNode::textValue);
                    final Optional<String> commit = Optional.ofNullable(dependencyNode.get("commit")).map(JsonNode::textValue);

                    if (name.isPresent() && commit.isPresent()) {
                        String dependencyName = name.get();
                        if (dependencyName.startsWith("golang.org/x/")) {
                            dependencyName = dependencyName.replace("golang.org/x/", "");
                        }
                        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.GOLANG, dependencyName, commit.get());
                        final Dependency dependency = new Dependency(externalId);
                        dependencyGraph.addChildToRoot(dependency);
                    }
                }
            }
        }

        return dependencyGraph;
    }
}
