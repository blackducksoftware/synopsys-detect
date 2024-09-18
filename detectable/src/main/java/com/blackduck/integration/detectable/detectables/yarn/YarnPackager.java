package com.blackduck.integration.detectable.detectables.yarn;

import java.util.List;

import com.blackduck.integration.detectable.detectable.codelocation.CodeLocation;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.blackduck.integration.detectable.detectables.yarn.packagejson.NullSafePackageJson;
import com.blackduck.integration.detectable.detectables.yarn.parse.YarnLock;
import com.blackduck.integration.detectable.detectables.yarn.parse.YarnLockResult;
import com.blackduck.integration.detectable.detectables.yarn.workspace.YarnWorkspaces;
import com.synopsys.integration.util.ExcludedIncludedWildcardFilter;
import com.synopsys.integration.util.NameVersion;

public class YarnPackager {
    private final YarnTransformer yarnTransformer;

    public YarnPackager(YarnTransformer yarnTransformer) {
        this.yarnTransformer = yarnTransformer;
    }

    public YarnResult generateCodeLocation(
        NullSafePackageJson rootPackageJson,
        YarnWorkspaces yarnWorkspaces,
        YarnLock yarnLock,
        List<NameVersion> externalDependencies,
        @Nullable ExcludedIncludedWildcardFilter workspaceFilter
    ) {
        YarnLockResult yarnLockResult = new YarnLockResult(rootPackageJson, yarnWorkspaces, yarnLock);

        try {
            List<CodeLocation> codeLocations = yarnTransformer.generateCodeLocations(yarnLockResult, externalDependencies, workspaceFilter);
            return YarnResult.success(rootPackageJson.getName().orElse(null), rootPackageJson.getVersion().orElse(null), codeLocations);
        } catch (MissingExternalIdException exception) {
            return YarnResult.failure(exception);
        }
    }
    
    public YarnResult generateCodeLocation(
        NullSafePackageJson rootPackageJson,
        YarnLock yarnLock,
        List<NameVersion> externalDependencies
    ) {
        YarnLockResult yarnLockResult = new YarnLockResult(rootPackageJson, yarnLock);

        try {
            List<CodeLocation> codeLocations = yarnTransformer.generateCodeLocations(yarnLockResult, externalDependencies);
            return YarnResult.success(rootPackageJson.getName().orElse(null), rootPackageJson.getVersion().orElse(null), codeLocations);
        } catch (MissingExternalIdException exception) {
            return YarnResult.failure(exception);
        }
    }
}
