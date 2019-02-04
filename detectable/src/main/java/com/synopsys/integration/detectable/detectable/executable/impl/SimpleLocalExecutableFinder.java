package com.synopsys.integration.detectable.detectable.executable.impl;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.executable.ExecutableType;
import com.synopsys.integration.detectable.detectable.executable.LocalExecutableFinder;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.util.OperatingSystemType;

public class SimpleLocalExecutableFinder implements LocalExecutableFinder {

    private final SimpleExecutableFinder simpleExecutableFinder;

    public SimpleLocalExecutableFinder(SimpleExecutableFinder simpleExecutableFinder) {
        this.simpleExecutableFinder = simpleExecutableFinder;
    }

    @Override
    public File findExecutable(final String executableType, final File location) {
        return simpleExecutableFinder.findExecutable(executableType, location);
    }
}
