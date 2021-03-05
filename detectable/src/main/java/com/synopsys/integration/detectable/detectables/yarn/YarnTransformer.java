/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependencyid.StringDependencyId;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockResult;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntry;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryId;
import com.synopsys.integration.util.NameVersion;

public class YarnTransformer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;

    public YarnTransformer(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph transform(YarnLockResult yarnLockResult, boolean productionOnly, List<NameVersion> externalDependencies) throws MissingExternalIdException {
        LazyExternalIdDependencyGraphBuilder graphBuilder = new LazyExternalIdDependencyGraphBuilder();

        addRootNodesToGraph(graphBuilder, yarnLockResult.getRootPackageJson(), yarnLockResult.getWorkspacePackageJsons(), productionOnly);

        for (YarnLockEntry entry : yarnLockResult.getYarnLock().getEntries()) {
            for (YarnLockEntryId entryId : entry.getIds()) {
                StringDependencyId id = new StringDependencyId(entryId.getName() + "@" + entryId.getVersion());
                graphBuilder.setDependencyInfo(id, entryId.getName(), entry.getVersion(), externalIdFactory.createNameVersionExternalId(Forge.NPMJS, entryId.getName(), entry.getVersion()));
                for (YarnLockDependency dependency : entry.getDependencies()) {
                    StringDependencyId stringDependencyId = new StringDependencyId(dependency.getName() + "@" + dependency.getVersion());
                    if (!productionOnly || !dependency.isOptional()) {
                        graphBuilder.addChildWithParent(stringDependencyId, id);
                    } else {
                        logger.debug("Excluding optional dependency: {}", stringDependencyId.getValue());
                    }
                }
            }
        }
        return graphBuilder.build((dependencyId, lazyDependencyInfo) -> {
            Optional<NameVersion> externalDependency = externalDependencies.stream().filter(it -> it.getName().equals(lazyDependencyInfo.getName())).findFirst();
            Optional<ExternalId> externalId = externalDependency.map(it -> externalIdFactory.createNameVersionExternalId(Forge.NPMJS, it.getName(), it.getVersion()));

            if (externalId.isPresent()) {
                return externalId.get();
            } else {
                // If we don't construct an external ID here (which seems pointless), an exception will be thrown (which is worse; see IDETECT-1974)
                StringDependencyId stringDependencyId = (StringDependencyId) dependencyId;
                logger.warn(String.format("Missing yarn dependency. Dependency '%s' is missing from %s.", stringDependencyId.getValue(), yarnLockResult.getYarnLockFilePath()));
                return externalIdFactory.createNameVersionExternalId(Forge.NPMJS, stringDependencyId.getValue());
            }
        });
    }

    // TODO REMOVE THIS:
    private void addRootNodesToGraphORIG(LazyExternalIdDependencyGraphBuilder graphBuilder, PackageJson rootPackageJson, List<PackageJson> workspacePackageJsons, boolean productionOnly) {
        List<PackageJson> allPackageJsons = new LinkedList<>();
        allPackageJsons.add(rootPackageJson);
        allPackageJsons.addAll(workspacePackageJsons);
        /////// TODO THIS IS WRONG!! IT'S ADDING DEPENDENCIES OF package.jsons, but FOR WORKSPACES:
        // NEED TO ADD THE WORKSPACES THEMSELVES!!!!!!!
        for (PackageJson curPackageJson : allPackageJsons) {
            for (Map.Entry<String, String> packageDependency : curPackageJson.dependencies.entrySet()) {
                graphBuilder.addChildToRoot(new StringDependencyId(packageDependency.getKey() + "@" + packageDependency.getValue()));
            }
            if (!productionOnly) {
                for (Map.Entry<String, String> packageDependency : curPackageJson.devDependencies.entrySet()) {
                    graphBuilder.addChildToRoot(new StringDependencyId(packageDependency.getKey() + "@" + packageDependency.getValue()));
                }
            }
        }
    }

    // TODO the workspace jsons were in a map indexed by name
    // may need that to tie their dependencies back to them (which is not happening now)
    // Ah... the yarn.lock does not define dependencies for workspaces (in v1)
    // So... should we add each workspace's dependencies at the top level?
    // Or add the workspaces at the top level (like this does now) and then tie their dependencies
    // (from the workspace package.json files) to them?
    private void addRootNodesToGraph(LazyExternalIdDependencyGraphBuilder graphBuilder,
        PackageJson rootPackageJson, List<PackageJson> workspacePackageJsons, boolean productionOnly) {
        ///List<PackageJson> allPackageJsons = new LinkedList<>();
        ///allPackageJsons.add(rootPackageJson);
        // TODO Can we filter out the workspace references??
        ///////// TODO THIS IS WRONG: should not put workspace DEPENDENCIES at root level; just workspaces themselves!
        ///allPackageJsons.addAll(workspacePackageJsons.values());
        ///for (PackageJson curPackageJson : allPackageJsons) {
        System.out.printf("* Processing Root PackageJson: %s:%s\n", rootPackageJson.name, rootPackageJson.version);
        for (Map.Entry<String, String> packageDependency : rootPackageJson.dependencies.entrySet()) {
            StringDependencyId stringDependencyId = new StringDependencyId(packageDependency.getKey() + "@" + packageDependency.getValue());
            System.out.printf("ROOT stringDependencyId: %s\n", stringDependencyId);
            graphBuilder.addChildToRoot(stringDependencyId);
        }
        if (!productionOnly) {
            for (Map.Entry<String, String> packageDependency : rootPackageJson.devDependencies.entrySet()) {
                StringDependencyId stringDependencyId = new StringDependencyId(packageDependency.getKey() + "@" + packageDependency.getValue());
                System.out.printf("ROOT stringDependencyId [dev]: %s\n", stringDependencyId);
                graphBuilder.addChildToRoot(stringDependencyId);
            }
        }
        ///}
        for (PackageJson curWorkspacePackageJson : workspacePackageJsons) {
            System.out.printf("* Processing workspace PackageJson: %s:%s\n", curWorkspacePackageJson.name, curWorkspacePackageJson.version);
            StringDependencyId workspaceStringDependencyId = new StringDependencyId(curWorkspacePackageJson.name + "@" + curWorkspacePackageJson.version);
            System.out.printf("WORKSPACE stringDependencyId: %s\n", workspaceStringDependencyId);
            graphBuilder.addChildToRoot(workspaceStringDependencyId);
        }
    }
}
