/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.packagist.parse;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependencyid.NameDependencyId;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.packagist.model.PackagistPackage;
import com.synopsys.integration.detectable.detectables.packagist.model.PackagistParseResult;
import com.synopsys.integration.util.NameVersion;

public class PackagistParser {
    private final Logger logger = LoggerFactory.getLogger(PackagistParser.class);

    private final ExternalIdFactory externalIdFactory;

    public PackagistParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public PackagistParseResult getDependencyGraphFromProject(final String composerJsonText, final String composerLockText, boolean includeDevDependencies) throws MissingExternalIdException {
        final LazyExternalIdDependencyGraphBuilder builder = new LazyExternalIdDependencyGraphBuilder();

        final JsonObject composerJsonObject = new JsonParser().parse(composerJsonText).getAsJsonObject();
        final NameVersion projectNameVersion = parseNameVersionFromJson(composerJsonObject);

        final JsonObject composerLockObject = new JsonParser().parse(composerLockText).getAsJsonObject();
        final List<PackagistPackage> models = convertJsonToModel(composerLockObject, includeDevDependencies);
        final List<NameVersion> rootPackages = parseDependencies(composerJsonObject, includeDevDependencies);

        models.forEach(it -> {
            final ExternalId id = externalIdFactory.createNameVersionExternalId(Forge.PACKAGIST, it.getNameVersion().getName(), it.getNameVersion().getVersion());
            final NameDependencyId dependencyId = new NameDependencyId(it.getNameVersion().getName());
            builder.setDependencyInfo(dependencyId, it.getNameVersion().getName(), it.getNameVersion().getVersion(), id);
            if (isRootPackage(it.getNameVersion(), rootPackages)) {
                builder.addChildToRoot(dependencyId);
            }
            it.getDependencies().forEach(child -> {
                if (existsInPackages(child, models)) {
                    final NameDependencyId childId = new NameDependencyId(child.getName());
                    builder.addChildWithParent(childId, dependencyId);
                } else {
                    logger.warn("Dependency was not found in packages list but found a require that used it: " + child.getName());
                }
            });
        });
        final DependencyGraph graph = builder.build();

        final CodeLocation codeLocation;
        if (projectNameVersion.getName() == null || projectNameVersion.getVersion() == null) {
            codeLocation = new CodeLocation(graph);
        } else {
            codeLocation = new CodeLocation(graph, externalIdFactory.createNameVersionExternalId(Forge.PACKAGIST, projectNameVersion.getName(), projectNameVersion.getVersion()));
        }
        return new PackagistParseResult(projectNameVersion.getName(), projectNameVersion.getVersion(), codeLocation);
    }

    private NameVersion parseNameVersionFromJson(final JsonObject json) {
        final JsonElement nameElement = json.get("name");
        final JsonElement versionElement = json.get("version");

        String name = null;
        String version = null;

        if (nameElement != null) {
            name = nameElement.toString().replace("\"", "");
        }
        if (versionElement != null) {
            version = versionElement.toString().replace("\"", "");
        }

        return new NameVersion(name, version);
    }

    private boolean isRootPackage(final NameVersion nameVersion, final List<NameVersion> rootPackages) {
        return rootPackages.stream().anyMatch(it -> it.getName().equals(nameVersion.getName()));
    }

    private boolean existsInPackages(final NameVersion nameVersion, final List<PackagistPackage> models) {
        return models.stream().anyMatch(it -> it.getNameVersion().getName().equals(nameVersion.getName()));
    }

    private List<PackagistPackage> convertJsonToModel(final JsonObject lockfile, final boolean checkDev) {
        final List<PackagistPackage> packages =
                new ArrayList<>(convertJsonToModel(lockfile.get("packages").getAsJsonArray(), checkDev));
        if (checkDev) {
            packages.addAll(convertJsonToModel(lockfile.get("packages-dev").getAsJsonArray(), checkDev));
        }
        return packages;
    }

    private List<PackagistPackage> convertJsonToModel(final JsonArray packagesProperty, final boolean checkDev) {
        final List<PackagistPackage> packages = new ArrayList<>();
        packagesProperty.forEach(it -> {
            if (it.isJsonObject()) {
                final JsonObject itObject = it.getAsJsonObject();
                final NameVersion nameVersion = parseNameVersionFromJson(itObject);
                final List<NameVersion> dependencies = parseDependencies(itObject, checkDev);
                packages.add(new PackagistPackage(nameVersion, dependencies));
            }
        });
        return packages;
    }

    private List<NameVersion> parseDependencies(final JsonObject packageJson, final boolean checkDev) {
        final List<NameVersion> dependencies = new ArrayList<>();

        final JsonElement require = packageJson.get("require");
        if (require != null && require.isJsonObject()) {
            dependencies.addAll(parseDependenciesFromRequire(require.getAsJsonObject()));
        }

        if (checkDev) {
            final JsonElement devRequire = packageJson.get("require-dev");
            if (devRequire != null && devRequire.isJsonObject()) {
                dependencies.addAll(parseDependenciesFromRequire(devRequire.getAsJsonObject()));
            }

        }

        return dependencies;
    }

    private List<NameVersion> parseDependenciesFromRequire(final JsonObject requireObject) {
        final List<NameVersion> dependencies = new ArrayList<>();
        requireObject.entrySet().forEach(it -> {
            if (!"php".equalsIgnoreCase(it.getKey())) {
                final NameVersion nameVersion = new NameVersion(it.getKey(), it.getValue().toString());
                dependencies.add(nameVersion);
            }
        });
        return dependencies;
    }

}
