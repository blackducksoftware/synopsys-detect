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

import com.synopsys.integration.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependencyid.StringDependencyId;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.yarn.YarnTransformer;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.WorkspacePackageJson;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntry;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryId;

public class YarnWorkspace {
    private static final String WORKSPACE_VERSION_PREFIX = "workspace:";
    private static final Forge WORKSPACE_FORGE = new Forge("/", "detect-yarn-workspace");
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;
    private final WorkspacePackageJson packageJson;

    public YarnWorkspace(ExternalIdFactory externalIdFactory, WorkspacePackageJson packageJson) {
        this.externalIdFactory = externalIdFactory;
        this.packageJson = packageJson;
    }

    public WorkspacePackageJson getPackageJson() {
        return packageJson;
    }

    public StringDependencyId generateDependencyId() {
        return new StringDependencyId(packageJson.getPackageJson().name + YarnTransformer.STRING_ID_NAME_VERSION_SEPARATOR + WORKSPACE_VERSION_PREFIX + packageJson.getDirRelativePath());
    }

    public ExternalId generateExternalId() {
        String version = packageJson.getPackageJson().version;
        return externalIdFactory.createNameVersionExternalId(WORKSPACE_FORGE, packageJson.getPackageJson().name, version);
    }

    public boolean matches(YarnLockEntry yarnLockEntry) {
        for (YarnLockEntryId yarnLockEntryId : yarnLockEntry.getIds()) {
            if (matches(yarnLockEntryId.getName(), yarnLockEntryId.getVersion())) {
                return true;
            }
        }
        return false;
    }

    public boolean matches(YarnLockDependency yarnLockDependency) {
        return matches(yarnLockDependency.getName(), yarnLockDependency.getVersion());
    }

    public boolean matches(StringDependencyId givenDependencyId) {
        String thisWorkspaceName = packageJson.getPackageJson().name;
        String givenDependencyIdString = givenDependencyId.getValue();
        if (givenDependencyIdString.startsWith(thisWorkspaceName + YarnTransformer.STRING_ID_NAME_VERSION_SEPARATOR)) {
            StringDependencyId thisWorkspaceDependencyId = generateDependencyId();
            if (!givenDependencyId.equals(thisWorkspaceDependencyId)) {
                logger.warn("Dependency ID {} looks like workspace {}, but expected the Dependency ID to be {}",
                    givenDependencyId, thisWorkspaceName, thisWorkspaceDependencyId);
            }
            return true;
        }
        return false;
    }

    public boolean matches(String name, String version) {
        if (packageJson.getPackageJson().name.equals(name)) {
            if (!packageJson.getPackageJson().version.equals(version)) {
                logger.warn("yarn.lock dependency {} has the same name as a workspace, but the version is {} (vs. {}). Considering them the same anyway.",
                    name, version, packageJson.getPackageJson().version);
            }
            return true;
        }
        return false;
    }

    public StringDependencyId createDependency(LazyExternalIdDependencyGraphBuilder graphBuilder) {
        StringDependencyId id = generateDependencyId();
        graphBuilder.setDependencyInfo(id, getPackageJson().getPackageJson().name, getPackageJson().getPackageJson().version, generateExternalId());
        return id;
    }
}
