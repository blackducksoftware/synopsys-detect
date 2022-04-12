package com.synopsys.integration.detectable.detectables.carthage.transform;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.detectable.detectables.carthage.model.CarthageDeclaration;

public class CarthageDeclarationTransformer {
    private static final String GITHUB_ORIGIN_ID = "github";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DependencyGraph transform(List<CarthageDeclaration> carthageDeclarations) {
        DependencyGraph graph = new BasicDependencyGraph();
        carthageDeclarations.stream()
            .filter(this::isGitHubOrigin)
            .map(this::createGitHubDependencyFromDeclaration)
            .forEach(graph::addChildToRoot);
        return graph;
    }

    private boolean isGitHubOrigin(CarthageDeclaration declaration) {
        // Carthage supports declarations of dependencies via github org/repo, a URL, or a local path
        // As of now, Detect only supports dependencies with github origins
        // The KB does not have mappings for binaries, or resources that are not open source.  It has some mappings, though, for GitHub repos
        boolean isGitHubOrigin = GITHUB_ORIGIN_ID.equals(declaration.getOrigin());
        if (!isGitHubOrigin) {
            logger.info(String.format(
                "Excluding Carthage declaration %s:%s due to an unsupported origin: %s",
                declaration.getName(),
                declaration.getVersion(),
                declaration.getOrigin()
            ));
        }
        return isGitHubOrigin;
    }

    private Dependency createGitHubDependencyFromDeclaration(CarthageDeclaration declaration) {
        // Because the dependency is hosted on GitHub, we must use the github forge in order for KB to match it
        return Dependency.FACTORY.createNameVersionDependency(Forge.GITHUB, declaration.getName(), declaration.getVersion());
    }

}
