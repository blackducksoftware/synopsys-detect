package com.synopsys.integration.detectable.detectables.bazel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class BazelDetectableOptions {
    private final String targetName;
    private final Set<WorkspaceRule> bazelDependencyRules;
    private final List<String> bazelCqueryAdditionalOptions;

    public BazelDetectableOptions(String targetName, Set<WorkspaceRule> bazelDependencyRules,
        List<String> bazelCqueryAdditionalOptions) {
        this.targetName = targetName;
        this.bazelDependencyRules = bazelDependencyRules;
        this.bazelCqueryAdditionalOptions = bazelCqueryAdditionalOptions;
    }

    public Optional<String> getTargetName() {
        return Optional.ofNullable(targetName);
    }

    public List<String> getBazelCqueryAdditionalOptions() {
        return bazelCqueryAdditionalOptions;
    }

    public Set<WorkspaceRule> getBazelDependencyRules() {
        return bazelDependencyRules;
    }
}
