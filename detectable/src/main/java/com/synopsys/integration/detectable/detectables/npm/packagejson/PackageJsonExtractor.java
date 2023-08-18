package com.synopsys.integration.detectable.detectables.npm.packagejson;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
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
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, key, value);
        return new Dependency(externalId);
    }

}
