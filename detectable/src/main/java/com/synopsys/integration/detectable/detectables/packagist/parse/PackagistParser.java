package com.synopsys.integration.detectable.detectables.packagist.parse;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.synopsys.integration.bdio.graph.builder.LazyId;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.packagist.PackagistDependencyType;
import com.synopsys.integration.detectable.detectables.packagist.model.PackagistPackage;
import com.synopsys.integration.detectable.detectables.packagist.model.PackagistParseResult;
import com.synopsys.integration.util.NameVersion;

public class PackagistParser {
    private final Logger logger = LoggerFactory.getLogger(PackagistParser.class);

    private final ExternalIdFactory externalIdFactory;
    private final EnumListFilter<PackagistDependencyType> packagistDependencyTypeFilter;

    public PackagistParser(ExternalIdFactory externalIdFactory, EnumListFilter<PackagistDependencyType> packagistDependencyTypeFilter) {
        this.externalIdFactory = externalIdFactory;
        this.packagistDependencyTypeFilter = packagistDependencyTypeFilter;
    }

    // TODO: Why are we dealing with JsonObjects rather than Gson straight to classes? Is this to avoid TypeAdapters? If so... smh JM-01/2022
    public PackagistParseResult getDependencyGraphFromProject(String composerJsonText, String composerLockText) throws MissingExternalIdException {
        LazyExternalIdDependencyGraphBuilder builder = new LazyExternalIdDependencyGraphBuilder();

        JsonObject composerJsonObject = new JsonParser().parse(composerJsonText).getAsJsonObject();
        NameVersion projectNameVersion = parseNameVersionFromJson(composerJsonObject);

        JsonObject composerLockObject = new JsonParser().parse(composerLockText).getAsJsonObject();
        List<PackagistPackage> models = convertJsonToModel(composerLockObject);
        List<NameVersion> rootPackages = parseDependencies(composerJsonObject);

        models.forEach(it -> {
            ExternalId id = externalIdFactory.createNameVersionExternalId(Forge.PACKAGIST, it.getNameVersion().getName(), it.getNameVersion().getVersion());
            LazyId dependencyId = LazyId.fromName(it.getNameVersion().getName());
            builder.setDependencyInfo(dependencyId, it.getNameVersion().getName(), it.getNameVersion().getVersion(), id);
            if (isRootPackage(it.getNameVersion(), rootPackages)) {
                builder.addChildToRoot(dependencyId);
            }
            it.getDependencies().forEach(child -> {
                if (existsInPackages(child, models)) {
                    LazyId childId = LazyId.fromName(child.getName());
                    builder.addChildWithParent(childId, dependencyId);
                } else {
                    logger.warn("Dependency was not found in packages list but found a require that used it: " + child.getName());
                }
            });
        });
        DependencyGraph graph = builder.build();

        CodeLocation codeLocation;
        if (projectNameVersion.getName() == null || projectNameVersion.getVersion() == null) {
            codeLocation = new CodeLocation(graph);
        } else {
            codeLocation = new CodeLocation(graph, externalIdFactory.createNameVersionExternalId(Forge.PACKAGIST, projectNameVersion.getName(), projectNameVersion.getVersion()));
        }
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
        return models.stream().anyMatch(it -> it.getNameVersion().getName().equals(nameVersion.getName()));
    }

    private List<PackagistPackage> convertJsonToModel(JsonObject lockfile) {
        List<PackagistPackage> packages =
            new ArrayList<>(convertJsonToModel(lockfile.get("packages").getAsJsonArray()));
        if (packagistDependencyTypeFilter.shouldInclude(PackagistDependencyType.DEV)) {
            packages.addAll(convertJsonToModel(lockfile.get("packages-dev").getAsJsonArray()));
        }
        return packages;
    }

    private List<PackagistPackage> convertJsonToModel(JsonArray packagesProperty) {
        List<PackagistPackage> packages = new ArrayList<>();
        packagesProperty.forEach(it -> {
            if (it.isJsonObject()) {
                JsonObject itObject = it.getAsJsonObject();
                NameVersion nameVersion = parseNameVersionFromJson(itObject);
                List<NameVersion> dependencies = parseDependencies(itObject);
                packages.add(new PackagistPackage(nameVersion, dependencies));
            }
        });
        return packages;
    }

    private List<NameVersion> parseDependencies(JsonObject packageJson) {
        List<NameVersion> dependencies = new ArrayList<>();

        JsonElement require = packageJson.get("require");
        if (require != null && require.isJsonObject()) {
            dependencies.addAll(parseDependenciesFromRequire(require.getAsJsonObject()));
        }

        if (packagistDependencyTypeFilter.shouldInclude(PackagistDependencyType.DEV)) {
            JsonElement devRequire = packageJson.get("require-dev");
            if (devRequire != null && devRequire.isJsonObject()) {
                dependencies.addAll(parseDependenciesFromRequire(devRequire.getAsJsonObject()));
            }

        }

        return dependencies;
    }

    private List<NameVersion> parseDependenciesFromRequire(JsonObject requireObject) {
        List<NameVersion> dependencies = new ArrayList<>();
        requireObject.entrySet().forEach(it -> {
            if (!"php".equalsIgnoreCase(it.getKey())) {
                NameVersion nameVersion = new NameVersion(it.getKey(), it.getValue().toString());
                dependencies.add(nameVersion);
            }
        });
        return dependencies;
    }

}
