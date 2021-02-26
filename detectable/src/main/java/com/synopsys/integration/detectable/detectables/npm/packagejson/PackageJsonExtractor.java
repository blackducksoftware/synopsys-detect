/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.npm.packagejson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;

public class PackageJsonExtractor {
    private final Gson gson;
    private final ExternalIdFactory externalIdFactory;

    public PackageJsonExtractor(final Gson gson, final ExternalIdFactory externalIdFactory) {
        this.gson = gson;
        this.externalIdFactory = externalIdFactory;
    }

    public Extraction extract(final InputStream packageJsonInputStream, final boolean includeDevDependencies) {
        final Reader packageJsonReader = new InputStreamReader(packageJsonInputStream);
        final PackageJson packageJson = gson.fromJson(packageJsonReader, PackageJson.class);

        return extract(packageJson, includeDevDependencies);
    }

    public Extraction extract(final PackageJson packageJson, final boolean includeDevDependencies) {
        final List<Dependency> dependencies = packageJson.dependencies.entrySet().stream()
                                                  .map(this::entryToDependency)
                                                  .collect(Collectors.toList());

        if (includeDevDependencies) {
            final List<Dependency> devDependencies = packageJson.devDependencies.entrySet().stream()
                                                         .map(this::entryToDependency)
                                                         .collect(Collectors.toList());
            dependencies.addAll(devDependencies);
        }

        final MutableMapDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        dependencyGraph.addChildrenToRoot(dependencies);

        final CodeLocation codeLocation = new CodeLocation(dependencyGraph);

        final String projectName = StringUtils.stripToNull(packageJson.name);
        final String projectVersion = StringUtils.stripToNull(packageJson.version);

        return new Extraction.Builder()
                   .success(codeLocation)
                   .projectName(projectName)
                   .projectVersion(projectVersion)
                   .build();
    }

    private Dependency entryToDependency(final Map.Entry<String, String> dependencyEntry) {
        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, dependencyEntry.getKey(), dependencyEntry.getValue());
        return new Dependency(externalId);
    }

}
