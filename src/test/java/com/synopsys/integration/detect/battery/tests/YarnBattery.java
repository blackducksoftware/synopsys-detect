/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;

@Tag("battery")
public class YarnBattery {
    @Test
    void lock() {
        BatteryTest test = new BatteryTest("yarn-lock", "yarn/yarn-lock");
        test.sourceDirectoryNamed("linux-yarn");
        test.sourceFileFromResource("yarn.lock");
        test.sourceFileFromResource("package.json");
        test.git("https://github.com/babel/babel", "master");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void yarn2lock() {
        BatteryTest test = new BatteryTest("yarn2-lock", "yarn/yarn2-lock");
        test.sourceDirectoryNamed("yarn2-lock");
        test.sourceFileFromResource("yarn.lock");
        test.sourceFileFromResource("package.json");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void yarnWorkspacesSimple() {
        BatteryTest test = new BatteryTest("yarn-workspaces-simple", "yarn/yarn-workspaces-simple");
        test.sourceDirectoryNamed("yarn-workspaces-simple");
        test.sourceFileFromResource("yarn.lock");
        test.sourceFileFromResource("package.json");
        test.sourceFileFromResource("mypkgs/workspace-a/package.json");
        test.sourceFileFromResource("mypkgs/workspace-b/package.json");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void yarnWorkspacesSimpleIncludingAllWorkspaces() {
        BatteryTest test = new BatteryTest("yarn-workspaces-simple-allworkspaces", "yarn/yarn-workspaces-simple-allworkspaces");
        test.sourceDirectoryNamed("yarn-workspaces-simple-allworkspaces");
        test.sourceFileFromResource("yarn.lock");
        test.sourceFileFromResource("package.json");
        test.sourceFileFromResource("mypkgs/workspace-a/package.json");
        test.sourceFileFromResource("mypkgs/workspace-b/package.json");
        test.property("detect.yarn.included.workspaces", "*");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void yarnWorkspacesSimpleSelectWorkspace() {
        BatteryTest test = new BatteryTest("yarn-workspaces-simple-selectwksp", "yarn/yarn-workspaces-simple-selectwksp");
        test.sourceDirectoryNamed("yarn-workspaces-simple-selectwksp");
        test.sourceFileFromResource("yarn.lock");
        test.sourceFileFromResource("package.json");
        test.sourceFileFromResource("mypkgs/workspace-a/package.json");
        test.sourceFileFromResource("mypkgs/workspace-b/package.json");
        test.property("detect.yarn.included.workspaces", "workspace-a");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void yarnWorkspacesExtensive() {
        BatteryTest test = new BatteryTest("yarn-workspaces-berry", "yarn/yarn-workspaces-berry");
        test.sourceDirectoryNamed("yarn-workspaces-berry");
        test.sourceFileFromResource("yarn.lock");
        test.sourceFileFromResource("package.json");
        test.sourceFileFromResource("packages/plugin-npm/package.json");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void yarnYarn1WorkspacesAddAll() {
        BatteryTest test = new BatteryTest("yarn1-workspaces", "yarn/yarn1-workspaces");
        test.sourceDirectoryNamed("yarn1-workspaces");
        test.sourceFileFromResource("yarn.lock");
        test.sourceFileFromResource("package.json");
        test.sourceFileFromResource("workspace-a/package.json");
        test.sourceFileFromResource("workspace-b/package.json");
        test.property("detect.yarn.included.workspaces", "*");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void yarnYarn1WorkspacesWithWorkspaceDep() {
        BatteryTest test = new BatteryTest("yarn1-workspaces-workspacedep", "yarn/yarn1-workspaces-workspacedep");
        test.sourceDirectoryNamed("yarn1-workspaces-workspacedep");
        test.sourceFileFromResource("yarn.lock");
        test.sourceFileFromResource("package.json");
        test.sourceFileFromResource("workspace-a/package.json");
        test.sourceFileFromResource("workspace-b/package.json");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void yarnYarn2WorkspacesHierarchy() {
        BatteryTest test = new BatteryTest("yarn2-workspace-hierarchy", "yarn/yarn2-workspace-hierarchy");
        test.sourceDirectoryNamed("yarn2-workspace-hierarchy");
        test.sourceFileFromResource("yarn.lock");
        test.sourceFileFromResource("package.json");
        test.sourceFileFromResource("workspace-a/package.json");
        test.sourceFileFromResource("workspace-a/child-workspace/package.json");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void yarnYarn2WorkspacesHierarchyMonorepo() {
        BatteryTest test = new BatteryTest("yarn2-hierarchical-monorepo", "yarn/yarn2-hierarchical-monorepo");
        test.sourceDirectoryNamed("yarn2-hierarchical-monorepo");
        test.sourceFileFromResource("yarn.lock");
        test.sourceFileFromResource("package.json");
        test.sourceFileFromResource("workspace-a/package.json");
        test.sourceFileFromResource("workspace-a/child-workspace/package.json");
        test.sourceFileFromResource("nondep-workspace/package.json");
        test.property("detect.yarn.included.workspaces", "nondep-work*");
        test.expectBdioResources();
        test.run();
    }
}

