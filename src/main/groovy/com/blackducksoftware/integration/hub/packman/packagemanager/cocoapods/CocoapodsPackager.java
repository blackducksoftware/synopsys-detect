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
package com.blackducksoftware.integration.hub.packman.packagemanager.cocoapods;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId;
import com.blackducksoftware.integration.hub.packman.Packager;
import com.blackducksoftware.integration.hub.packman.packagemanager.cocoapods.model.PodLock;
import com.blackducksoftware.integration.hub.packman.packagemanager.cocoapods.model.Podspec;
import com.blackducksoftware.integration.hub.packman.packagemanager.cocoapods.parsers.PodLockParser;
import com.blackducksoftware.integration.hub.packman.packagemanager.cocoapods.parsers.PodspecParser;
import com.blackducksoftware.integration.hub.packman.util.InputStreamConverter;
import com.blackducksoftware.integration.hub.packman.util.OutputCleaner;

public class CocoapodsPackager extends Packager {
    public static final String COMMENTS = "#";

    private final InputStreamConverter inputStreamConverter;

    private final OutputCleaner outputCleaner;

    private final InputStream podlockStream;

    private final InputStream podspecStream;

    private final String potentialProjectName;

    public CocoapodsPackager(final InputStreamConverter inputStreamConverter, final OutputCleaner outputCleaner, final InputStream podlockStream,
            final InputStream podspecStream, final String potentialProjectName) {
        this.inputStreamConverter = inputStreamConverter;
        this.outputCleaner = outputCleaner;
        this.podlockStream = podlockStream;
        this.podspecStream = podspecStream;
        this.potentialProjectName = potentialProjectName;
    }

    @Override
    public List<DependencyNode> makeDependencyNodes() throws IOException, NullPointerException {
        DependencyNode project = null;

        final PodLockParser podLockParser = new PodLockParser();
        final String podLockText = IOUtils.toString(podlockStream, StandardCharsets.UTF_8);
        final PodLock podLock = podLockParser.parse(podLockText);

        final PodspecParser podspecParser = new PodspecParser(outputCleaner);
        if (podspecStream != null) {
            final BufferedReader podspecBufferedReader = inputStreamConverter.convertToBufferedReader(podspecStream);
            final Podspec podspec = podspecParser.parse(podspecBufferedReader);
            final ExternalId externalId = new NameVersionExternalId(Forge.cocoapods, podspec.name, podspec.version);
            project = new DependencyNode(podspec.name, podspec.version, externalId);
        } else {
            final String name = potentialProjectName;
            final String version = DateTime.now().toString("MM-dd-YYYY_HH:mm:Z");
            final ExternalId externalId = new NameVersionExternalId(Forge.cocoapods, name, version);
            project = new DependencyNode(name, version, externalId);
        }

        final Map<String, DependencyNode> allDependencies = getDependencies(podLock);
        for (final DependencyNode dependency : podLock.dependencies) {
            final DependencyNode dependencyNode = allDependencies.get(dependency.name);
            project.children.add(dependencyNode);
        }

        final List<DependencyNode> dependencyNodes = new ArrayList<>();
        dependencyNodes.add(project);
        return dependencyNodes;
    }

    private Map<String, DependencyNode> getDependencies(final PodLock podLock) {
        final Map<String, DependencyNode> allPods = new HashMap<>();
        for (final DependencyNode pod : podLock.pods) {
            allPods.put(pod.name, pod);
        }

        // Fix pods dependencies
        for (final Entry<String, DependencyNode> pod : allPods.entrySet()) {
            final List<DependencyNode> pod_deps = new ArrayList<>();
            for (final DependencyNode dependency : pod.getValue().children) {
                pod_deps.add(allPods.get(dependency.name));
            }
            pod.getValue().children = pod_deps;
        }
        return allPods;
    }

    public static DependencyNode createPodNodeFromGroups(final Matcher regexMatcher, final int nameGroup, final int versionGroup) {
        DependencyNode node = null;
        if (regexMatcher.matches()) {
            final String name = regexMatcher.group(nameGroup).trim();
            final String version = regexMatcher.group(versionGroup).trim();
            final ExternalId externalId = new NameVersionExternalId(Forge.cocoapods, name, version);
            node = new DependencyNode(name, version, externalId, new ArrayList<DependencyNode>());
        }
        return node;
    }

}
