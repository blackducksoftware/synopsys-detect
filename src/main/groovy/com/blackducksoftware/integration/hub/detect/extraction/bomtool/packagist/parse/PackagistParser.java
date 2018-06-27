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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.packagist.parse;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependencyid.NameDependencyId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.packagist.parse.model.PackagistPackage;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.util.NameVersion;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component
public class PackagistParser {
    private final Logger logger = LoggerFactory.getLogger(PackagistParser.class);

    @Autowired
    DetectConfiguration detectConfiguration;

    @Autowired
    ExternalIdFactory externalIdFactory;

    public PackagistParseResult getDependencyGraphFromProject(String sourcePath, String composerJsonText, String composerLockText) {
        LazyExternalIdDependencyGraphBuilder builder = new LazyExternalIdDependencyGraphBuilder();

        JsonObject composerJsonObject = new JsonParser().parse(composerJsonText).getAsJsonObject();
        NameVersion projectNameVersion = parseNameVersionFromJson(composerJsonObject);
        
        JsonObject composerLockObject = new JsonParser().parse(composerLockText).getAsJsonObject();
        List<PackagistPackage> models = convertJsonToModel(composerLockObject, detectConfiguration.getPackagistIncludeDevDependencies());
        List<NameVersion> rootPackages = parseDependencies(composerJsonObject, detectConfiguration.getPackagistIncludeDevDependencies());

        models.forEach(it -> {
            ExternalId id = externalIdFactory.createNameVersionExternalId(Forge.PACKAGIST, it.getNameVersion().getName(), it.getNameVersion().getVersion());
            NameDependencyId dependencyId = new NameDependencyId(it.getNameVersion().getName());
			builder.setDependencyInfo(dependencyId, it.getNameVersion().getName(), it.getNameVersion().getVersion(), id);
            if (isRootPackage(it.getNameVersion(), rootPackages)) {
                builder.addChildToRoot(dependencyId);
            }
            it.getDependencies().forEach (child -> {
                if (existsInPackages(child, models)) {
                    NameDependencyId childId = new NameDependencyId(child.getName());
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

        DependencyGraph graph = builder.build();
        DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(BomToolType.PACKAGIST, sourcePath, projectExternalId, graph).build();
        
        return new PackagistParseResult(projectNameVersion.getName(), projectNameVersion.getVersion(), codeLocation);
    }

    private NameVersion parseNameVersionFromJson(JsonObject json) {
    	JsonElement nameElement = json.get("name");
    	JsonElement versionElement = json.get("version");
    	
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
    
    private boolean isRootPackage(NameVersion nameVersion, List<NameVersion> rootPackages) {
        return rootPackages.stream().anyMatch(it -> it.getName().equals(nameVersion.getName()));
    }

    private boolean existsInPackages(NameVersion nameVersion, List<PackagistPackage> models) {
        return models.stream().anyMatch( it -> it.getNameVersion().getName().equals(nameVersion.getName()));
    }

    private List<PackagistPackage> convertJsonToModel(JsonObject lockfile, boolean checkDev) {
        List<PackagistPackage> packages = new ArrayList<>();
        lockfile.get("packages").getAsJsonArray().forEach(it -> {
        	if (it.isJsonObject()) {
            	JsonObject itObject = it.getAsJsonObject();
            	NameVersion nameVersion = parseNameVersionFromJson(itObject);
	            List<NameVersion> dependencies = parseDependencies(itObject, checkDev);
	            packages.add(new PackagistPackage(nameVersion, dependencies));
        	}
        });
        return packages;
    }

    private List<NameVersion> parseDependencies(JsonObject packageJson, boolean checkDev){
    	List<NameVersion> dependencies = new ArrayList<>();
    	
    	JsonElement require = packageJson.get("require");
    	if (require != null && require.isJsonObject()) {
    		dependencies.addAll(parseDependenciesFromRequire(require.getAsJsonObject()));
    	}
		
        if (checkDev) {
            JsonElement devRequire = packageJson.get("require-dev");
    		if (devRequire != null && devRequire.isJsonObject()) {
    			dependencies.addAll(parseDependenciesFromRequire(devRequire.getAsJsonObject()));
    		}
			
        }

        return dependencies;
    }
	
	private List<NameVersion> parseDependenciesFromRequire(JsonObject requireObject){
		List<NameVersion> dependencies = new ArrayList<>();
		requireObject.entrySet().forEach(it -> {
			if (!it.getKey().equalsIgnoreCase("php")) {
				NameVersion nameVersion = new NameVersion(it.getKey().toString(), it.getValue().toString());
				dependencies.add(nameVersion);
			}
		});
		return dependencies;
	}
}
