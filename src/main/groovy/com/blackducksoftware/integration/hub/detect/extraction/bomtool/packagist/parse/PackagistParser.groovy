/*
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.packagist.parse

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency
import com.blackducksoftware.integration.hub.bdio.model.dependencyid.NameDependencyId
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.packagist.parse.model.PackagistPackage
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.util.NameVersion
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser

import groovy.transform.TypeChecked

@Component
@TypeChecked
class PackagistParser {
    private final Logger logger = LoggerFactory.getLogger(PackagistParser.class)

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    ExternalIdFactory externalIdFactory

    public PackagistParseResult getDependencyGraphFromProject(String sourcePath, String composerJsonText, String composerLockText) {
        LazyExternalIdDependencyGraphBuilder builder = new LazyExternalIdDependencyGraphBuilder();

        JsonObject composerJsonObject = new JsonParser().parse(composerJsonText) as JsonObject
        String projectName = composerJsonObject.get('name')?.getAsString()
        String projectVersion = composerJsonObject.get('version')?.getAsString()

        JsonObject composerLockObject = new JsonParser().parse(composerLockText) as JsonObject
        List<PackagistPackage> models = convertJsonToModel(composerLockObject, detectConfiguration.getPackagistIncludeDevDependencies());
        List<NameVersion> rootPackages = parseDependencies(composerJsonObject, detectConfiguration.getPackagistIncludeDevDependencies());

        models.each {
            ExternalId id = externalIdFactory.createNameVersionExternalId(Forge.PACKAGIST, it.nameVersion.name, it.nameVersion.version);
            NameDependencyId dependencyId = new NameDependencyId(it.nameVersion.name);
			builder.setDependencyInfo(dependencyId, it.nameVersion.name, it.nameVersion.version, id);
            if (isRootPackage(rootPackages, it.nameVersion)) {
                builder.addChildToRoot(dependencyId);
            }
            it.dependencies.each { child ->
                if (existsInPackages(models, child)) {
                    NameDependencyId childId = new NameDependencyId(child.name);
                    builder.addChildWithParent(childId, dependencyId);
                } else {
                    logger.warn("Dependency was not found in packages list but found a require that used it: " + child.name);
                }
            }
        }

        ExternalId projectExternalId;
        if (projectName == null || projectVersion == null) {
            projectExternalId = externalIdFactory.createPathExternalId(Forge.PACKAGIST, sourcePath);
        } else {
            projectExternalId = externalIdFactory.createNameVersionExternalId(Forge.PACKAGIST, projectName, projectVersion);
        }

        DependencyGraph graph = builder.build();

        DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(BomToolType.PACKAGIST, sourcePath, projectExternalId, graph).build();

        return new PackagistParseResult(projectName, projectVersion, codeLocation);
    }

    private boolean isRootPackage(List<NameVersion> rootPackages, NameVersion nameVersion) {
        return rootPackages.any {it -> it.getName().equals(nameVersion.getName()) };
    }

    private boolean existsInPackages(List<PackagistPackage> models, NameVersion nameVersion) {
        return models.any{ it -> it.getNameVersion().getName().equals(nameVersion.getName()) };
    }

    private List<PackagistPackage> convertJsonToModel(JsonObject lockfile, boolean checkDev) {
        List<PackagistPackage> packages = new ArrayList<>();
        lockfile.get('packages')?.getAsJsonArray().each {
            String currentRowPackageName = it.getAt('name').toString().replace('"', '');
            String currentRowPackageVersion = it.getAt('version').toString().replace('"', '');
            JsonObject packageJson = it.getAsJsonObject();
            NameVersion nameVersion = new NameVersion(currentRowPackageName, currentRowPackageVersion);
            List<NameVersion> dependencies = parseDependencies(packageJson, checkDev);
            packages.add(new PackagistPackage(nameVersion, dependencies));
        }
        return packages;
    }

    private List<NameVersion> parseDependencies(JsonObject packageJson, boolean checkDev){
        JsonObject requireObject;

		JsonObject require = packageJson.get('require')?.getAsJsonObject()
		List<NameVersion> dependencies = parseDependenciesFromRequire(require);
		
        if (checkDev) {
            JsonObject devRequire = packageJson.get('require-dev')?.getAsJsonObject();
			dependencies.addAll(parseDependenciesFromRequire(devRequire));
        }

        return dependencies;
    }
	
	private List<NameVersion> parseDependenciesFromRequire(JsonObject requireObject){
		List<NameVersion> dependencies = new ArrayList<>();
		requireObject?.entrySet().each {
			if (!it.key.equalsIgnoreCase('php')) {
				NameVersion nameVersion = new NameVersion(it.getKey().toString(), it.getValue().toString());
				dependencies.add(nameVersion);
			}
		}
		return dependencies;
	}
}
