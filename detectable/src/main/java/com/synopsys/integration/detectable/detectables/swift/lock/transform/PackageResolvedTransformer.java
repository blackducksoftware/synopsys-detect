package com.synopsys.integration.detectable.detectables.swift.lock.transform;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.detectable.detectables.swift.lock.data.PackageResolved;
import com.synopsys.integration.detectable.detectables.swift.lock.data.PackageState;
import com.synopsys.integration.detectable.detectables.swift.lock.data.ResolvedPackage;

public class PackageResolvedTransformer {
    private static final String[] REPO_SUFFIX_TO_STRIP = { ".git" };

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DependencyGraph transform(PackageResolved packageResolved) {
        MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        packageResolved.getResolvedObject().getPackages().stream()
            .filter(Objects::nonNull)
            .map(this::convertToDependency)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(dependencyGraph::addChildToRoot);

        return dependencyGraph;
    }

    private Optional<Dependency> convertToDependency(ResolvedPackage resolvedPackage) {
        PackageState packageState = resolvedPackage.getPackageState();
        String repositoryURL = resolvedPackage.getRepositoryURL();
        try {
            String name = extractPackageName(repositoryURL);
            String version = packageState.getVersion();
            return Optional.of(Dependency.FACTORY.createNameVersionDependency(Forge.GITHUB, name, version));
        } catch (MalformedURLException exception) {
            logger.warn(String.format("Package '%s' has a malformed url. It cannot be added to the graph.", resolvedPackage.getPackageName()));
            logger.debug(String.format(
                "Package '%s', Version '%s', Branch '%s', Revision: '%s', MalformedURL '%s'",
                resolvedPackage.getPackageName(),
                packageState.getVersion(),
                StringUtils.defaultIfEmpty(packageState.getBranch(), "N/A"),
                packageState.getRevision(),
                repositoryURL
            ), exception);
            return Optional.empty();
        }
    }

    private String extractPackageName(String repositoryUrl) throws MalformedURLException {
        String cleanPackageName = new URL(repositoryUrl).getPath();
        cleanPackageName = StringUtils.strip(cleanPackageName, "/");
        for (String suffix : REPO_SUFFIX_TO_STRIP) {
            cleanPackageName = StringUtils.removeEnd(cleanPackageName, suffix);
        }
        return cleanPackageName;
    }
}
