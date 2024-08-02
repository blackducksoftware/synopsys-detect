package com.synopsys.integration.detectable.detectables.npm.packagejson;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.npm.NpmDependencyType;
import com.synopsys.integration.detectable.extraction.Extraction;

public class PackageJsonExtractor {
    private final Gson gson;
    private final ExternalIdFactory externalIdFactory;
    private final EnumListFilter<NpmDependencyType> npmDependencyTypeFilter;

    public PackageJsonExtractor(Gson gson, ExternalIdFactory externalIdFactory, EnumListFilter<NpmDependencyType> npmDependencyTypeFilter) {
        this.gson = gson;
        this.externalIdFactory = externalIdFactory;
        this.npmDependencyTypeFilter = npmDependencyTypeFilter;
    }

    public Extraction extract(File packageJsonFile) throws IOException {
        String packageText = null;
        String packagePath = null;
        if (packageJsonFile != null) {
            packageText = FileUtils.readFileToString(packageJsonFile, StandardCharsets.UTF_8);
            packagePath = packageJsonFile.getPath();
        }

        CombinedPackageJsonExtractor extractor = new CombinedPackageJsonExtractor(gson);
        CombinedPackageJson combinedPackageJson = extractor.constructCombinedPackageJson(packagePath, packageText);

        return extract(combinedPackageJson);
    }

    public Extraction extract(CombinedPackageJson combinedPackageJson) {
        List<Dependency> dependencies = transformDependencies(combinedPackageJson.getDependencies());
        npmDependencyTypeFilter.ifShouldInclude(NpmDependencyType.DEV, transformDependencies(combinedPackageJson.getDevDependencies()), dependencies::addAll);
        npmDependencyTypeFilter.ifShouldInclude(NpmDependencyType.PEER, transformDependencies(combinedPackageJson.getPeerDependencies()), dependencies::addAll);

        DependencyGraph dependencyGraph = new BasicDependencyGraph();
        dependencyGraph.addChildrenToRoot(dependencies);

        CodeLocation codeLocation = new CodeLocation(dependencyGraph);

        String projectName = StringUtils.stripToNull(combinedPackageJson.getName());
        String projectVersion = StringUtils.stripToNull(combinedPackageJson.getVersion());

        return new Extraction.Builder()
            .success(codeLocation)
            .projectName(projectName)
            .projectVersion(projectVersion)
            .build();
    }

    private List<Dependency> transformDependencies(MultiValuedMap<String, String> dependencies) {
        if (dependencies == null || dependencies.size() == 0) {
            return new ArrayList<>();
        }
        return dependencies.entries().stream()
            .map(entry -> entryToDependency(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }
    
    private Dependency entryToDependency(String key, String value) {
        // Extract the lowest version from the value
        String version = extractLowestVersion(value);
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, key, version);
        return new Dependency(externalId);
    }

    private String extractLowestVersion(String value) {
        // Split the value into parts by spaces, "||", or "-".
        String[] parts = value.split("\\s+|\\|\\||-");
        String lowestVersion = Arrays.stream(parts)
             // Replace "x" or "*" with "0"
            .map(part -> part.replaceAll("x|\\*", "0"))
            // Remove npm version selection characters that the KB won't match on
            .map(part -> part.replaceAll("[>=<~^]", ""))
            // Filter out parts that don't match the version pattern
            .filter(part -> part.matches("\\d+\\.\\d+\\.\\d+|\\d+\\.\\d+|\\d+"))
            // Use the compareVersions method to find smallest version in each value
            .min(this::compareSemVerVersions)
            // If no part matches the version pattern, return the original value.
            .orElse(value);

        return lowestVersion;
    }
    
    private int compareSemVerVersions(String v1, String v2) {
        // Split each version string into parts
        String[] v1Parts = v1.split("\\.");
        String[] v2Parts = v2.split("\\.");

        // Determine the maximum length to iterate over
        int maxLength = Math.max(v1Parts.length, v2Parts.length);

        // Compare each part until we know which string is smallest
        for (int i = 0; i < maxLength; i++) {
            int part1 = (i < v1Parts.length) ? Integer.parseInt(v1Parts[i]) : 0;
            int part2 = (i < v2Parts.length) ? Integer.parseInt(v2Parts[i]) : 0;

            int comparison = Integer.compare(part1, part2);

            // If the parts are not equal, return the comparison result
            if (comparison != 0) {
                return comparison;
            }
        }

        // If all parts are equal, return 0
        return 0;
    }

}
