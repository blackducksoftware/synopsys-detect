/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pnpm.lockfile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.enums.DependencyType;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmLockYaml;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmPackage;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class PnpmYamlTransformer {
    private final ExternalIdFactory externalIdFactory;

    public PnpmYamlTransformer(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public CodeLocation generateCodeLocation(PnpmLockYaml pnpmLockYaml, DependencyTypeFilter dependencyTypeFilter, @Nullable NameVersion projectNameVersion) throws IntegrationException {
        List<String> rootPackageIds = extractRootPackageIds(pnpmLockYaml, dependencyTypeFilter);
        if (rootPackageIds.isEmpty()) {
            throw new IntegrationException("Could not parse any direct dependencies when parsing the pnpm-lock.yaml file.");
        }

        MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();

        buildGraph(dependencyGraph, rootPackageIds, pnpmLockYaml.packages, dependencyTypeFilter);

        return createCodeLocation(dependencyGraph, projectNameVersion);
    }

    private void buildGraph(MutableDependencyGraph graphBuilder, List<String> rootPackageIds, Map<String, @Nullable PnpmPackage> packageMap, DependencyTypeFilter dependencyTypeFilter) throws IntegrationException {
        if (packageMap == null) {
            throw new IntegrationException("Could not parse 'packages' section of the pnpm-lock.yaml file.");
        }
        for (Map.Entry<String, PnpmPackage> packageEntry : packageMap.entrySet()) {
            String packageId = packageEntry.getKey();
            if (rootPackageIds.contains(packageId)) {
                graphBuilder.addChildToRoot(buildDependencyFromPackageId(packageId));
            }
            PnpmPackage pnpmPackage = packageEntry.getValue();

            dependencyTypeFilter.ifReportingType(pnpmPackage.getDependencyType(), pnpmPackage.dependencies, dependencies -> {
                for (Map.Entry<String, String> dependency : dependencies.entrySet()) {
                    String dependencyPackageId = convertRawEntryToPackageId(dependency);
                    Dependency child = buildDependencyFromPackageId(dependencyPackageId);
                    graphBuilder.addChildWithParent(child, buildDependencyFromPackageId(packageId));
                }
            });
        }
    }

    private List<String> extractRootPackageIds(PnpmLockYaml pnpmLockYaml, DependencyTypeFilter dependencyTypeFilter) {
        Map<String, String> rawPackageInfo = new HashMap<>();
        dependencyTypeFilter.ifReportingType(DependencyType.APP, pnpmLockYaml.dependencies, rawPackageInfo::putAll);
        dependencyTypeFilter.ifReportingType(DependencyType.DEV, pnpmLockYaml.devDependencies, rawPackageInfo::putAll);
        dependencyTypeFilter.ifReportingType(DependencyType.OPTIONAL, pnpmLockYaml.optionalDependencies, rawPackageInfo::putAll);

        return rawPackageInfo.entrySet().stream()
            .map(this::convertRawEntryToPackageId)
            .collect(Collectors.toList());
    }

    private String convertRawEntryToPackageId(Map.Entry<String, String> entry) {
        String name = StringUtils.strip(entry.getKey(), "'");
        String version = entry.getValue();
        return String.format("/%s/%s", name, version);
    }

    private NameVersion parseNameVersionFromId(String id) {
        // ids follow format: /name/version, where name often contains slashes
        int indexOfLastSlash = id.lastIndexOf("/");
        String name = id.substring(1, indexOfLastSlash);
        String version = id.substring(indexOfLastSlash + 1);

        return new NameVersion(name, version);
    }

    private Dependency buildDependencyFromPackageId(String packageId) {
        NameVersion nameVersion = parseNameVersionFromId(packageId);
        return new Dependency(externalIdFactory.createNameVersionExternalId(Forge.NPMJS, nameVersion.getName(), nameVersion.getVersion()));
    }

    private CodeLocation createCodeLocation(DependencyGraph graph, @Nullable NameVersion nameVersion) {
        if (nameVersion != null) {
            return new CodeLocation(graph, externalIdFactory.createNameVersionExternalId(Forge.NPMJS, nameVersion.getName(), nameVersion.getVersion()));
        }
        return new CodeLocation(graph);
    }
}
