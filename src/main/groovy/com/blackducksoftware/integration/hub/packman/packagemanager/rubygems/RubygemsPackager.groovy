/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.packman.packagemanager.rubygems

import java.util.regex.Matcher
import java.util.regex.Pattern

import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeBuilder
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.packman.type.PackageManagerType
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer

public class RubygemsPackager {
    private final ProjectInfoGatherer projectInfoGatherer

    private final Pattern linePattern = Pattern.compile("(.*) \\((.*)\\)")

    public RubygemsPackager(final ProjectInfoGatherer projectInfoGatherer) {
        this.projectInfoGatherer = projectInfoGatherer
    }

    public List<DependencyNode> makeDependencyNodes(final String sourcePath, final String gemlock) {
        final String rootName = projectInfoGatherer.getDefaultProjectName(PackageManagerType.RUBYGEMS, sourcePath)
        final String rootVersion = projectInfoGatherer.getDefaultProjectVersionName()
        final ExternalId rootExternalId = new NameVersionExternalId(Forge.RUBYGEMS, rootName, rootVersion)
        final DependencyNode root = new DependencyNode(rootName, rootVersion, rootExternalId)

        final DependencyNodeBuilder dependencyNodeBuilder = new DependencyNodeBuilder(root)
        final List<DependencyNode> dependencies = new ArrayList<>()
        dependencies.add(root)

        final GemLockParser gemlockParser = new GemLockParser("  ", ":")
        final Map gemlockMap = gemlockParser.parse(gemlock)
        final Map specMap = gemlockMap.get("GEM").get("specs")
        gemlockMap.get("DEPENDENCIES").each { key, value ->
            final DependencyNode dependencyNode = entryToDependencyNode(specMap, key, value)
            if (dependencyNode) {
                dependencyNodeBuilder.addChildNodeWithParents(dependencyNode, dependencies)
            }
        }
        return dependencies
    }

    public DependencyNode keyToDependencyNode(final String line) {
        final Matcher versionMatcher = linePattern.matcher(line)
        String name
        String version
        if (versionMatcher.matches()) {
            name = versionMatcher.group(1).trim()
            version = versionMatcher.group(2).trim()
        } else {
            name = line.trim()
            version = null
        }
        final ExternalId externalId = new NameVersionExternalId(Forge.RUBYGEMS, name, version)
        final DependencyNode dependencyNode = new DependencyNode(name, version, externalId)
        return dependencyNode
    }

    public DependencyNode entryToDependencyNode(final Map specMap, String key, Map value) {
        final String foundKey = findKeyInMap(key, specMap)
        if(!foundKey) {
            return null
        }

        final DependencyNode dependencyNode = keyToDependencyNode(foundKey)
        specMap.get(foundKey).each { mapKey, mapValue ->
            final DependencyNode transitive = entryToDependencyNode(specMap, mapKey, mapValue)
            if(transitive) {
                dependencyNode.children.add(transitive)
            }
        }
        return dependencyNode
    }

    public String findKeyInMap(final String key, final Map<String, ?> map) {
        final DependencyNode givenNode = keyToDependencyNode(key)
        for (final String currentKey : map.keySet()) {
            final DependencyNode actualNode = keyToDependencyNode(currentKey)
            if (givenNode.name.equals(actualNode.name)) {
                return currentKey
            }
        }
        return null
    }
}
