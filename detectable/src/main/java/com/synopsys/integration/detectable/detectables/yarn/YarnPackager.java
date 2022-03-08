package com.synopsys.integration.detectable.detectables.yarn;

import java.util.List;

import org.jetbrains.annotations.Nullable;

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
}
