/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.gradle.inspection.model;

import java.util.Optional;

public class GradleTreeNode {
    public GradleTreeNode(final NodeType nodeType, final int level, final GradleGav gav, final String projectName) {
        this.nodeType = nodeType;
        this.level = level;
        this.gav = gav;
        this.projectName = projectName;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public Optional<GradleGav> getGav() {
        return Optional.ofNullable(gav);
    }

    public Optional<String> getProjectName() {
        return Optional.ofNullable(projectName);
    }

    public int getLevel() {
        return level;
    }

    public enum NodeType {
        GAV,
        UNKNOWN,
        PROJECT
    }

    private final NodeType nodeType;
    private final int level;
    private final GradleGav gav;
    private final String projectName;

    public static GradleTreeNode newProject(final int level) {
        return new GradleTreeNode(NodeType.PROJECT, level, null, "");
    }

    public static GradleTreeNode newGav(final int level, final String name, final String group, final String version) {
        return new GradleTreeNode(NodeType.GAV, level, new GradleGav(name, group, version), null);
    }

    public static GradleTreeNode newUnknown(final int level) {
        return new GradleTreeNode(NodeType.UNKNOWN, level, null, null);
    }
}
