package com.synopsys.integration.detectable.detectable.executable.impl;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleSystemExecutableFinder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SimpleExecutableFinder executableFinder;

    public SimpleSystemExecutableFinder(final SimpleExecutableFinder executableFinder) {
        this.executableFinder = executableFinder;
    }

    public File findExecutable(final String executableName) {
        final String systemPath = System.getenv("PATH");
        final List<File> systemPathLocations = Arrays.stream(systemPath.split(File.pathSeparator))
                                             .map(File::new)
                                             .collect(Collectors.toList());

        final File found = executableFinder.findExecutable(executableName, systemPathLocations);
        if (found == null) {
            logger.debug(String.format("Could not find the executable: %s while searching through: %s", executableName, systemPath));
        }
        return found;
    }
}
