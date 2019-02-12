package com.synopsys.integration.detectable.detectable.executable.impl;

import java.io.File;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.ExecutableResolver;
import com.synopsys.integration.detectable.detectable.executable.ExecutableType;

public class MavenExecutableResolver implements ExecutableResolver {
    private final CachedExecutableResolver cachedExecutableResolver;
    private final SimpleLocalExecutableFinder simpleLocalExecutableFinder;
    private final SimpleSystemExecutableFinder simpleSystemExecutableFinder;

    public MavenExecutableResolver(final CachedExecutableResolver cachedExecutableResolver, final SimpleLocalExecutableFinder simpleLocalExecutableFinder, final SimpleSystemExecutableFinder simpleSystemExecutableFinder) {
        this.cachedExecutableResolver = cachedExecutableResolver;
        this.simpleLocalExecutableFinder = simpleLocalExecutableFinder;
        this.simpleSystemExecutableFinder = simpleSystemExecutableFinder;
    }

    @Override
    public File resolveExecutable(final ExecutableType executableType, final DetectableEnvironment environment) {
        if (ExecutableType.GRADLE.equals(executableType)) {
            File maven = simpleLocalExecutableFinder.findExecutable("mvnw", environment.getDirectory());

            if (maven == null) {
                maven = simpleSystemExecutableFinder.findExecutable("mvn");
            }

            return maven;
        }

        return cachedExecutableResolver.resolveExecutable(executableType, environment);
    }
}
