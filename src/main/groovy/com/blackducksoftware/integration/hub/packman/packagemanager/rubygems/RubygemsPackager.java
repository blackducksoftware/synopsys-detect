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
package com.blackducksoftware.integration.hub.packman.packagemanager.rubygems;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeBuilder;
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId;
import com.blackducksoftware.integration.hub.packman.Packager;
import com.blackducksoftware.integration.hub.packman.packagemanager.rubygems.parsers.ParserMap;
import com.blackducksoftware.integration.hub.packman.packagemanager.rubygems.parsers.SimpleParser;
import com.google.gson.GsonBuilder;

public class RubygemsPackager extends Packager {
    private final String gemlock;

    public RubygemsPackager(final String gemlock) {
        this.gemlock = gemlock;
    }

    @Override
    public List<DependencyNode> makeDependencyNodes() {
        final String rootName = "TestName";
        final String rootVersion = DateTime.now().toString("MM-dd-YYYY_HH:mm:Z");
        final ExternalId rootExternalId = new NameVersionExternalId(Forge.rubygems, rootName, rootVersion);
        final DependencyNode root = new DependencyNode(rootName, rootVersion, rootExternalId);

        final DependencyNodeBuilder dependencyNodeBuilder = new DependencyNodeBuilder(root);
        final List<DependencyNode> dependencies = new ArrayList<>();
        dependencies.add(root);

        final SimpleParser gemlockParser = new SimpleParser("  ", ":");
        final ParserMap gemlockMap = gemlockParser.parse(gemlock);
        final String mapS = new GsonBuilder().setPrettyPrinting().create().toJson(gemlockMap.get("DEPENDENCIES"));
        final ParserMap specMap = gemlockMap.get("GEM").get("specs");
        gemlockMap.get("DEPENDENCIES").entrySet().forEach(dependencyEntry -> {
            final DependencyNode dependencyNode = entryToDependencyNode(specMap, dependencyEntry);
            if (dependencyNode != null) {
                dependencyNodeBuilder.addChildNodeWithParents(dependencyNode, dependencies);
            }
        });
        return dependencies;
    }

    private DependencyNode keyToDependencyNode(final String line) {
        final Matcher versionMatcher = Pattern.compile("(.*) \\((.*)\\)").matcher(line);
        String name;
        String version;
        if (versionMatcher.matches()) {
            name = versionMatcher.group(1).trim();
            version = versionMatcher.group(2).trim();
        } else {
            name = line.trim();
            version = null;
        }
        final ExternalId externalId = new NameVersionExternalId(Forge.rubygems, name, version);
        final DependencyNode dependencyNode = new DependencyNode(name, version, externalId);
        return dependencyNode;
    }

    private DependencyNode entryToDependencyNode(final ParserMap specMap, final Entry<String, ParserMap> entry) {
        final String foundKey = findKeyInMap(entry.getKey(), specMap);
        final DependencyNode dependencyNode = keyToDependencyNode(foundKey);
        specMap.get(foundKey).entrySet().forEach(dependencyEntry -> {
            final DependencyNode transitive = entryToDependencyNode(specMap, dependencyEntry);
            dependencyNode.children.add(transitive);
        });
        return dependencyNode;
    }

    private String findKeyInMap(final String key, final Map<String, ?> map) {
        final DependencyNode givenNode = keyToDependencyNode(key);
        for (final String currentKey : map.keySet()) {
            final DependencyNode actualNode = keyToDependencyNode(currentKey);
            if (givenNode.name.equals(actualNode.name)) {
                return currentKey;
            }
        }
        return null;
    }
}
