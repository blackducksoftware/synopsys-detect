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

    public SimpleSystemExecutableFinder(SimpleExecutableFinder executableFinder) {
        this.executableFinder = executableFinder;
    }

    public File findExecutable(final String executable) {
        final String systemPath = System.getenv("PATH");
        List<File> systemPathLocations = Arrays.stream(systemPath.split(File.pathSeparator))
                                             .map(File::new)
                                             .collect(Collectors.toList());

        File found = executableFinder.findExecutable(executable, systemPathLocations);
        if (found == null) {
            logger.debug(String.format("Could not find the executable: %s while searching through: %s", executable, systemPath));
        }
        return found;
    }
}
