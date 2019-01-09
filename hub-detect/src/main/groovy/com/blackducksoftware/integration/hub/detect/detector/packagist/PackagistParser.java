/**
 * hub-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.detect.detector.packagist;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocationType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.hub.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.synopsys.integration.hub.bdio.model.Forge;
import com.synopsys.integration.hub.bdio.model.dependencyid.NameDependencyId;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalId;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.util.NameVersion;

public class PackagistParser {
    private final Logger logger = LoggerFactory.getLogger(PackagistParser.class);

    private final ExternalIdFactory externalIdFactory;
    private final DetectConfiguration detectConfiguration;

    public PackagistParser(final ExternalIdFactory externalIdFactory, final DetectConfiguration detectConfiguration) {
        this.externalIdFactory = externalIdFactory;
        this.detectConfiguration = detectConfiguration;
    }

    public PackagistParseResult getDependencyGraphFromProject(final String sourcePath, final String composerJsonText, final String composerLockText) {
        final LazyExternalIdDependencyGraphBuilder builder = new LazyExternalIdDependencyGraphBuilder();

        final JsonObject composerJsonObject = new JsonParser().parse(composerJsonText).getAsJsonObject();
        final NameVersion projectNameVersion = parseNameVersionFromJson(composerJsonObject);

        final JsonObject composerLockObject = new JsonParser().parse(composerLockText).getAsJsonObject();
        final List<PackagistPackage> models = convertJsonToModel(composerLockObject, detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PACKAGIST_INCLUDE_DEV_DEPENDENCIES, PropertyAuthority.None));
        final List<NameVersion> rootPackages = parseDependencies(composerJsonObject, detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PACKAGIST_INCLUDE_DEV_DEPENDENCIES, PropertyAuthority.None));

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

        ExternalId projectExternalId;
        if (projectNameVersion.getName() == null || projectNameVersion.getVersion() == null) {
            projectExternalId = externalIdFactory.createPathExternalId(Forge.PACKAGIST, sourcePath);
        } else {
            projectExternalId = externalIdFactory.createNameVersionExternalId(Forge.PACKAGIST, projectNameVersion.getName(), projectNameVersion.getVersion());
        }

        final DependencyGraph graph = builder.build();
        final DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(DetectCodeLocationType.PACKAGIST, sourcePath, projectExternalId, graph).build();

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
        final List<PackagistPackage> packages = new ArrayList<>();
        lockfile.get("packages").getAsJsonArray().forEach(it -> {
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
            if (!it.getKey().equalsIgnoreCase("php")) {
                final NameVersion nameVersion = new NameVersion(it.getKey().toString(), it.getValue().toString());
                dependencies.add(nameVersion);
            }
        });
        return dependencies;
    }

}
