package com.synopsys.integration.detectable.detectables.pipenv.parse;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.pipenv.parse.data.PipfileLock;
import com.synopsys.integration.detectable.detectables.pipenv.parse.data.PipfileLockDependencyEntry;
import com.synopsys.integration.detectable.detectables.pipenv.parse.model.PipfileLockDependency;

public class PipfileLockParser {
    private final EnumListFilter<PipenvDependencyType> dependencyTypeFilter;

    public PipfileLockParser(EnumListFilter<PipenvDependencyType> dependencyTypeFilter) {
        this.dependencyTypeFilter = dependencyTypeFilter;
    }

    public List<PipfileLockDependency> parse(PipfileLock pipfileLock) {
        List<PipfileLockDependency> dependencies = new LinkedList<>();
        dependencies.addAll(parseDependencyInfo(pipfileLock.dependencies));
        if (dependencyTypeFilter.shouldInclude(PipenvDependencyType.DEV)) {
            dependencies.addAll(parseDependencyInfo(pipfileLock.devDependencies));
        }
        return dependencies;
    }

    private List<PipfileLockDependency> parseDependencyInfo(Map<String, PipfileLockDependencyEntry> dependencyEntries) {
        return dependencyEntries.entrySet().stream()
            .map(entry -> new PipfileLockDependency(entry.getKey(), parseRawVersion(entry.getValue().version)))
            .collect(Collectors.toList());
    }

    private String parseRawVersion(@Nullable String rawVersion) {
        if (rawVersion == null) {
            return null;
        }
        return rawVersion.replace("==", "");
    }
}
