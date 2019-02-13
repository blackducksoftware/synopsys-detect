package com.synopsys.integration.detectable.detectable.executable.impl;

import java.io.File;

public class SimpleLocalExecutableFinder {

    private final SimpleExecutableFinder simpleExecutableFinder;

    public SimpleLocalExecutableFinder(SimpleExecutableFinder simpleExecutableFinder) {
        this.simpleExecutableFinder = simpleExecutableFinder;
    }

    public File findExecutable(final String executableType, final File location) {
        return simpleExecutableFinder.findExecutable(executableType, location);
    }
}
