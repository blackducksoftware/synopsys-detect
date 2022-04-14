package com.synopsys.integration.detectable.detectables.npm.cli.parse;

import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.npm.NpmDependencyType;
import com.synopsys.integration.detectable.detectables.npm.lockfile.result.NpmPackagerResult;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;

public class NpmCliParser {
    private final Logger logger = LoggerFactory.getLogger(NpmCliParser.class);

    private static final String JSON_NAME = "name";
    private static final String JSON_VERSION = "version";
    private static final String JSON_DEPENDENCIES = "dependencies";

    private final ExternalIdFactory externalIdFactory;
    private final EnumListFilter<NpmDependencyType> npmDependencyTypeFilter;

    public NpmCliParser(ExternalIdFactory externalIdFactory, EnumListFilter<NpmDependencyType> npmDependencyTypeFilter) {
        this.externalIdFactory = externalIdFactory;
        this.npmDependencyTypeFilter = npmDependencyTypeFilter;
    }

    public NpmPackagerResult generateCodeLocation(String npmLsOutput, PackageJson packageJson) {
        if (StringUtils.isBlank(npmLsOutput)) {
            logger.error("Ran into an issue creating and writing to file");
            return null;
        }

        logger.debug("Generating results from npm ls -json");
        return convertNpmJsonFileToCodeLocation(npmLsOutput, packageJson);
    }

    public NpmPackagerResult convertNpmJsonFileToCodeLocation(String npmLsOutput, PackageJson packageJson) {
        JsonObject npmJson = JsonParser.parseString(npmLsOutput).getAsJsonObject();
        DependencyGraph graph = new BasicDependencyGraph();

        JsonElement projectNameElement = npmJson.getAsJsonPrimitive(JSON_NAME);
        JsonElement projectVersionElement = npmJson.getAsJsonPrimitive(JSON_VERSION);
        String projectName = null;
        String projectVersion = null;
        if (projectNameElement != null) {
            projectName = projectNameElement.getAsString();
        }
        if (projectVersionElement != null) {
            projectVersion = projectVersionElement.getAsString();
        }

        populateChildren(graph, null, npmJson.getAsJsonObject(JSON_DEPENDENCIES), true, packageJson);

        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, projectName, projectVersion);

        CodeLocation codeLocation = new CodeLocation(graph, externalId);

        return new NpmPackagerResult(projectName, projectVersion, codeLocation);

    }

    private void populateChildren(DependencyGraph graph, Dependency parentDependency, JsonObject parentNodeChildren, boolean isRootDependency, PackageJson packageJson) {
        if (parentNodeChildren == null) {
            return;
        }

        Set<Entry<String, JsonElement>> elements = parentNodeChildren.entrySet();
        elements.stream()
            .filter(Objects::nonNull)
            .filter(elementEntry -> elementEntry.getValue().isJsonObject())
            .filter(elementEntry -> {
                if (!isRootDependency) {
                    // Transitives can be both application and dev/peer dependency graphs, but Detect shouldn't be walking a dev or peer dependency tree unless it passed the filter already.
                    return true;
                }
                boolean excludingBecauseDev = (npmDependencyTypeFilter.shouldExclude(NpmDependencyType.DEV, packageJson.devDependencies) && packageJson.devDependencies.containsKey(
                    elementEntry.getKey()));
                boolean excludingBecausePeer = (npmDependencyTypeFilter.shouldExclude(NpmDependencyType.PEER, packageJson.peerDependencies)
                    && packageJson.peerDependencies.containsKey(elementEntry.getKey()));
                return !excludingBecauseDev && !excludingBecausePeer;
            })
            .forEach(elementEntry -> processChild(elementEntry, graph, parentDependency, isRootDependency, packageJson));
    }

    private void processChild(
        Entry<String, JsonElement> elementEntry,
        DependencyGraph graph,
        Dependency parentDependency,
        boolean isRootDependency,
        PackageJson packageJson
    ) {
        JsonObject element = elementEntry.getValue().getAsJsonObject();
        String name = elementEntry.getKey();
        String version = Optional.ofNullable(element.getAsJsonPrimitive(JSON_VERSION))
            .filter(JsonPrimitive::isString)
            .map(JsonPrimitive::getAsString)
            .orElse(null);

        JsonObject children = element.getAsJsonObject(JSON_DEPENDENCIES);

        if (name != null && version != null) {
            ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, name, version);
            Dependency child = new Dependency(name, version, externalId);

            populateChildren(graph, child, children, false, packageJson);
            if (isRootDependency) {
                graph.addChildToRoot(child);
            } else {
                graph.addParentWithChild(parentDependency, child);
            }
        } else {
            logger.trace(String.format("Excluding Json Element missing name or version: { name: %s, version: %s }", name, version));
        }
    }
}
