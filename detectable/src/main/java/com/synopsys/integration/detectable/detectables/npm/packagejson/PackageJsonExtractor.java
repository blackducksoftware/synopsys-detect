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

//    private Dependency entryToDependency(String key, String value) {
//        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, key, value);
//        return new Dependency(externalId);
//    }
    
    private Dependency entryToDependency(String key, String value) {
        // Extract the lowest version from the value
        String version = extractLowestVersion(value);
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, key, version);
        return new Dependency(externalId);
    }

    private String extractLowestVersion(String value) {
        // If the value starts with "http", "file", or is "latest", return the value as is.
        if (value.startsWith("http") || value.startsWith("file") || value.equals("latest")) {
            return value;
        }

        // Split the value into parts by spaces, "||", or "-".
        String[] parts = value.split("\\s+|\\|\\||-");
        String lowestVersion = Arrays.stream(parts)
            .map(part -> part.replaceAll("x|\\*", "0")) // Replace "x" or "*" with "0"
            .map(part -> part.replaceAll("[^0-9.]", "")) // Remove all non-digit and non-period characters
            .filter(part -> part.matches("\\d+\\.\\d+\\.\\d+")) // Filter out parts that don't match the version pattern
            // TODO need to read doc, test the below two lines
            .min(Comparator.naturalOrder()) // Find the smallest part
            .orElse(value); // If no part matches the version pattern, return the original value

        return lowestVersion;
    }

}
