package com.synopsys.integration.detectable.detectable.executable.impl;

import java.io.File;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.ExecutableResolver;
import com.synopsys.integration.detectable.detectable.executable.ExecutableType;

public class GradleExecutableResolver implements ExecutableResolver {
    private final CachedExecutableResolver cachedExecutableResolver;
    private final SimpleLocalExecutableFinder simpleLocalExecutableFinder;
    private final SimpleSystemExecutableFinder simpleSystemExecutableFinder;

    public GradleExecutableResolver(final CachedExecutableResolver cachedExecutableResolver, final SimpleLocalExecutableFinder simpleLocalExecutableFinder,
        final SimpleSystemExecutableFinder simpleSystemExecutableFinder) {
        this.cachedExecutableResolver = cachedExecutableResolver;
        this.simpleLocalExecutableFinder = simpleLocalExecutableFinder;
        this.simpleSystemExecutableFinder = simpleSystemExecutableFinder;
    }

    @Override
    public File resolveExecutable(final ExecutableType executableType, final DetectableEnvironment environment) {
        if (ExecutableType.GRADLE.equals(executableType)) {
            File gradle = simpleLocalExecutableFinder.findExecutable("gradlew", environment.getDirectory());

            if (gradle == null) {
                gradle = simpleSystemExecutableFinder.findExecutable("gradle");
            }

            return gradle;
        }

        return cachedExecutableResolver.resolveExecutable(executableType, environment);
    }
}
