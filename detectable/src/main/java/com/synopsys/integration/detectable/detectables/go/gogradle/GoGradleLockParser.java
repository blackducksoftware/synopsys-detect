package com.synopsys.integration.detectable.detectables.go.gogradle;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.exception.IntegrationException;

public class GoGradleLockParser {
    private final ExternalIdFactory externalIdFactory;

    public GoGradleLockParser(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parse(File goGradleLockFile) throws IOException, IntegrationException {
        DependencyGraph dependencyGraph = new BasicDependencyGraph();
        YAMLMapper mapper = new YAMLMapper();
        JsonNode rootNode = mapper.readTree(goGradleLockFile);
        JsonNode buildNode = rootNode.findPath("build");

        if (buildNode == null) {
            throw new IntegrationException(String.format("Failed to find build node in %s", GoGradleDetectable.GO_GRADLE_LOCK));
        }

        for (JsonNode dependencyNode : buildNode) {
            Optional<String> name = Optional.ofNullable(dependencyNode.get("name")).map(JsonNode::textValue);
            Optional<String> commit = Optional.ofNullable(dependencyNode.get("commit")).map(JsonNode::textValue);

            if (name.isPresent() && commit.isPresent()) {
                String dependencyName = name.get();
                if (dependencyName.startsWith("golang.org/x/")) {
                    dependencyName = dependencyName.replace("golang.org/x/", "");
                }
                ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.GOLANG, dependencyName, commit.get());
                Dependency dependency = new Dependency(externalId);
                dependencyGraph.addDirectDependency(dependency);
            }
        }

        return dependencyGraph;
    }
}
