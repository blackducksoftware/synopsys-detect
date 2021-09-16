/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmParseResult;

public class NpmCliParser {
    private final Logger logger = LoggerFactory.getLogger(NpmCliParser.class);

    private static final String JSON_NAME = "name";
    private static final String JSON_VERSION = "version";
    private static final String JSON_DEPENDENCIES = "dependencies";

    public final ExternalIdFactory externalIdFactory;

    public NpmCliParser(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public NpmParseResult generateCodeLocation(String npmLsOutput, NpmDependencyTypeFilter npmDependencyTypeFilter) {
        if (StringUtils.isBlank(npmLsOutput)) {
            logger.error("Ran into an issue creating and writing to file");
            return null;
        }

        logger.debug("Generating results from npm ls -json");
        return convertNpmJsonFileToCodeLocation(npmLsOutput, npmDependencyTypeFilter);
    }

    public NpmParseResult convertNpmJsonFileToCodeLocation(String npmLsOutput, NpmDependencyTypeFilter npmDependencyTypeFilter) {
        JsonObject npmJson = JsonParser.parseString(npmLsOutput).getAsJsonObject();
        MutableDependencyGraph graph = new MutableMapDependencyGraph();

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

        populateChildren(graph, null, npmJson.getAsJsonObject(JSON_DEPENDENCIES), true, npmDependencyTypeFilter);

        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, projectName, projectVersion);

        CodeLocation codeLocation = new CodeLocation(graph, externalId);

        return new NpmParseResult(projectName, projectVersion, codeLocation);

    }

    private void populateChildren(MutableDependencyGraph graph, Dependency parentDependency, JsonObject parentNodeChildren, boolean isRootDependency, NpmDependencyTypeFilter npmDependencyTypeFilter) {
        if (parentNodeChildren == null) {
            return;
        }

        Set<Entry<String, JsonElement>> elements = parentNodeChildren.entrySet();
        elements.stream()
            .filter(Objects::nonNull)
            .filter(elementEntry -> elementEntry.getValue().isJsonObject())
            .filter(elementEntry -> npmDependencyTypeFilter.shouldInclude(elementEntry.getKey(), isRootDependency))
            .forEach(elementEntry -> processChild(elementEntry, graph, parentDependency, isRootDependency, npmDependencyTypeFilter));
    }

    private void processChild(Entry<String, JsonElement> elementEntry, MutableDependencyGraph graph, Dependency parentDependency, boolean isRootDependency, NpmDependencyTypeFilter npmDependencyTypeFilter) {
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

            populateChildren(graph, child, children, false, npmDependencyTypeFilter);
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
