package com.synopsys.integration.detectable.detectables.npm.cli.parse;

import java.util.Set;

public class NpmDependencyTypeFilter {
    private final Set<String> devDependencies;
    private final Set<String> peerDependencies;
    private final boolean includeDevDependencies;
    private final boolean includePeerDependencies;

    public NpmDependencyTypeFilter(Set<String> devDependencies, Set<String> peerDependencies, boolean includeDevDependencies, boolean includePeerDependencies) {
        this.devDependencies = devDependencies;
        this.peerDependencies = peerDependencies;
        this.includeDevDependencies = includeDevDependencies;
        this.includePeerDependencies = includePeerDependencies;
    }

    public boolean shouldInclude(String dependencyName, boolean isRootDependency) {
        if (!isRootDependency) {
            return true;
        }

        boolean excludeDev = !includeDevDependencies && devDependencies.contains(dependencyName);
        boolean excludePeer = !includePeerDependencies && peerDependencies.contains(dependencyName);

        return !(excludeDev || excludePeer);

    }
}
