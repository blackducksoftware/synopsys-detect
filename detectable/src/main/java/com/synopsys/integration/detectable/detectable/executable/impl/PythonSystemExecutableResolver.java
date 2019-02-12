package com.synopsys.integration.detectable.detectable.executable.impl;

import java.io.File;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.ExecutableResolver;
import com.synopsys.integration.detectable.detectable.executable.ExecutableType;

public class PythonSystemExecutableResolver implements ExecutableResolver {
    private final CachedExecutableResolver cachedExecutableResolver;
    private final SimpleSystemExecutableFinder simpleSystemExecutableFinder;
    private final boolean usingPython3;

    public PythonSystemExecutableResolver(final CachedExecutableResolver cachedExecutableResolver, final SimpleSystemExecutableFinder simpleSystemExecutableFinder, final boolean usingPython3) {
        this.cachedExecutableResolver = cachedExecutableResolver;
        this.simpleSystemExecutableFinder = simpleSystemExecutableFinder;
        this.usingPython3 = usingPython3;
    }

    @Override
    public File resolveExecutable(final ExecutableType executableType, final DetectableEnvironment environment) {
        if (ExecutableType.PYTHON.equals(executableType) || ExecutableType.PIP.equals(executableType)) {
            final String suffix = usingPython3 ? "3" : "";
            final String executableName = executableType.name().toLowerCase() + suffix;
            return simpleSystemExecutableFinder.findExecutable(executableName);
        } else {
            return cachedExecutableResolver.resolveExecutable(executableType, environment);
        }
    }
}
