/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn.workspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import com.synopsys.integration.bdio.model.dependencyid.StringDependencyId;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntry;

public class YarnWorkspaces {
    private final Collection<YarnWorkspace> workspaces;
    public static final YarnWorkspaces EMPTY = new YarnWorkspaces(new ArrayList<>(0));

    public YarnWorkspaces(Collection<YarnWorkspace> workspaces) {
        this.workspaces = workspaces;
    }

    public Collection<YarnWorkspace> getWorkspaces() {
        return workspaces;
    }

    // TODO use a lambda to make these share code
    public Optional<YarnWorkspace> lookup(YarnLockDependency yarnLockDependency) {
        for (YarnWorkspace candidateWorkspace : workspaces) {
            if (candidateWorkspace.matches(yarnLockDependency)) {
                return Optional.of(candidateWorkspace);
            }
        }
        return Optional.empty();
    }

    public Optional<YarnWorkspace> lookup(YarnLockEntry yarnLockEntry) {
        for (YarnWorkspace candidateWorkspace : workspaces) {
            if (candidateWorkspace.matches(yarnLockEntry)) {
                return Optional.of(candidateWorkspace);
            }
        }
        return Optional.empty();
    }

    public Optional<YarnWorkspace> lookup(String name, String version) {
        for (YarnWorkspace candidateWorkspace : workspaces) {
            if (candidateWorkspace.matches(name, version)) {
                return Optional.of(candidateWorkspace);
            }
        }
        return Optional.empty();
    }

    public Optional<YarnWorkspace> lookup(StringDependencyId dependencyId) {
        for (YarnWorkspace candidateWorkspace : workspaces) {
            if (candidateWorkspace.matches(dependencyId)) {
                return Optional.of(candidateWorkspace);
            }
        }
        return Optional.empty();
    }
}
