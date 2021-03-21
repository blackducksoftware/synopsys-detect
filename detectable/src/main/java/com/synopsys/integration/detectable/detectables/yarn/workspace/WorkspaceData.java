/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn.workspace;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.dependencyid.StringDependencyId;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
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

    // TODO use a lambda to make these share code
    public Optional<Workspace> lookup(YarnLockDependency yarnLockDependency) {
        for (Workspace candidateWorkspace : workspacesByName.values()) {
            if (candidateWorkspace.matches(yarnLockDependency)) {
                return Optional.of(candidateWorkspace);
            }
        }
        return Optional.empty();
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

    public Optional<Workspace> lookup(StringDependencyId dependencyId) {
        // TODO not sure this is ideal yet
        for (Map.Entry<String, Workspace> candidateWorkspace : workspacesByName.entrySet()) {
            String dependencyIdString = ((StringDependencyId) dependencyId).getValue();
            if (dependencyIdString.startsWith(candidateWorkspace.getKey() + "@")) {
                // TODO this should happen inside Workspace
                if (!dependencyIdString.equals(candidateWorkspace.getValue().generateDependencyId())) {
                    logger.warn("Dependency ID {} looks like workspace {}, but expected the Dependency ID to be {}",
                        dependencyId, candidateWorkspace.getKey(), candidateWorkspace.getValue().generateDependencyId());
                }
                return Optional.of(candidateWorkspace.getValue());
            }
        }
        return Optional.empty();
    }
}
