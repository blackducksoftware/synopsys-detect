package com.synopsys.integration.detectable.detectables.yarn.workspace;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.builder.LazyId;
import com.synopsys.integration.detectable.detectables.yarn.YarnTransformer;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.WorkspacePackageJson;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntry;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryId;

public class YarnWorkspace {
    private static final String WORKSPACE_VERSION_PREFIX = "workspace:";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final WorkspacePackageJson workspacePackageJson;

    public YarnWorkspace(WorkspacePackageJson workspacePackageJson) {
        this.workspacePackageJson = workspacePackageJson;
    }

    public Optional<String> getName() {
        return workspacePackageJson.getPackageJson().getName();
    }

    public Optional<String> getVersion() {
        return workspacePackageJson.getPackageJson().getVersion();
    }

    public String getVersionString() {
        return workspacePackageJson.getPackageJson().getVersion().orElse("");
    }

    public Map<String, String> getDependencies() {
        return workspacePackageJson.getPackageJson().getDependencies();
    }

    public Map<String, String> getDevDependencies() {
        return workspacePackageJson.getPackageJson().getDevDependencies();
    }

    public WorkspacePackageJson getWorkspacePackageJson() {
        return workspacePackageJson;
    }

    public LazyId generateDependencyId() {
        return LazyId.fromString(
            getName().orElse(null)
                + YarnTransformer.STRING_ID_NAME_VERSION_SEPARATOR
                + WORKSPACE_VERSION_PREFIX
                + workspacePackageJson.getDirRelativePath()
        );
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

    public boolean matches(LazyId givenDependencyId) {
        String thisWorkspaceName = getName().orElse(null);
        String givenDependencyIdString = givenDependencyId.toString();
        if (givenDependencyIdString.startsWith(thisWorkspaceName + YarnTransformer.STRING_ID_NAME_VERSION_SEPARATOR)) {
            LazyId thisWorkspaceDependencyId = generateDependencyId();
            if (!givenDependencyId.equals(thisWorkspaceDependencyId)) {
                logger.warn(
                    "Dependency ID {} looks like workspace {}, but expected the Dependency ID to be {}",
                    givenDependencyId,
                    thisWorkspaceName,
                    thisWorkspaceDependencyId
                );
            }
            return true;
        }
        return false;
    }

    public boolean matches(String name, String version) {
        if (getName().orElse("").equals(name)) {
            if (!version.startsWith(WORKSPACE_VERSION_PREFIX) && !versionMatches(version)) {
                logger.trace(
                    "yarn.lock dependency {} has the same name as a workspace, but the version is {} (vs. {}). Considering them the same anyway.",
                    name,
                    version,
                    getVersionString()
                );
            }
            return true;
        }
        return false;
    }

    public boolean hasDependency(String depName) {
        return getDependencies().containsKey(depName);
    }

    public boolean hasDevDependency(String depName) {
        return getDevDependencies().containsKey(depName);
    }

    private boolean versionMatches(String version) {
        if (StringUtils.isBlank(version) && StringUtils.isBlank(getVersion().orElse(null))) {
            return true;
        }
        return version.equals(getVersion().orElse(null));
    }
}
