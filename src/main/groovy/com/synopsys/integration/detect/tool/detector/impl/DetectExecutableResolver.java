package com.synopsys.integration.detect.tool.detector.impl;

import java.io.File;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.BashResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.BazelResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CondaResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CpanResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CpanmResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.GradleResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.MavenResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.NpmResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PearResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PipResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PipenvResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PythonResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.Rebar3Resolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.YarnResolver;
import com.synopsys.integration.detectable.detectable.inspector.go.GoDepResolver;
import com.synopsys.integration.detectable.detectable.inspector.go.GoResolver;

public class DetectExecutableResolver implements GradleResolver, BashResolver, CondaResolver, CpanmResolver, CpanResolver, PearResolver, Rebar3Resolver, YarnResolver, PythonResolver, PipResolver, PipenvResolver, MavenResolver, NpmResolver,
                                                     BazelResolver, GoDepResolver, GoResolver {
    @Override
    public File resolveBash() throws DetectableException {
        return null;
    }

    @Override
    public File resolveBazel() throws DetectableException {
        return null;
    }

    @Override
    public File resolveConda() throws DetectableException {
        return null;
    }

    @Override
    public File resolveCpan() throws DetectableException {
        return null;
    }

    @Override
    public File resolveGradle(final DetectableEnvironment environment) throws DetectableException {
        return null;
    }

    @Override
    public File resolveMaven(final DetectableEnvironment environment) {
        return null;
    }

    @Override
    public File resolveNpm(final DetectableEnvironment environment) {
        return null;
    }

    @Override
    public File resolvePear() throws DetectableException {
        return null;
    }

    @Override
    public File resolvePip() throws DetectableException {
        return null;
    }

    @Override
    public File resolvePipenv() {
        return null;
    }

    @Override
    public File resolvePython() throws DetectableException {
        return null;
    }

    @Override
    public File resolveRebar3() throws DetectableException {
        return null;
    }

    @Override
    public File resolveYarn() throws DetectableException {
        return null;
    }

    @Override
    public File resolveGoDep(final File location) throws DetectableException {
        return null;
    }

    @Override
    public File resolveGo() throws DetectableException {
        return null;
    }
}
