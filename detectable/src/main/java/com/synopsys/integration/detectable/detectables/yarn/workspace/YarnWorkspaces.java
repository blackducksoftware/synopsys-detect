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
        workspaces.forEach(workspace -> {
            workspaceMap.put(workspace.getName().orElse(""), workspace);
        });
    }

    public Collection<YarnWorkspace> getWorkspaces() {
        return workspaceMap.values();
    }

    public Optional<YarnWorkspace> lookup(YarnLockDependency yarnLockDependency) {
        return lookup(yarnLockDependency.getName(), yarnLockDependency.getVersion());
    }
    
    public Optional<YarnWorkspace> lookup(String name, String version) {
        YarnWorkspace workspace = workspaceMap.get(name);
        if (workspace != null && workspace.matches(name, version)) {
            return Optional.of(workspace);
        }
        return Optional.empty();
    }
}
