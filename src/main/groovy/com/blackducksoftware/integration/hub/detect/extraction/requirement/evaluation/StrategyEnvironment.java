package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation;

import java.io.File;
import java.util.Set;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.util.ExcludedIncludedFilter;

public class StrategyEnvironment {

    private final File directory;
    private final Set<Strategy> appliedToDirectory;
    private final Set<Strategy> appliedToParent;
    private final int depth;
    private final ExcludedIncludedFilter bomToolFilter;

    public StrategyEnvironment(final File directory, final Set<Strategy> appliedToDirectory, final Set<Strategy> appliedToParent, final int depth, final ExcludedIncludedFilter bomToolFilter) {
        this.directory = directory;
        this.appliedToDirectory = appliedToDirectory;
        this.appliedToParent = appliedToParent;
        this.depth = depth;
        this.bomToolFilter = bomToolFilter;
    }

    public File getDirectory() {
        return directory;
    }

    public Set<Strategy> getAppliedToDirectory() {
        return appliedToDirectory;
    }

    public Set<Strategy> getAppliedToParent() {
        return appliedToParent;
    }

    public int getDepth() {
        return depth;
    }

    public ExcludedIncludedFilter getBomToolFilter() {
        return bomToolFilter;
    }
}
