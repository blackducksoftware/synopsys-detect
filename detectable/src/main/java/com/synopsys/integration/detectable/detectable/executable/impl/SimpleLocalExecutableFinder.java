package com.synopsys.integration.detectable.detectable.executable.impl;

import java.io.File;

public class SimpleLocalExecutableFinder {

    private final SimpleExecutableFinder simpleExecutableFinder;

    public SimpleLocalExecutableFinder(final SimpleExecutableFinder simpleExecutableFinder) {
        this.simpleExecutableFinder = simpleExecutableFinder;
    }

    public File findExecutable(final String executableName, final File location) {
        return simpleExecutableFinder.findExecutable(executableName, location);
    }
}
