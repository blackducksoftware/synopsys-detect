/**
 * detectable
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
