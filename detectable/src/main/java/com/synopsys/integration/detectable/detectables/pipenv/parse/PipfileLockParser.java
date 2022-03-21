package com.synopsys.integration.detectable.detectables.pipenv.parse;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.pipenv.parse.model.PipfileLock;
import com.synopsys.integration.detectable.detectables.pipenv.parse.model.PipfileLockDependency;
import com.synopsys.integration.detectable.detectables.pipenv.parse.model.PipfileLockDependencyEntry;

public class PipfileLockParser {
    public List<PipfileLockDependency> parse(PipfileLock pipfileLock, EnumListFilter<PipenvDependencyType> dependencyTypeFilter) {
        List<PipfileLockDependency> dependencies = new LinkedList<>();
        dependencies.addAll(parseDependencyInfo(pipfileLock.dependencies));
        if (!dependencyTypeFilter.shouldExclude(PipenvDependencyType.DEV)) {
            dependencies.addAll(parseDependencyInfo(pipfileLock.devDependencies));
        }
        return dependencies;
    }

    private List<PipfileLockDependency> parseDependencyInfo(Map<String, PipfileLockDependencyEntry> dependencyEntries) {
        return dependencyEntries.entrySet().stream()
            .map(entry -> new PipfileLockDependency(entry.getKey(), parseRawVersion(entry.getValue().version)))
            .collect(Collectors.toList());
    }

    private String parseRawVersion(String rawVersion) {
        return rawVersion.replace("==", "");
    }
}
