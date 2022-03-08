package com.synopsys.integration.detectable.detectables.yarn.workspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

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

    public Optional<YarnWorkspace> lookup(YarnLockDependency yarnLockDependency) {
        return lookup(w -> w.matches(yarnLockDependency));
    }

    public Optional<YarnWorkspace> lookup(YarnLockEntry yarnLockEntry) {
        return lookup(w -> w.matches(yarnLockEntry));
    }

    public Optional<YarnWorkspace> lookup(String name, String version) {
        return lookup(w -> w.matches(name, version));
    }

    private Optional<YarnWorkspace> lookup(Predicate<YarnWorkspace> p) {
        for (YarnWorkspace candidateWorkspace : workspaces) {
            if (p.test(candidateWorkspace)) {
                return Optional.of(candidateWorkspace);
            }
        }
        return Optional.empty();
    }
}
