package com.synopsys.integration.detectable.detectable.executable.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.ExecutableResolver;
import com.synopsys.integration.detectable.detectable.executable.ExecutableType;

//this will cache the find result.
public class CachedExecutableResolver implements ExecutableResolver {
    private final SimpleLocalExecutableFinder localExecutableFinder;
    private final SimpleSystemExecutableFinder systemExecutableFinder;

    private final Map<String, File> cached = new HashMap<>();

    public CachedExecutableResolver(final SimpleLocalExecutableFinder localExecutableFinder, final SimpleSystemExecutableFinder systemExecutableFinder) {
        this.localExecutableFinder = localExecutableFinder;
        this.systemExecutableFinder = systemExecutableFinder;
    }

    @Override
    public File resolveExecutable(final ExecutableType executableType, final DetectableEnvironment environment) {
        final String executableName = executableType.name().toLowerCase();

        File foundExecutable = localExecutableFinder.findExecutable(executableName, environment.getDirectory());

        if (foundExecutable == null) {
            foundExecutable = findCachedSystem(executableName);
        }

        return foundExecutable;
    }

    private File findCachedSystem(final String name) {
        if (!cached.containsKey(name)) {
            final File found = systemExecutableFinder.findExecutable(name);
            cached.put(name, found);
        }

        return cached.get(name);
    }
}
