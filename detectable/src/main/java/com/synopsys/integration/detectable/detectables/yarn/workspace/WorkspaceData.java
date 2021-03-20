package com.synopsys.integration.detectable.detectables.yarn.workspace;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.dependencyid.StringDependencyId;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntry;

// TODO name?
public class WorkspaceData {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    // TODO does the map add any value anymore?
    private final Map<String, Workspace> workspacesByName;
    public final static WorkspaceData EMPTY = new WorkspaceData(new HashMap<>());

    public WorkspaceData(Map<String, Workspace> workspacesByName) {
        this.workspacesByName = workspacesByName;
    }

    public Collection<Workspace> getWorkspaces() {
        return workspacesByName.values();
    }

    public Optional<Workspace> lookup(YarnLockEntry yarnLockEntry) {
        for (Workspace candidateWorkspace : workspacesByName.values()) {
            if (candidateWorkspace.matches(yarnLockEntry)) {
                return Optional.of(candidateWorkspace);
            }
        }
        return Optional.empty();
    }

    public Optional<Workspace> lookup(String name, String version) {
        for (Workspace candidateWorkspace : workspacesByName.values()) {
            if (candidateWorkspace.matches(name, version)) {
                return Optional.of(candidateWorkspace);
            }
        }
        return Optional.empty();
    }

    public boolean isWorkspace(StringDependencyId dependencyId) {
        // TODO not sure this is ideal yet
        for (Map.Entry<String, Workspace> candidateWorkspace : workspacesByName.entrySet()) {
            String dependencyIdString = ((StringDependencyId) dependencyId).getValue();
            if (dependencyIdString.startsWith(candidateWorkspace.getKey() + "@")) {
                if (!dependencyIdString.equals(candidateWorkspace.getValue().generateDependencyId())) {
                    logger.warn("Dependency ID {} looks like workspace {}, but expected the Dependency ID to be {}",
                        dependencyId, candidateWorkspace.getKey(), candidateWorkspace.getValue().generateDependencyId());
                }
                return true;
            }
        }
        return false;
    }
}
