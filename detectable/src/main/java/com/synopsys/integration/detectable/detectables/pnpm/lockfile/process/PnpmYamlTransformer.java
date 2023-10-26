package com.synopsys.integration.detectable.detectables.pnpm.lockfile.process;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmDependencyInfo;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmDependencyType;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmLockYamlv6;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmPackageInfo;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmProjectPackage;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmProjectPackagev6;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class PnpmYamlTransformer {
    private static final String LINKED_PACKAGE_PREFIX = "link:";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EnumListFilter<PnpmDependencyType> dependencyTypeFilter;

    public PnpmYamlTransformer(EnumListFilter<PnpmDependencyType> dependencyTypeFilter) {
        this.dependencyTypeFilter = dependencyTypeFilter;
    }

    public CodeLocation generateCodeLocation(File sourcePath, PnpmLockYamlv6 pnpmLockYaml, @Nullable NameVersion projectNameVersion, PnpmLinkedPackageResolver linkedPackageResolver)
        throws IntegrationException {
        return generateCodeLocation(sourcePath, convertPnpmLockYamlToPnpmProjectPackage(pnpmLockYaml), null, projectNameVersion, pnpmLockYaml.packages, linkedPackageResolver);
    }

    public CodeLocation generateCodeLocation(
        File sourcePath,
        PnpmProjectPackagev6 projectPackage,
        @Nullable String reportingProjectPackagePath,
        @Nullable NameVersion projectNameVersion,
        @Nullable Map<String, PnpmPackageInfo> packageMap,
        PnpmLinkedPackageResolver linkedPackageResolver
    ) throws IntegrationException {
        DependencyGraph dependencyGraph = new BasicDependencyGraph();
        List<String> rootPackageIds = extractRootPackageIds(projectPackage, reportingProjectPackagePath, linkedPackageResolver);
        buildGraph(dependencyGraph, rootPackageIds, packageMap, linkedPackageResolver, reportingProjectPackagePath);

        if (projectNameVersion != null) {
            return new CodeLocation(
                dependencyGraph,
                ExternalId.FACTORY.createNameVersionExternalId(Forge.NPMJS, projectNameVersion.getName(), projectNameVersion.getVersion()),
                sourcePath
            );
        }
        return new CodeLocation(dependencyGraph, sourcePath);
    }

    private void buildGraph(
        DependencyGraph graphBuilder,
        List<String> rootPackageIds,
        @Nullable Map<String, PnpmPackageInfo> packageMap,
        PnpmLinkedPackageResolver linkedPackageResolver,
        @Nullable String reportingProjectPackagePath
    ) throws IntegrationException {
        if (packageMap == null) {
            throw new DetectableException("Could not parse 'packages' section of the pnpm-lock.yaml file.");
        }
        for (Map.Entry<String, PnpmPackageInfo> packageEntry : packageMap.entrySet()) {
//            String packageEntryKey = packageEntry.getKey();
//            int indexOfLastAt = packageEntryKey.lastIndexOf("@");
//            String name = packageEntryKey.substring(0, indexOfLastAt);
//            String version = packageEntryKey.substring(indexOfLastAt + 1);
//            String packageId = name + "/" + version;  

            //String packageId = packageEntry.getKey();
            String packageId = packageEntry.getKey();
            Optional<Dependency> pnpmPackage = buildDependencyFromPackageEntry(packageEntry);
            if (!pnpmPackage.isPresent()) {
                logger.debug(String.format("Could not add package %s to the graph.", packageId));
                continue;
            }

            if (isRootPackage(packageId, rootPackageIds)) {
                graphBuilder.addChildToRoot(pnpmPackage.get());
            }

//            PnpmPackageInfo packageInfo = packageEntry.getValue();
//            if (!packageInfo.getDependencyType().isPresent() || dependencyTypeFilter.shouldInclude(packageInfo.getDependencyType().get())) {
//                for (Map.Entry<String, String> packageDependency : packageInfo.getDependencies().entrySet()) {
//                    String dependencyPackageId = convertRawEntryToPackageId(packageDependency, linkedPackageResolver, reportingProjectPackagePath);
//                    Optional<Dependency> child = buildDependencyFromPackageId(dependencyPackageId);
//                    child.ifPresent(c -> graphBuilder.addChildWithParent(child.get(), pnpmPackage.get()));
//                }
//            }
        }
    }

    private PnpmProjectPackagev6 convertPnpmLockYamlToPnpmProjectPackage(PnpmLockYamlv6 pnpmLockYaml) {
        PnpmProjectPackagev6 pnpmProjectPackage = new PnpmProjectPackagev6();

        // TODO seems possible to have these at the root of the lock, need to update the POJOs
//        pnpmProjectPackage.dependencies = pnpmLockYaml.dependencies;
//        pnpmProjectPackage.devDependencies = pnpmLockYaml.devDependencies;
//        pnpmProjectPackage.optionalDependencies = pnpmLockYaml.optionalDependencies;

        return pnpmProjectPackage;
    }

    private List<String> extractRootPackageIds(
        PnpmProjectPackagev6 pnpmProjectPackage,
        @Nullable String reportingProjectPackagePath,
        PnpmLinkedPackageResolver linkedPackageResolver
    ) {
        Map<String, PnpmDependencyInfo> rawPackageInfo = new HashMap<>();
        if (pnpmProjectPackage.dependencies != null) {
            rawPackageInfo.putAll(pnpmProjectPackage.dependencies);
        }
        dependencyTypeFilter.ifShouldInclude(PnpmDependencyType.DEV, pnpmProjectPackage.devDependencies, rawPackageInfo::putAll);
        dependencyTypeFilter.ifShouldInclude(PnpmDependencyType.OPTIONAL, pnpmProjectPackage.optionalDependencies, rawPackageInfo::putAll);

        return rawPackageInfo.entrySet().stream()
            .map(entry -> convertPnpmDependencyEntryToPackageId(entry, linkedPackageResolver, reportingProjectPackagePath))
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
    
    private String convertPnpmDependencyEntryToPackageId(Map.Entry<String, PnpmDependencyInfo> entry, PnpmLinkedPackageResolver linkedPackageResolver, @Nullable String reportingProjectPackagePath) {
        String name = StringUtils.strip(entry.getKey(), "'");
        String version = entry.getValue().version;
        if (version.startsWith(LINKED_PACKAGE_PREFIX)) {
            // a linked project package's version will be referenced in the format: <linkPrefix><pathToLinkedPackageRelativeToReportingProjectPackage>
            version = linkedPackageResolver.resolveVersionOfLinkedPackage(reportingProjectPackagePath, version.replace(LINKED_PACKAGE_PREFIX, ""));
        }
        return String.format("/%s/%s", name, version);
    }

    private Optional<NameVersion> parseNameVersionFromId(String id) {
        // ids follow format: /name/version, where name often contains slashes
        try {
         // TODO have to keep both code paths
         if (id.contains("(")) {
             // TODO nasty one
             // @algolia/autocomplete-preset-algolia@1.7.1(@algolia/client-search@4.14.2)(algoliasearch@4.14.2)
             id = id.split("\\(")[0];
         }
          
//             Pattern pattern = Pattern.compile("\\@(.*?)\\(");
//             Matcher matcher = pattern.matcher(id);
//             matcher.find();
//             String version = matcher.group(1);
//             return Optional.of(new NameVersion("name", version));
//         } else {
             int indexOfLastSlash = id.lastIndexOf("@");
             String name = id.substring(1, indexOfLastSlash);
             String version = id.substring(indexOfLastSlash + 1);
             return Optional.of(new NameVersion(name, version));
//         }
        } catch (Exception e) {
            logger.debug(String.format("There was an issue parsing package id: %s.  This is likely an unsupported format.", id));
            return Optional.empty();
        }
    }
    
    private Optional<NameVersion> parseNameVersionFromIdForRoot(String id) {
        // ids follow format: /name/version, where name often contains slashes
        try {
            // TODO if this works could send in the separator I want
            int indexOfLastSlash = id.lastIndexOf("/");
            String name = id.substring(1, indexOfLastSlash);
            String version = id.substring(indexOfLastSlash + 1);
            return Optional.of(new NameVersion(name, version));
        } catch (Exception e) {
            logger.debug(String.format("There was an issue parsing package id: %s.  This is likely an unsupported format.", id));
            return Optional.empty();
        }
    }

    private Optional<Dependency> buildDependencyFromPackageEntry(Map.Entry<String, PnpmPackageInfo> packageEntry) {
        PnpmPackageInfo packageInfo = packageEntry.getValue();
        if (packageInfo.name != null) {
            return Optional.of(Dependency.FACTORY.createNameVersionDependency(Forge.NPMJS, packageInfo.name, packageInfo.version));
        }
        return buildDependencyFromPackageId(packageEntry.getKey());
    }

    private Optional<Dependency> buildDependencyFromPackageId(String packageId) {
        return parseNameVersionFromId(packageId)
            .map(nameVersion -> Dependency.FACTORY.createNameVersionDependency(Forge.NPMJS, nameVersion.getName(), nameVersion.getVersion()));
    }

    private boolean isRootPackage(String id, List<String> rootIds) {
        return compareIgnoringSeparator(id, rootIds) ||
            rootIds.stream()
                .map(this::parseNameVersionFromIdForRoot)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(NameVersion::getVersion)
                .anyMatch(id::equals); // for file dependencies, they are declared as <name> : <fileIdAsReportedInPackagesSection>
    }

    private boolean compareIgnoringSeparator(String s1, List<String> stringsToCheck) {
        for (String s2 : stringsToCheck) {
            if (s1.replace("@", "/").equals(s2.replace("@", "/"))) {
                return true;
            }
        }
        
        return false;
    }
    
}
