/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn.parse;

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

        addRootNodesToGraph(graphBuilder, yarnLockResult.getPackageJson(), productionOnly);

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

    private void addRootNodesToGraph(LazyExternalIdDependencyGraphBuilder graphBuilder, PackageJson packageJson, boolean productionOnly) {
        for (Map.Entry<String, String> packageDependency : packageJson.dependencies.entrySet()) {
            graphBuilder.addChildToRoot(new StringDependencyId(packageDependency.getKey() + "@" + packageDependency.getValue()));
        }

        if (!productionOnly) {
            for (Map.Entry<String, String> packageDependency : packageJson.devDependencies.entrySet()) {
                graphBuilder.addChildToRoot(new StringDependencyId(packageDependency.getKey() + "@" + packageDependency.getValue()));
            }
        }
        // TODO
        // v4 yarn.lock files have the root and it's dependencies (including workspaces)
        // v1 yarn.lock files do not, so the workspaces must be added to the root
        // Worse: Because v1 yarn.lock files don't define workspaces and their dependencies,
        // we'd need to mine that info from ALL of the workspace package.json files
        // What version of yarn did it switch to v4 yarn.lock? Can we support workspaces starting with that version?
        // Yarn 1 used yarn.lock v1. yarn 2 (from the beginning) has used yarn.lock v4 and included root project.
        // MY CURRENT THINKING IS: YARN V2 (YARN.LOCK V4) ONLY. Do not add workspaces as root dependencies,
        // but let the "dependencies" list do that if appropriate.
        // To be supported: yarn.lock should be v4:
        // __metadata:
        //  version: 4
        //
        //        for (PackageJson curWorkspacePackageJson : workspacePackageJsons.values()) {
        //            System.out.printf("* Processing workspace PackageJson: %s:%s\n", curWorkspacePackageJson.name, curWorkspacePackageJson.version);
        //            StringDependencyId workspaceStringDependencyId = new StringDependencyId(curWorkspacePackageJson.name + "@" + curWorkspacePackageJson.version);
        //            System.out.printf("WORKSPACE stringDependencyId: %s\n", workspaceStringDependencyId);
        //            graphBuilder.addChildToRoot(workspaceStringDependencyId);
        //        }
    }
}
