package com.synopsys.integration.detectable.detectables.pipenv.parse;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.pipenv.parse.data.PipfileLock;
import com.synopsys.integration.detectable.detectables.pipenv.parse.data.PipfileLockDependencyEntry;
import com.synopsys.integration.detectable.detectables.pipenv.parse.model.PipfileLockDependency;

public class PipfileLockTransformer {
    private final PipfileLockDependencyVersionParser dependencyVersionParser;
    private final EnumListFilter<PipenvDependencyType> dependencyTypeFilter;

    public PipfileLockTransformer(
        PipfileLockDependencyVersionParser dependencyVersionParser,
        EnumListFilter<PipenvDependencyType> dependencyTypeFilter
    ) {
        this.dependencyVersionParser = dependencyVersionParser;
        this.dependencyTypeFilter = dependencyTypeFilter;
    }

    public List<PipfileLockDependency> transform(PipfileLock pipfileLock) {
        List<PipfileLockDependency> dependencies = new LinkedList<>();
        dependencies.addAll(convertEntriesToDependencyInfo(pipfileLock.dependencies));
        if (dependencyTypeFilter.shouldInclude(PipenvDependencyType.DEV)) {
            dependencies.addAll(convertEntriesToDependencyInfo(pipfileLock.devDependencies));
        }
        return dependencies;
    }

    private List<PipfileLockDependency> convertEntriesToDependencyInfo(Map<String, PipfileLockDependencyEntry> dependencyEntries) {
        return dependencyEntries.entrySet().stream()
            .map(entry -> new PipfileLockDependency(entry.getKey(), dependencyVersionParser.parseRawVersion(entry.getValue().version)))
            .collect(Collectors.toList());
    }
}
