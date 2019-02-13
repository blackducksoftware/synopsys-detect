package com.synopsys.integration.detectable.detectable.executable.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.BashResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CondaResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CpanResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CpanmResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.GradleResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PearResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.Rebar3Resolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.YarnResolver;

//this will cache the find result.
public class SimpleExecutableResolver implements GradleResolver, BashResolver, CondaResolver, CpanmResolver, CpanResolver, PearResolver, Rebar3Resolver, YarnResolver {
    private final CachedExecutableResolverOptions executableResolverOptions;
    private final SimpleLocalExecutableFinder localExecutableFinder;
    private final SimpleSystemExecutableFinder systemExecutableFinder;

    Map<String, File> cached = new HashMap<>();

    public SimpleExecutableResolver(CachedExecutableResolverOptions executableResolverOptions, SimpleLocalExecutableFinder localExecutableFinder, SimpleSystemExecutableFinder systemExecutableFinder) {
        this.executableResolverOptions = executableResolverOptions;
        this.localExecutableFinder = localExecutableFinder;
        this.systemExecutableFinder = systemExecutableFinder;
    }

    private File findCachedSystem(String name) {
        if (cached.containsKey(name)) {
            return cached.get(name);
        } else {
            File found = systemExecutableFinder.findExecutable(name);
            cached.put(name, found);
            return found;
        }
    }

    private File findLocalOrSystem(String localName, String systemName, DetectableEnvironment environment) {
        File local = localExecutableFinder.findExecutable(localName, environment.getDirectory());
        if (local != null) {
            return local;
        } else {
            return findCachedSystem(systemName);
        }
    }

    private File findLocalOrSystem(String name, DetectableEnvironment environment) {
        return findLocalOrSystem(name, name, environment);
    }

    @Override
    public File resolveGradle(final DetectableEnvironment environment) throws DetectableException {
        return findLocalOrSystem("gradlew", "gradle", environment);
    }

    @Override
    public File resolveBash() throws DetectableException {
        return findCachedSystem("bash");
    }

    @Override
    public File resolveConda() throws DetectableException {
        return findCachedSystem("conda");
    }

    @Override
    public File resolveCpan() throws DetectableException {
        return findCachedSystem("cpan");
    }

    @Override
    public File resolvePear() throws DetectableException {
        return findCachedSystem("pear");
    }

    @Override
    public File resolveRebar3() throws DetectableException {
        return findCachedSystem("rebar3");
    }

    @Override
    public File resolveYarn() throws DetectableException {
        return findCachedSystem("yarn");
    }
}
