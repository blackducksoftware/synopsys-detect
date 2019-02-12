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

    private File resolveExecutable(final SimpleExecutableType simpleExecutableType, final DetectableEnvironment environment) throws DetectableException {
        switch (simpleExecutableType) {
            case BASH:
                return findCachedSystem("bash");
            case BITBAKE:
                return findCachedSystem("bitbake");
            case CONDA:
                return findCachedSystem("conda");
            case CPAN:
                return findCachedSystem("cpan");
            case CPANM:
                return findCachedSystem("cpanm");
            case DOCKER:
                return findCachedSystem("docker");
            case DOTNET:
                return findCachedSystem("dotnet");
            case GO:
                return findCachedSystem("go");
            case GO_DEP:
                return findCachedSystem("deps");
            case GRADLE:
                return findLocalOrSystem("gradlew", "gradle", environment);
            case MVN:
                return findLocalOrSystem("mvnw", "mvn", environment);
            case NPM:
                return findCachedSystem("npm");
            case NUGET:
                return findCachedSystem("nuget");
            case PEAR:
                return findCachedSystem("pear");
            case PERL:
                return findCachedSystem("pearl");
            case PIP:
                if (executableResolverOptions.python3) {
                    return findCachedSystem("pip3");
                } else {
                    return findCachedSystem("pip");
                }
            case PIPENV:
                return findCachedSystem("pipenv");
            case PYTHON:
                if (executableResolverOptions.python3) {
                    return findCachedSystem("python3");
                } else {
                    return findCachedSystem("python");
                }
            case REBAR3:
                return findCachedSystem("rebar3");
            case YARN:
                return findCachedSystem("yarn");
            case JAVA:
                return findCachedSystem("java");
        }
        return null;
    }

    @Override
    public File resolveGradle(final DetectableEnvironment environment) throws DetectableException {
        return resolveExecutable(SimpleExecutableType.GRADLE, environment);
    }

    @Override
    public File resolveBash() throws DetectableException {
        return resolveExecutable(SimpleExecutableType.BASH, null);
    }

    @Override
    public File resolveConda() throws DetectableException {
        return resolveExecutable(SimpleExecutableType.CONDA, null);
    }

    @Override
    public File resolveCpan() throws DetectableException {
        return resolveExecutable(SimpleExecutableType.CPAN, null);
    }

    @Override
    public File resolvePear() throws DetectableException {
        return resolveExecutable(SimpleExecutableType.PEAR, null);
    }

    @Override
    public File resolveRebar3() throws DetectableException {
        return resolveExecutable(SimpleExecutableType.REBAR3, null);
    }

    @Override
    public File resolveYarn() throws DetectableException {
        return resolveExecutable(SimpleExecutableType.YARN, null);
    }
}
