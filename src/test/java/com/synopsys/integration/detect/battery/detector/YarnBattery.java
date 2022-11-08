package com.synopsys.integration.detect.battery.detector;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.util.DetectorBatteryTestRunner;

@Tag("battery")
public class YarnBattery {
    @Test
    void lock() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("yarn-lock", "yarn/yarn-lock");
        test.sourceDirectoryNamed("linux-yarn");
        test.sourceFileFromResource("yarn.lock");
        test.sourceFileFromResource("package.json");
        test.git("https://github.com/babel/babel", "master");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void yarn2lock() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("yarn2-lock", "yarn/yarn2-lock");
        test.sourceDirectoryNamed("yarn2-lock");
        test.sourceFileFromResource("yarn.lock");
        test.sourceFileFromResource("package.json");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void yarnWorkspacesAllByDefault() {
        Assumptions.assumeFalse(SystemUtils.IS_OS_WINDOWS);
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("yarn-workspaces-simple", "yarn/yarn-workspaces-simple");
        test.sourceDirectoryNamed("yarn-workspaces-simple");
        test.sourceFileFromResource("yarn.lock");
        test.sourceFileFromResource("package.json");
        test.sourceFileFromResource("mypkgs/workspace-a/package.json");
        test.sourceFileFromResource("mypkgs/workspace-b/package.json");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void yarnWorkspacesAllByFilter() {
        Assumptions.assumeFalse(SystemUtils.IS_OS_WINDOWS);
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("yarn-workspaces-simple-allworkspaces", "yarn/yarn-workspaces-simple-allworkspaces");
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
    void yarnWorkspacesExcludeDev() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("yarn-workspaces-excludedev", "yarn/yarn-workspaces-excludedev");
        test.sourceDirectoryNamed("yarn-workspaces-excludedev");
        test.sourceFileFromResource("yarn.lock");
        test.sourceFileFromResource("package.json");
        test.sourceFileFromResource("workspace-a/package.json");
        test.sourceFileFromResource("workspace-b/package.json");
        test.sourceFileFromResource("workspace-c/package.json");
        test.property("detect.yarn.dependency.types.excluded", "NON_PRODUCTION");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void yarnWorkspacesSimpleSelectWorkspace() {
        Assumptions.assumeFalse(SystemUtils.IS_OS_WINDOWS);
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("yarn-workspaces-simple-selectwksp", "yarn/yarn-workspaces-simple-selectwksp");
        test.sourceDirectoryNamed("yarn-workspaces-simple-selectwksp");
        test.sourceFileFromResource("yarn.lock");
        test.sourceFileFromResource("package.json");
        test.sourceFileFromResource("mypkgs/workspace-a/package.json");
        test.sourceFileFromResource("mypkgs/workspace-b/package.json");
        test.property("detect.yarn.included.workspaces", "mypkgs/workspace-a");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void yarnYarn1WorkspacesAllByFilter() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("yarn1-workspaces", "yarn/yarn1-workspaces");
        test.sourceDirectoryNamed("yarn1-workspaces");
        test.sourceFileFromResource("yarn.lock");
        test.sourceFileFromResource("package.json");
        test.sourceFileFromResource("workspace-a/package.json");
        test.sourceFileFromResource("workspace-b/package.json");
        test.property("detect.yarn.included.workspaces", "workspace-*");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void yarnYarn2UnnamedWorkspaces() {
        Assumptions.assumeFalse(SystemUtils.IS_OS_WINDOWS);
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("yarn2-unnamed-workspaces", "yarn/yarn2-unnamed-workspaces");
        test.sourceDirectoryNamed("yarn2-unnamed-workspaces");
        test.sourceFileFromResource("yarn.lock");
        test.sourceFileFromResource("package.json");
        test.sourceFileFromResource("workspaces/workspace-a/package.json");
        test.sourceFileFromResource("workspaces/workspace-b/package.json");
        test.sourceFileFromResource("workspaces/workspace-c/package.json");
        test.sourceFileFromResource("workspaces/workspace-d/package.json");
        test.property("detect.yarn.included.workspaces", "workspaces/workspace-c,workspaces/workspace-d");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void yarnYarn1SelectWorkspaceWithDevDep() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("yarn1-workspaces-workspacedep", "yarn/yarn1-workspaces-workspacedep");
        test.sourceDirectoryNamed("yarn1-workspaces-workspacedep");
        test.sourceFileFromResource("yarn.lock");
        test.sourceFileFromResource("package.json");
        test.sourceFileFromResource("workspace-a/package.json");
        test.sourceFileFromResource("workspace-b/package.json");
        test.property("detect.yarn.included.workspaces", "w*-a");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void yarnYarn2WorkspacesHierarchy() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("yarn2-workspace-hierarchy", "yarn/yarn2-workspace-hierarchy");
        test.sourceDirectoryNamed("yarn2-workspace-hierarchy");
        test.sourceFileFromResource("yarn.lock");
        test.sourceFileFromResource("package.json");
        test.sourceFileFromResource("workspace-a/package.json");
        test.sourceFileFromResource("workspace-a/child-workspace/package.json");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void yarnYarn2ExcludeNestedWorkspace() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("yarn2-workspace-hierarchy-exclude", "yarn/yarn2-workspace-hierarchy-exclude");
        test.sourceDirectoryNamed("yarn2-workspace-hierarchy-exclude");
        test.sourceFileFromResource("yarn.lock");
        test.sourceFileFromResource("package.json");
        test.sourceFileFromResource("workspace-a/package.json");
        test.sourceFileFromResource("workspace-a/child-workspace/package.json");
        test.property("detect.yarn.excluded.workspaces", "child-workspace");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void yarnYarn2WorkspacesHierarchyMonorepo() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("yarn2-hierarchical-monorepo", "yarn/yarn2-hierarchical-monorepo");
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

    @Test
    void yarnWorkspacesExtensive() {
        Assumptions.assumeFalse(SystemUtils.IS_OS_WINDOWS);
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("yarn-workspaces-berry", "yarn/yarn-workspaces-berry");
        test.sourceDirectoryNamed("yarn-workspaces-berry");
        test.sourceFileFromResource("yarn.lock");
        test.sourceFileFromResource("package.json");
        test.sourceFileFromResource("packages/plugin-npm/package.json");
        test.expectBdioResources();
        test.run();
    }
}

