/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn.workspace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependencyid.StringDependencyId;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.WorkspacePackageJson;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntry;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryId;

public class Workspace {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;
    private final WorkspacePackageJson workspacePackageJson;

    public Workspace(/*ExternalIdFactory externalIdFactory, */WorkspacePackageJson workspacePackageJson) {
        // TODO inject this:
        this.externalIdFactory = new ExternalIdFactory();
        this.workspacePackageJson = workspacePackageJson;
    }

    public WorkspacePackageJson getWorkspacePackageJson() {
        return workspacePackageJson;
    }

    public StringDependencyId generateDependencyId() {
        return new StringDependencyId(workspacePackageJson.getPackageJson().name + "@workspace:" + workspacePackageJson.getDirRelativePath());
    }

    public ExternalId generateExternalId() {
        String version = workspacePackageJson.getPackageJson().version;
        // TODO could avoid false positives by changing the Forge here!
        //Forge workspaceMetaForge = Forge.NPMJS;
        // TODO make this a static class field
        Forge workspaceMetaForge = new Forge("/", "detect-yarn-workspace");
        return externalIdFactory.createNameVersionExternalId(workspaceMetaForge, workspacePackageJson.getPackageJson().name, version);
    }

    public boolean matches(YarnLockEntry yarnLockEntry) {
        for (YarnLockEntryId yarnLockEntryId : yarnLockEntry.getIds()) {
            if (workspacePackageJson.getPackageJson().name.equals(yarnLockEntryId.getName())) {
                if (!workspacePackageJson.getPackageJson().version.equals(yarnLockEntryId.getVersion())) {
                    logger.warn("yarn.lock entry ID {} has the same name as a workspace, but the version is {} (vs. {}). Considering them the same anyway.",
                        yarnLockEntryId.getName(), yarnLockEntryId.getVersion(), workspacePackageJson.getPackageJson().version);
                }
                return true;
            }
        }
        return false;
    }

    // TODO this method should use the one below
    public boolean matches(YarnLockDependency yarnLockDependency) {
        if (workspacePackageJson.getPackageJson().name.equals(yarnLockDependency.getName())) {
            if (!workspacePackageJson.getPackageJson().version.equals(yarnLockDependency.getVersion())) {
                logger.warn("yarn.lock dependency {} has the same name as a workspace, but the version is {} (vs. {}). Considering them the same anyway.",
                    yarnLockDependency.getName(), yarnLockDependency.getVersion(), workspacePackageJson.getPackageJson().version);
            }
            return true;
        }
        return false;
    }

    public boolean matches(String name, String version) {
        if (workspacePackageJson.getPackageJson().name.equals(name)) {
            if (!workspacePackageJson.getPackageJson().version.equals(version)) {
                logger.warn("yarn.lock dependency {} has the same name as a workspace, but the version is {} (vs. {}). Considering them the same anyway.",
                    name, version, workspacePackageJson.getPackageJson().version);
            }
            return true;
        }
        return false;
    }
}
