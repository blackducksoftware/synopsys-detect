/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.NullSafePackageJson;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLock;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockResult;
import com.synopsys.integration.detectable.detectables.yarn.workspace.YarnWorkspaces;
import com.synopsys.integration.util.ExcludedIncludedWildcardFilter;
import com.synopsys.integration.util.NameVersion;

public class YarnPackager {
    private final YarnTransformer yarnTransformer;

    public YarnPackager(YarnTransformer yarnTransformer) {
        this.yarnTransformer = yarnTransformer;
    }

    public YarnResult generateCodeLocation(NullSafePackageJson rootPackageJson, YarnWorkspaces yarnWorkspaces, YarnLock yarnLock, List<NameVersion> externalDependencies,
        boolean useProductionOnly, boolean getWorkspaceDependenciesFromWorkspacePackageJson, @Nullable ExcludedIncludedWildcardFilter workspaceFilter) {
        YarnLockResult yarnLockResult = new YarnLockResult(rootPackageJson, yarnWorkspaces, yarnLock);

        try {
            DependencyGraph dependencyGraph = yarnTransformer.generateDependencyGraph(yarnLockResult, useProductionOnly, getWorkspaceDependenciesFromWorkspacePackageJson, externalDependencies,
                workspaceFilter);
            CodeLocation codeLocation = new CodeLocation(dependencyGraph);

            return YarnResult.success(rootPackageJson.getName().orElse(null), rootPackageJson.getVersion().orElse(null), codeLocation);
        } catch (MissingExternalIdException exception) {
            return YarnResult.failure(exception);
        }
    }
}
