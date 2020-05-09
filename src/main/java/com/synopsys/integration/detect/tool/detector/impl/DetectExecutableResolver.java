/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.tool.detector.impl;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleExecutableResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.BashResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.BazelResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CondaResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CpanResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CpanmResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.DockerResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.DotNetResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.GitResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.GradleResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.JavaResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.MavenResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.NpmResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PearResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PipResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PipenvResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PythonResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.Rebar3Resolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.SwiftResolver;
import com.synopsys.integration.detectable.detectable.inspector.go.GoResolver;

public class DetectExecutableResolver
    implements JavaResolver, GradleResolver, BashResolver, CondaResolver, CpanmResolver, CpanResolver, PearResolver, Rebar3Resolver, PythonResolver, PipResolver, PipenvResolver, MavenResolver, NpmResolver, BazelResolver, DockerResolver,
                   DotNetResolver, GitResolver, SwiftResolver, GoResolver {

    private final SimpleExecutableResolver simpleExecutableResolver;
    private final DetectExecutableOptions detectExecutableOptions;

    private final Map<String, File> cachedExecutables = new HashMap<>();

    public DetectExecutableResolver(final SimpleExecutableResolver simpleExecutableResolver, final DetectExecutableOptions detectExecutableOptions) {
        this.simpleExecutableResolver = simpleExecutableResolver;
        this.detectExecutableOptions = detectExecutableOptions;
    }

    private File resolveExecutable(final String cacheKey, final Supplier<File> resolveExecutable, @Nullable final Path executableOverride) throws DetectableException {
        if (executableOverride != null) {
            final File exe = executableOverride.toFile();
            if (!exe.exists()) {
                throw new DetectableException("Executable override must exist: " + executableOverride);
            } else if (!exe.isFile()) {
                throw new DetectableException("Executable override must be a file: " + executableOverride);
            } else if (!exe.canExecute()) {
                throw new DetectableException("Executable override must be executable: " + executableOverride);
            } else {
                return exe;
            }
        }
        final boolean hasCacheKey = StringUtils.isNotBlank(cacheKey);
        if (hasCacheKey && cachedExecutables.containsKey(cacheKey)) {
            return cachedExecutables.get(cacheKey);
        }
        final File resolved = resolveExecutable.get();
        if (hasCacheKey) {
            cachedExecutables.put(cacheKey, resolved);
        }
        return resolved;
    }

    private File resolveExecutableLocally(final Function<DetectableEnvironment, File> resolveExecutable, final DetectableEnvironment environment, final Path executableOverride) throws DetectableException {
        return resolveExecutable(null, () -> resolveExecutable.apply(environment), executableOverride);
    }

    @Override
    public File resolveBash() throws DetectableException {
        return resolveExecutable("bash", simpleExecutableResolver::resolveBash, detectExecutableOptions.getBashUserPath());
    }

    @Override
    public File resolveBazel() throws DetectableException {
        return resolveExecutable("bazel", simpleExecutableResolver::resolveBazel, detectExecutableOptions.getBazelUserPath());
    }

    @Override
    public File resolveConda() throws DetectableException {
        return resolveExecutable("conda", simpleExecutableResolver::resolveConda, detectExecutableOptions.getCondaUserPath());
    }

    @Override
    public File resolveCpan() throws DetectableException {
        return resolveExecutable("cpan", simpleExecutableResolver::resolveCpan, detectExecutableOptions.getCpanUserPath());
    }

    @Override
    public File resolveCpanm() throws DetectableException {
        return resolveExecutable("cpanm", simpleExecutableResolver::resolveCpanm, detectExecutableOptions.getCpanmUserPath());
    }

    @Override
    public File resolveGradle(final DetectableEnvironment environment) throws DetectableException {
        return resolveExecutableLocally(simpleExecutableResolver::resolveGradle, environment, detectExecutableOptions.getGradleUserPath());
    }

    @Override
    public File resolveMaven(final DetectableEnvironment environment) throws DetectableException {
        return resolveExecutableLocally(simpleExecutableResolver::resolveMaven, environment, detectExecutableOptions.getMavenUserPath());
    }

    @Override
    public File resolveNpm(final DetectableEnvironment environment) throws DetectableException {
        return resolveExecutableLocally(simpleExecutableResolver::resolveNpm, environment, detectExecutableOptions.getNpmUserPath());
    }

    @Override
    public File resolvePear() throws DetectableException {
        return resolveExecutable("pear", simpleExecutableResolver::resolvePear, detectExecutableOptions.getPearUserPath());
    }

    @Override
    public File resolvePip() throws DetectableException {
        return resolveExecutable("pip", simpleExecutableResolver::resolvePip, null);
    }

    @Override
    public File resolvePipenv() throws DetectableException {
        return resolveExecutable("pipenv", simpleExecutableResolver::resolvePipenv, detectExecutableOptions.getPipenvUserPath());
    }

    @Override
    public File resolvePython() throws DetectableException {
        return resolveExecutable("python", simpleExecutableResolver::resolvePython, detectExecutableOptions.getPythonUserPath());
    }

    @Override
    public File resolveRebar3() throws DetectableException {
        return resolveExecutable("rebar3", simpleExecutableResolver::resolveRebar3, detectExecutableOptions.getRebarUserPath());
    }

    @Override
    public File resolveJava() throws DetectableException {
        return resolveExecutable("java", simpleExecutableResolver::resolveJava, detectExecutableOptions.getJavaUserPath());
    }

    @Override
    public File resolveDocker() throws DetectableException {
        return resolveExecutable("docker", simpleExecutableResolver::resolveDocker, detectExecutableOptions.getDockerUserPath());
    }

    @Override
    public File resolveDotNet() throws DetectableException {
        return resolveExecutable("dotnet", simpleExecutableResolver::resolveDotNet, detectExecutableOptions.getDotnetUserPath());
    }

    @Override
    public File resolveGit() throws DetectableException {
        return resolveExecutable("git", simpleExecutableResolver::resolveGit, detectExecutableOptions.getGitUserPath());
    }

    @Override
    public File resolveSwift() throws DetectableException {
        return resolveExecutable("swift", simpleExecutableResolver::resolveSwift, detectExecutableOptions.getSwiftUserPath());
    }

    @Override
    public File resolveGo() throws DetectableException {
        return resolveExecutable("go", simpleExecutableResolver::resolveGo, detectExecutableOptions.getGoUserPath());
    }
}

