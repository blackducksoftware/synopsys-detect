package com.synopsys.integration.detectable.detectables.npm.packagejson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
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

    public Extraction extract(InputStream packageJsonInputStream) {
        Reader packageJsonReader = new InputStreamReader(packageJsonInputStream);
        PackageJson packageJson = gson.fromJson(packageJsonReader, PackageJson.class);

        return extract(packageJson);
    }

    public Extraction extract(PackageJson packageJson) {
        List<Dependency> dependencies = transformDependencies(packageJson.dependencies);
        npmDependencyTypeFilter.ifShouldInclude(NpmDependencyType.DEV, transformDependencies(packageJson.devDependencies), dependencies::addAll);
        npmDependencyTypeFilter.ifShouldInclude(NpmDependencyType.PEER, transformDependencies(packageJson.peerDependencies), dependencies::addAll);

        DependencyGraph dependencyGraph = new BasicDependencyGraph();
        dependencyGraph.addChildrenToRoot(dependencies);

        CodeLocation codeLocation = new CodeLocation(dependencyGraph);

        String projectName = StringUtils.stripToNull(packageJson.name);
        String projectVersion = StringUtils.stripToNull(packageJson.version);

        return new Extraction.Builder()
            .success(codeLocation)
            .projectName(projectName)
            .projectVersion(projectVersion)
            .build();
    }

    private List<Dependency> transformDependencies(Map<String, String> dependencies) {
        return dependencies.entrySet().stream()
            .map(this::entryToDependency)
            .collect(Collectors.toList());
    }

    private Dependency entryToDependency(Map.Entry<String, String> dependencyEntry) {
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, dependencyEntry.getKey(), dependencyEntry.getValue());
        return new Dependency(externalId);
    }

}
