package com.synopsys.integration.detectable.detectables.yarn.parse.entry;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;

public class YarnLockEntryBuilder {
    private final List<YarnLockEntryId> ids = new LinkedList<>();
    private String version;
    private final List<YarnLockDependency> dependencies = new LinkedList<>();

    public YarnLockEntryBuilder addId(YarnLockEntryId id) {
        ids.add(id);
        return this;
    }

    public YarnLockEntryBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    public YarnLockEntryBuilder addDependency(YarnLockDependency dependency) {
        dependencies.add(dependency);
        return this;
    }

    public Optional<YarnLockEntry> build() {
        if (ids.isEmpty() || version == null) {
            return Optional.empty();
        }
        return Optional.of(new YarnLockEntry(ids, version, dependencies));
    }
}
