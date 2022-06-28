package com.synopsys.integration.detector.finder;

import java.io.File;
import java.util.Set;

public class DirectoryFindResult {
    private final File directory;
    private final int depthFromRoot;
    private final Set<DirectoryFindResult> children;

    public DirectoryFindResult(
        File directory,
        int depthFromRoot,
        Set<DirectoryFindResult> children
    ) {
        this.directory = directory;
        this.depthFromRoot = depthFromRoot;
        this.children = children;
    }

    public File getDirectory() {
        return directory;
    }

    public int getDepthFromRoot() {
        return depthFromRoot;
    }

    public Set<DirectoryFindResult> getChildren() {
        return children;
    }
}
