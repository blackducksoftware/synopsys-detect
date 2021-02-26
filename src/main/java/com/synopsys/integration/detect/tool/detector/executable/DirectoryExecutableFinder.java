/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector.executable;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.util.OperatingSystemType;

// Finds an executable in a directory.
public class DirectoryExecutableFinder {
    private final List<String> extensions;
    private final FileFinder fileFinder;

    public static DirectoryExecutableFinder forCurrentOperatingSystem(final FileFinder fileFinder) {
        return DirectoryExecutableFinder.forOperatingSystem(OperatingSystemType.determineFromSystem(), fileFinder);
    }

    public static DirectoryExecutableFinder forOperatingSystem(final OperatingSystemType operatingSystemType, final FileFinder fileFinder) {
        if (operatingSystemType == OperatingSystemType.WINDOWS) {
            return new DirectoryExecutableFinder(Arrays.asList(".cmd", ".bat", ".exe"), fileFinder);
        } else {
            return new DirectoryExecutableFinder(Collections.emptyList(), fileFinder);
        }
    }

    public DirectoryExecutableFinder(final List<String> extensions, final FileFinder fileFinder) {
        this.extensions = extensions;
        this.fileFinder = fileFinder;
    }

    private List<String> executablesFromName(final String name) {
        if (extensions.isEmpty()) {
            return Collections.singletonList(name);
        } else {
            return extensions.stream().map(ext -> name + ext).collect(Collectors.toList());
        }
    }

    @Nullable
    public File findExecutable(final String executable, final File location) {
        return findExecutable(executable, Collections.singletonList(location));
    }

    @Nullable
    public File findExecutable(final String executable, final List<File> locations) {
        final List<String> executables = executablesFromName(executable);

        for (final File location : locations) {
            for (final String possibleExecutable : executables) {
                final File foundFile = fileFinder.findFile(location, possibleExecutable);
                if (foundFile != null && foundFile.exists() && foundFile.canExecute()) {
                    return foundFile;
                }
            }
        }

        return null;
    }
}