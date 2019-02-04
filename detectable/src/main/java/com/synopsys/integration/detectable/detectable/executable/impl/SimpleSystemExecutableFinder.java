package com.synopsys.integration.detectable.detectable.executable.impl;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.executable.ExecutableType;
import com.synopsys.integration.detectable.detectable.executable.SystemExecutableFinder;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.util.OperatingSystemType;

public class SimpleSystemExecutableFinder implements SystemExecutableFinder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SimpleExecutableFinder executableFinder;

    public SimpleSystemExecutableFinder(SimpleExecutableFinder executableFinder){
        this.executableFinder = executableFinder;
    }

    @Override
    public File findExecutable(final String executable) {
        final String systemPath = System.getenv("PATH");
        List<File> systemPathLocations = Arrays.stream(systemPath.split(File.pathSeparator))
                                             .map(File::new)
                                             .collect(Collectors.toList());

        File found = executableFinder.findExecutable(executable, systemPathLocations);
        if (found == null){
            logger.debug(String.format("Could not find the executable: %s while searching through: %s", executable, systemPath));
        }
        return found;
    }
}
