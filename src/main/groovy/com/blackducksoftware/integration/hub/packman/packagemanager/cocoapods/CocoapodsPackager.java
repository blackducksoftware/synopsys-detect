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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId;
import com.blackducksoftware.integration.hub.packman.PackageManagerType;
import com.blackducksoftware.integration.hub.packman.Packager;
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer;

public class CocoapodsPackager extends Packager {
    public static final String COMMENTS = "#";

    private final InputStream podlockStream;

    private final ProjectInfoGatherer projectInfoGatherer;

    private final String sourcePath;

    public CocoapodsPackager(final ProjectInfoGatherer projectInfoGatherer, final InputStream podlockStream, final String sourcePath) {
        this.podlockStream = podlockStream;
        this.projectInfoGatherer = projectInfoGatherer;
        this.sourcePath = sourcePath;
    }

    @Override
    public List<DependencyNode> makeDependencyNodes() throws IOException, NullPointerException {
        DependencyNode project = null;

        final PodLockParser podLockParser = new PodLockParser();
        final String podLockText = IOUtils.toString(podlockStream, StandardCharsets.UTF_8.name());
        final PodLock podLock = podLockParser.parse(podLockText);

        final String name = projectInfoGatherer.getProjectName(PackageManagerType.COCOAPODS, sourcePath);
        final String version = projectInfoGatherer.getProjectVersion();
        final ExternalId externalId = new NameVersionExternalId(Forge.cocoapods, name, version);
        project = new DependencyNode(name, version, externalId);

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
            final Set<DependencyNode> pod_deps = new HashSet<>();
            for (final DependencyNode dependency : pod.getValue().children) {
                pod_deps.add(allPods.get(dependency.name));
            }
            pod.getValue().children = pod_deps;
        }
        return allPods;
    }
}
