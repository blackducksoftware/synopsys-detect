/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.xcode;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.xcode.model.PackageResolved;
import com.synopsys.integration.detectable.detectables.xcode.model.PackageState;
import com.synopsys.integration.detectable.detectables.xcode.model.ResolvedPackage;

public class PackageResolvedTransformer {
    protected static final String[] KNOWN_FILE_FORMAT_VERSIONS = { "1" };
    private static final String[] REPO_SUFFIX_TO_STRIP = { ".git" };

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExternalIdFactory externalIdFactory;

    public PackageResolvedTransformer(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph transform(PackageResolved packageResolved) {
        logVersionCompatibility(packageResolved);

        MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        packageResolved.getResolvedObject().getPackages().stream()
            .map(this::convertToDependency)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(dependencyGraph::addChildToRoot);

        return dependencyGraph;
    }

    private void logVersionCompatibility(PackageResolved packageResolved) {
        String fileFormatVersion = packageResolved.getFileFormatVersion();
        boolean versionMatches = Arrays.asList(KNOWN_FILE_FORMAT_VERSIONS).contains(fileFormatVersion);
        if (!versionMatches) {
            logger.warn(String.format("The format version of Package.resolved (%s) is unknown to Detect, but will attempt to parse anyway. Known format versions are (%s).",
                fileFormatVersion,
                StringUtils.join(KNOWN_FILE_FORMAT_VERSIONS, ", ")
            ));
        }
    }

    private Optional<Dependency> convertToDependency(ResolvedPackage resolvedPackage) {
        PackageState packageState = resolvedPackage.getPackageState();
        String repositoryURL = resolvedPackage.getRepositoryURL();
        try {
            String name = extractPackageName(repositoryURL);
            String version = packageState.getVersion();
            ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.GITHUB, name, version);
            return Optional.of(new Dependency(name, version, externalId));
        } catch (MalformedURLException exception) {
            logger.warn(String.format("Package '%s' has a malformed url. It cannot be added to the graph.", resolvedPackage.getPackageName()));
            logger.debug(String.format("Package '%s', Version '%s', Branch '%s', Revision: '%s', MalformedURL '%s'",
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
