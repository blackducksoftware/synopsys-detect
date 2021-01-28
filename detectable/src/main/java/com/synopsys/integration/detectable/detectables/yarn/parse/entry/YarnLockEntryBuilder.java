package com.synopsys.integration.detectable.detectables.yarn.parse.entry;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;

public class YarnLockEntryBuilder {
    private final List<YarnLockEntryId> ids = new LinkedList<>();
    private String version;
    private final Map<String, YarnLockDependency> dependencies = new HashMap<>();

    public YarnLockEntryBuilder addId(YarnLockEntryId id) {
        ids.add(id);
        return this;
    }

    public YarnLockEntryBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    public YarnLockEntryBuilder addDependency(YarnLockDependency dependency) {
        dependencies.put(dependency.getName(), dependency);
        return this;
    }

    public Map<String, YarnLockDependency> getDependencies() {
        return dependencies;
    }

    public Optional<YarnLockEntry> build() {
        if (ids.isEmpty() || version == null) {
            return Optional.empty();
        }
        return Optional.of(new YarnLockEntry(ids, version, new LinkedList<>(dependencies.values())));
    }
}
