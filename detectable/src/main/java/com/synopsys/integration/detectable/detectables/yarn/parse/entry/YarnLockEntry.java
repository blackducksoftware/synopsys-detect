package com.synopsys.integration.detectable.detectables.yarn.parse.entry;

import java.util.List;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.detectable.detectables.yarn.workspace.YarnWorkspaces;
import com.synopsys.integration.util.Stringable;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YarnLockEntry extends Stringable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final boolean metadataEntry;
    private final List<YarnLockEntryId> ids;
    private final Map<String, YarnLockEntryId> idMap;
    private final String version;
    private final List<YarnLockDependency> dependencies;

    public YarnLockEntry(boolean metadataEntry, List<YarnLockEntryId> ids, Map<String, YarnLockEntryId> idMap, String version, List<YarnLockDependency> dependencies) {
        this.metadataEntry = metadataEntry;
        this.ids = ids;
        this.idMap = idMap;
        this.version = version;
        this.dependencies = dependencies;
    }

    public boolean isMetadataEntry() {
        return metadataEntry;
    }
    
    private boolean idsStoredAsList() {
        return idMap.isEmpty();
    }

    public List<YarnLockEntryId> getIds() {
        if (idsStoredAsList()) {
            return ids;
        } else {
            return idMap.values().stream().collect(Collectors.toCollection(ArrayList::new));
        }
    }
    
    public boolean matches(String workspaceName, String workspaceVersion) {
        if (idsStoredAsList()) {
            for (YarnLockEntryId id : ids) {
                if (matchWithAnId(id, workspaceName, workspaceVersion)) {
                    return true;
                }
            }
        } else {
            YarnLockEntryId id = idMap.get(workspaceName);
            if (id != null) {
                return matchWithAnId(id, workspaceName, workspaceVersion);
            }
        }
        return false;
    }
    
    private boolean matchWithAnId(YarnLockEntryId id, String workspaceName, String workspaceVersion) {
        if (workspaceName.equals(id.getName())) {
            if (!workspaceName.startsWith(YarnWorkspaces.WORKSPACE_VERSION_PREFIX) && !versionMatches(workspaceVersion)) {
                logger.trace(
                    "yarn.lock dependency {} has the same name as a workspace, but the version is {} (vs. {}). Considering them the same anyway.",
                    id.getName(),
                    workspaceName,
                    id.getVersion()
                );
            }
            return true;
        }
        return false;
    }
    
    private boolean versionMatches(String workspaceVersion) {
        if (StringUtils.isBlank(version) && StringUtils.isBlank(workspaceVersion)) {
            return true;
        }
        return version.equals(workspaceVersion);
    }

    public List<YarnLockDependency> getDependencies() {
        return dependencies;
    }

    public String getVersion() {
        return version;
    }
}
