package com.synopsys.integration.detectable.detectables.pnpm.cli;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.npm.cli.parse.NpmCliParser;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmParseResult;

public class PnpmCliParser {
    private final Logger logger = LoggerFactory.getLogger(NpmCliParser.class);

    private static final String JSON_NAME = "name";
    private static final String JSON_VERSION = "version";
    private static final String JSON_DEPENDENCIES = "dependencies";
    private static final String JSON_DEV_DEPENDENCIES = "devDependencies";

    public final ExternalIdFactory externalIdFactory;

    public PnpmCliParser(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public NpmParseResult generateCodeLocation(String npmLsOutput, boolean includeDevDependencies) {
        if (StringUtils.isBlank(npmLsOutput)) {
            logger.error("Ran into an issue creating and writing to file");
            return null;
        }

        logger.debug("Generating results from pnpm ls -json");
        return convertNpmJsonFileToCodeLocation(npmLsOutput, includeDevDependencies);
    }

    public NpmParseResult convertNpmJsonFileToCodeLocation(String npmLsOutput, boolean includeDevDependencies) {
        JsonArray pnpmListArray = JsonParser.parseString(npmLsOutput)
                                      .getAsJsonArray(); // pnpm wraps list output in an array TODO- does this have something to do with workspaces/there monorepo support?  shoudl there be a code location for each element in the array?
        JsonObject npmJson = pnpmListArray.get(0).getAsJsonObject();
        MutableDependencyGraph graph = new MutableMapDependencyGraph();

        JsonElement projectNameElement = npmJson.getAsJsonPrimitive(JSON_NAME);
        JsonElement projectVersionElement = npmJson.getAsJsonPrimitive(JSON_VERSION);
        String projectName = projectNameElement != null ? projectNameElement.getAsString() : null;
        String projectVersion = projectNameElement != null ? projectVersionElement.getAsString() : null;

        populateChildren(graph, null, npmJson.getAsJsonObject(JSON_DEPENDENCIES), true);
        if (includeDevDependencies) {
            populateChildren(graph, null, npmJson.getAsJsonObject(JSON_DEV_DEPENDENCIES), true);
        }

        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, projectName, projectVersion);

        CodeLocation codeLocation = new CodeLocation(graph, externalId);

        return new NpmParseResult(projectName, projectVersion, codeLocation);

    }

    private void populateChildren(MutableDependencyGraph graph, Dependency parentDependency, JsonObject parentNodeChildren, boolean isRootDependency) {
        if (parentNodeChildren == null) {
            return;
        }

        Set<Map.Entry<String, JsonElement>> elements = parentNodeChildren.entrySet();
        elements.stream()
            .filter(Objects::nonNull)
            .filter(elementEntry -> elementEntry.getValue().isJsonObject())
            .forEach(elementEntry -> processChild(elementEntry, graph, parentDependency, isRootDependency));
    }

    private void processChild(Map.Entry<String, JsonElement> elementEntry, MutableDependencyGraph graph, Dependency parentDependency, boolean isRootDependency) {
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

            populateChildren(graph, child, children, false);
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
