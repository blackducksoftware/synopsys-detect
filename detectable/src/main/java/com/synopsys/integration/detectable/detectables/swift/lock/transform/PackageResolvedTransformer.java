package com.synopsys.integration.detectable.detectables.swift.lock.transform;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.detectable.detectables.git.cli.GitUrlParser;
import com.synopsys.integration.detectable.detectables.swift.lock.data.PackageState;
import com.synopsys.integration.detectable.detectables.swift.lock.data.ResolvedPackage;

public class PackageResolvedTransformer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GitUrlParser gitUrlParser;

    public PackageResolvedTransformer(GitUrlParser gitUrlParser) {
        this.gitUrlParser = gitUrlParser;
    }

    public DependencyGraph transform(List<ResolvedPackage> resolvedPackages) {
        DependencyGraph dependencyGraph = new BasicDependencyGraph();
        resolvedPackages.stream()
            .filter(Objects::nonNull)
            .map(this::convertToDependency)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(dependencyGraph::addDirectDependency);

        return dependencyGraph;
    }

    private Optional<Dependency> convertToDependency(ResolvedPackage resolvedPackage) {
        PackageState packageState = resolvedPackage.getPackageState();
        String location = resolvedPackage.getLocation();
        try {
            String name = gitUrlParser.getRepoName(location);
            String version = packageState.getVersion();
            return Optional.of(Dependency.FACTORY.createNameVersionDependency(Forge.GITHUB, name, version));
        } catch (MalformedURLException exception) {
            logger.warn(String.format("Package '%s' has a malformed url. It cannot be added to the graph. Please contact support.", resolvedPackage.getIdentity()));
            logger.debug(String.format(
                "Package '%s', Version '%s', Branch '%s', Revision '%s', Location '%s'%s",
                resolvedPackage.getIdentity(),
                packageState.getVersion(),
                StringUtils.defaultIfEmpty(packageState.getBranch(), "N/A"),
                packageState.getRevision(),
                location,
                resolvedPackage.getKind().map(kind -> String.format(", Kind '%s'", kind)).orElse("")
            ), exception);
            return Optional.empty();
        }
    }
}
