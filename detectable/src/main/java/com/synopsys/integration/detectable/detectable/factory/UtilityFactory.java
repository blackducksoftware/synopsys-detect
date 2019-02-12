package com.synopsys.integration.detectable.detectable.factory;

import com.synopsys.integration.detectable.detectable.executable.ExecutableResolver;
import com.synopsys.integration.detectable.detectable.executable.impl.CachedExecutableResolver;
import com.synopsys.integration.detectable.detectable.executable.impl.GradleExecutableResolver;
import com.synopsys.integration.detectable.detectable.executable.impl.MavenExecutableResolver;
import com.synopsys.integration.detectable.detectable.executable.impl.PythonSystemExecutableResolver;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleExecutableFinder;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleLocalExecutableFinder;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleSystemExecutableFinder;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.file.impl.SimpleFileFinder;

public class UtilityFactory {
    public FileFinder simpleFileFinder() {
        return new SimpleFileFinder();
    }

    public SimpleExecutableFinder simpleExecutableFinder() {
        return SimpleExecutableFinder.forCurrentOperatingSystem(simpleFileFinder());
    }

    public SimpleLocalExecutableFinder simpleLocalExecutableFinder() {
        return new SimpleLocalExecutableFinder(simpleExecutableFinder());
    }

    public SimpleSystemExecutableFinder simpleSystemExecutableFinder() {
        return new SimpleSystemExecutableFinder(simpleExecutableFinder());
    }

    public ExecutableResolver executableResolver() {
        return cachedExecutableResolver();
    }

    public CachedExecutableResolver cachedExecutableResolver() {
        return new CachedExecutableResolver(simpleLocalExecutableFinder(), simpleSystemExecutableFinder());
    }

    public PythonSystemExecutableResolver pythonExecutableResolver() {
        final boolean usingPython3 = false; // TODO: Get from configuration
        return new PythonSystemExecutableResolver(cachedExecutableResolver(), simpleSystemExecutableFinder(), usingPython3);
    }

    public MavenExecutableResolver mavenExecutableResolver() {
        return new MavenExecutableResolver(cachedExecutableResolver(), simpleLocalExecutableFinder(), simpleSystemExecutableFinder());
    }

    public GradleExecutableResolver gradleExecutableResolver() {
        return new GradleExecutableResolver(cachedExecutableResolver(), simpleLocalExecutableFinder(), simpleSystemExecutableFinder());
    }
}
