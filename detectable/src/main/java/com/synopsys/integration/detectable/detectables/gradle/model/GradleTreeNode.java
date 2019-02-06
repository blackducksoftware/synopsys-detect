package com.synopsys.integration.detectable.detectables.gradle.model;

import java.util.Optional;

public class GradleTreeNode {
    public GradleTreeNode(final NodeType nodeType, int level, final GradleGav gav, final String projectName) {
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
        UNKOWN,
        PROJECT
    }

    private NodeType nodeType;
    private final int level;
    private GradleGav gav;
    private String projectName;

    public static GradleTreeNode newProject(int level) {
        return new GradleTreeNode(NodeType.PROJECT, level, null, "");
    }

    public static GradleTreeNode newGav(int level, String artifact, String version, String name) {
        return new GradleTreeNode(NodeType.GAV, level, new GradleGav(artifact, version, name), null);
    }

    public static GradleTreeNode newUnknown(int level) {
        return new GradleTreeNode(NodeType.UNKOWN, level, null, null);
    }
}
