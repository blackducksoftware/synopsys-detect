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

public class YarnWorkspaces {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    // TODO does the map add any value anymore?
    private final Map<String, YarnWorkspace> workspacesByName;
    public final static YarnWorkspaces EMPTY = new YarnWorkspaces(new HashMap<>());

    public YarnWorkspaces(Map<String, YarnWorkspace> workspacesByName) {
        this.workspacesByName = workspacesByName;
    }

    public Collection<YarnWorkspace> getWorkspaces() {
        return workspacesByName.values();
    }

    // TODO use a lambda to make these share code
    public Optional<YarnWorkspace> lookup(YarnLockDependency yarnLockDependency) {
        for (YarnWorkspace candidateWorkspace : workspacesByName.values()) {
            if (candidateWorkspace.matches(yarnLockDependency)) {
                return Optional.of(candidateWorkspace);
            }
        }
        return Optional.empty();
    }

    public Optional<YarnWorkspace> lookup(YarnLockEntry yarnLockEntry) {
        for (YarnWorkspace candidateWorkspace : workspacesByName.values()) {
            if (candidateWorkspace.matches(yarnLockEntry)) {
                return Optional.of(candidateWorkspace);
            }
        }
        return Optional.empty();
    }

    public Optional<YarnWorkspace> lookup(String name, String version) {
        for (YarnWorkspace candidateWorkspace : workspacesByName.values()) {
            if (candidateWorkspace.matches(name, version)) {
                return Optional.of(candidateWorkspace);
            }
        }
        return Optional.empty();
    }

    public Optional<YarnWorkspace> lookup(StringDependencyId dependencyId) {
        for (Map.Entry<String, YarnWorkspace> candidateWorkspace : workspacesByName.entrySet()) {
            String dependencyIdString = dependencyId.getValue();
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
