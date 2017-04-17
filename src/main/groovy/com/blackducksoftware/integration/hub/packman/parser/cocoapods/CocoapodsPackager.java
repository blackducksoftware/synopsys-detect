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
package com.blackducksoftware.integration.hub.packman.parser.cocoapods;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId;
import com.blackducksoftware.integration.hub.packman.parser.StreamParser;
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.model.PodLock;
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.model.Podfile;
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.model.Podspec;
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.model.WorkspaceProject;
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.parsers.PodLockParser;
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.parsers.PodfileParser;
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.parsers.PodspecParser;
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.parsers.WorkspaceProjectParser;
import com.blackducksoftware.integration.hub.packman.parser.model.Packager;

public class CocoapodsPackager extends Packager {
    public static final String COMMENTS = "#";

    private final InputStream podfileStream;

    private final InputStream podlockStream;

    private final InputStream podspecStream;

    private final InputStream[] projectStreams;

    public CocoapodsPackager(final InputStream podfileStream, final InputStream podlockStream, final InputStream[] projectStreams) {
        this.podfileStream = podfileStream;
        this.podlockStream = podlockStream;
        this.projectStreams = projectStreams;
        this.podspecStream = null;
    }

    public CocoapodsPackager(final InputStream podfileStream, final InputStream podlockStream, final InputStream podspecStream) {
        this.podfileStream = podfileStream;
        this.podlockStream = podlockStream;
        this.podspecStream = podspecStream;
        this.projectStreams = null;
    }

    @Override
    public List<DependencyNode> makeDependencyNodes() {
        final List<DependencyNode> packages = new ArrayList<>();

        final StreamParser<Podfile> podfileParser = new PodfileParser();
        final Podfile podfile = podfileParser.parse(podfileStream);

        final StreamParser<PodLock> podLockParser = new PodLockParser();
        final PodLock podLock = podLockParser.parse(podlockStream);

        final StreamParser<Podspec> podspecParser = new PodspecParser();
        final Podspec podspec = podspecParser.parse(podspecStream);

        final Map<String, String> workspaceProjects = new HashMap<>();

        // Attempt to resolve versions from project parser
        if (projectStreams != null) {
            for (final InputStream projectStream : projectStreams) {
                final StreamParser<WorkspaceProject> workspaceProjectParser = new WorkspaceProjectParser();
                final WorkspaceProject workspaceProject = workspaceProjectParser.parse(projectStream);
                workspaceProjects.put(workspaceProject.name, workspaceProject.version);
            }
        }

        if (podspec != null) {
            for (final DependencyNode target : podfile.targets) {
                workspaceProjects.put(target.name, podspec.version);
            }
        }

        final Map<String, DependencyNode> allPods = getDependencies(podLock);

        for (final DependencyNode target : podfile.targets) {
            final List<DependencyNode> targetDependencies = new ArrayList<>();
            for (final DependencyNode dep : target.children) {
                final DependencyNode finalDependency = allPods.get(dep.name);
                if (finalDependency != null) {
                    targetDependencies.add(finalDependency);
                }
            }
            target.children = targetDependencies;
            final String versionFound = workspaceProjects.get(target.name);
            if (StringUtils.isNotBlank(versionFound)) {
                target.version = versionFound;
            }
            target.externalId = new NameVersionExternalId(Forge.cocoapods, target.name, target.version);
            packages.add(target);
        }

        return packages;
    }

    public Map<String, DependencyNode> getDependencies(final PodLock podLock) {
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
            pod.getValue().children.clear();
            pod.getValue().children.addAll(pod_deps);
        }
        return allPods;
    }

    public static DependencyNode createPodNodeFromGroups(final Matcher regexMatcher, final int nameGroup, final int versionGroup) {
        DependencyNode node = null;
        try {
            final String name = regexMatcher.group(nameGroup).trim();
            final String version = regexMatcher.group(versionGroup).trim();
            final ExternalId externalId = new NameVersionExternalId(Forge.cocoapods, name, version);
            node = new DependencyNode(name, version, externalId, new ArrayList<DependencyNode>());
        } catch (final IllegalStateException e) {
            e.printStackTrace();
        } catch (final IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return node;
    }

}
