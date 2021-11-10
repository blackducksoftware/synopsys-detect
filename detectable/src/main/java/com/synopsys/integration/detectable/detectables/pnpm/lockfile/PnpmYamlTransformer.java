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
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmLockYaml;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmPackage;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmProjectPackage;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class PnpmYamlTransformer {
    private static final String LINKED_PACKAGE_PREFIX = "link:";

    private final ExternalIdFactory externalIdFactory;

    public PnpmYamlTransformer(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public CodeLocation generateCodeLocation(PnpmLockYaml pnpmLockYaml, List<DependencyType> dependencyTypes, @Nullable NameVersion projectNameVersion, PnpmLinkedPackageResolver linkedPackageResolver) throws IntegrationException {
        return generateCodeLocation(convertPnpmLockYamlToPnpmProjectPackage(pnpmLockYaml), null, dependencyTypes, projectNameVersion, pnpmLockYaml.packages, linkedPackageResolver);
    }

    public CodeLocation generateCodeLocation(PnpmProjectPackage projectPackage, @Nullable String reportingProjectPackagePath, List<DependencyType> dependencyTypes, @Nullable NameVersion projectNameVersion,
        @Nullable Map<String, PnpmPackage> packageMap, PnpmLinkedPackageResolver linkedPackageResolver) throws IntegrationException {
        List<String> rootPackageIds = extractRootPackageIds(projectPackage, reportingProjectPackagePath, dependencyTypes, linkedPackageResolver);

        MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();

        buildGraph(dependencyGraph, rootPackageIds, packageMap, dependencyTypes, linkedPackageResolver, reportingProjectPackagePath);

        return createCodeLocation(dependencyGraph, projectNameVersion);
    }

    private void buildGraph(MutableDependencyGraph graphBuilder, List<String> rootPackageIds, @Nullable Map<String, PnpmPackage> packageMap, List<DependencyType> dependencyTypes, PnpmLinkedPackageResolver linkedPackageResolver,
        @Nullable String reportingProjectPackagePath)
        throws IntegrationException {
        if (packageMap == null) {
            throw new DetectableException("Could not parse 'packages' section of the pnpm-lock.yaml file.");
        }
        for (Map.Entry<String, PnpmPackage> packageEntry : packageMap.entrySet()) {
            String packageId = packageEntry.getKey();
            if (rootPackageIds.contains(packageId)) {
                graphBuilder.addChildToRoot(buildDependencyFromPackageId(packageId));
            }
            PnpmPackage pnpmPackage = packageEntry.getValue();

            if (dependencyTypes.contains(pnpmPackage.getDependencyType())) {
                for (Map.Entry<String, String> dependency : pnpmPackage.getDependencies().entrySet()) {
                    String dependencyPackageId = convertRawEntryToPackageId(dependency, linkedPackageResolver, reportingProjectPackagePath);
                    Dependency child = buildDependencyFromPackageId(dependencyPackageId);
                    graphBuilder.addChildWithParent(child, buildDependencyFromPackageId(packageId));
                }
            }
        }
    }

    private PnpmProjectPackage convertPnpmLockYamlToPnpmProjectPackage(PnpmLockYaml pnpmLockYaml) {
        PnpmProjectPackage pnpmProjectPackage = new PnpmProjectPackage();

        pnpmProjectPackage.dependencies = pnpmLockYaml.dependencies;
        pnpmProjectPackage.devDependencies = pnpmLockYaml.devDependencies;
        pnpmProjectPackage.optionalDependencies = pnpmLockYaml.optionalDependencies;

        return pnpmProjectPackage;
    }

    private List<String> extractRootPackageIds(PnpmProjectPackage pnpmProjectPackage, @Nullable String reportingProjectPackagePath, List<DependencyType> dependencyTypes, PnpmLinkedPackageResolver linkedPackageResolver) {
        Map<String, String> rawPackageInfo = new HashMap<>();
        if (dependencyTypes.contains(DependencyType.APP) && pnpmProjectPackage.dependencies != null) {
            rawPackageInfo.putAll(pnpmProjectPackage.dependencies);
        }
        if (dependencyTypes.contains(DependencyType.DEV) && pnpmProjectPackage.devDependencies != null) {
            rawPackageInfo.putAll(pnpmProjectPackage.devDependencies);
        }
        if (dependencyTypes.contains(DependencyType.OPTIONAL) && pnpmProjectPackage.optionalDependencies != null) {
            rawPackageInfo.putAll(pnpmProjectPackage.optionalDependencies);
        }

        return rawPackageInfo.entrySet().stream()
                   .map(entry -> convertRawEntryToPackageId(entry, linkedPackageResolver, reportingProjectPackagePath))
                   .collect(Collectors.toList());
    }

    private String convertRawEntryToPackageId(Map.Entry<String, String> entry, PnpmLinkedPackageResolver linkedPackageResolver, @Nullable String reportingProjectPackagePath) {
        String name = StringUtils.strip(entry.getKey(), "'");
        String version = entry.getValue();
        if (version.startsWith(LINKED_PACKAGE_PREFIX)) {
            // a linked project package's version will be referenced in the format: <linkPrefix><pathToLinkedPackageRelativeToReportingProjectPackage>
            version = linkedPackageResolver.resolveVersionOfLinkedPackage(reportingProjectPackagePath, version.replace(LINKED_PACKAGE_PREFIX, ""));
        }
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
