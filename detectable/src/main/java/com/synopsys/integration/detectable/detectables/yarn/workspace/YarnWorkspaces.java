package com.synopsys.integration.detectable.detectables.yarn.workspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import java.util.HashMap;
import java.util.Map;

public class YarnWorkspaces {
    public static final String WORKSPACE_VERSION_PREFIX = "workspace:";
    private final Map<String, YarnWorkspace> workspaceMap = new HashMap<>();
    public static final YarnWorkspaces EMPTY = new YarnWorkspaces(new ArrayList<>(0));

    public YarnWorkspaces(Collection<YarnWorkspace> workspaces) {
        for (YarnWorkspace workspace : workspaces) {
            workspaceMap.put(workspace.getName().orElse(""), workspace);
        }
    }

    public Collection<YarnWorkspace> getWorkspaces() {
        return workspaceMap.values();
    }

    public Optional<YarnWorkspace> lookup(YarnLockDependency yarnLockDependency) {
        YarnWorkspace workspace = workspaceMap.get(yarnLockDependency.getName());
        if (workspace != null && workspace.matches(yarnLockDependency)) {
            return Optional.of(workspace);
        }
        return Optional.empty();
    }
    
    public Optional<YarnWorkspace> lookup(String name, String version) {
        YarnWorkspace workspace = workspaceMap.get(name);
        if (workspace != null && workspace.matches(name, version)) {
            return Optional.of(workspace);
        }
        return Optional.empty();
    }
}
