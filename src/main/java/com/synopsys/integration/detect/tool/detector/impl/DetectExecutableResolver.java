/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
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
import com.synopsys.integration.detectable.detectable.executable.resolver.YarnResolver;
import com.synopsys.integration.detectable.detectable.inspector.go.GoResolver;

public class DetectExecutableResolver
    implements JavaResolver, GradleResolver, BashResolver, CondaResolver, CpanmResolver, CpanResolver, PearResolver, Rebar3Resolver, YarnResolver, PythonResolver, PipResolver, PipenvResolver, MavenResolver, NpmResolver, BazelResolver,
                   DockerResolver, DotNetResolver, GitResolver, SwiftResolver, GoResolver {

    private final SimpleExecutableResolver simpleExecutableResolver;
    private final DetectConfiguration detectConfiguration;
    private final Map<String, File> cachedExecutables = new HashMap<>();

    public DetectExecutableResolver(final SimpleExecutableResolver simpleExecutableResolver, final DetectConfiguration detectConfiguration) {
        this.simpleExecutableResolver = simpleExecutableResolver;
        this.detectConfiguration = detectConfiguration;
    }

    private File resolveExecutable(final String cacheKey, final Supplier<File> resolveExecutable, final String executableOverride) throws DetectableException {
        if (StringUtils.isNotBlank(executableOverride)) {
            final File exe = new File(executableOverride);
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

    private File resolveExecutableLocally(final Function<DetectableEnvironment, File> resolveExecutable, final DetectableEnvironment environment, final String executableOverride) throws DetectableException {
        return resolveExecutable(null, () -> resolveExecutable.apply(environment), executableOverride);
    }

    @Override
    public File resolveBash() throws DetectableException {
        return resolveExecutable("bash", simpleExecutableResolver::resolveBash, detectConfiguration.getProperty(DetectProperty.DETECT_BASH_PATH, PropertyAuthority.None));
    }

    @Override
    public File resolveBazel() throws DetectableException {
        return resolveExecutable("bazel", simpleExecutableResolver::resolveBazel, detectConfiguration.getProperty(DetectProperty.DETECT_BAZEL_PATH, PropertyAuthority.None));
    }

    @Override
    public File resolveConda() throws DetectableException {
        return resolveExecutable("conda", simpleExecutableResolver::resolveConda, detectConfiguration.getProperty(DetectProperty.DETECT_CONDA_PATH, PropertyAuthority.None));
    }

    @Override
    public File resolveCpan() throws DetectableException {
        return resolveExecutable("cpan", simpleExecutableResolver::resolveCpan, detectConfiguration.getProperty(DetectProperty.DETECT_CPAN_PATH, PropertyAuthority.None));
    }

    @Override
    public File resolveCpanm() throws DetectableException {
        return resolveExecutable("cpanm", simpleExecutableResolver::resolveCpanm, detectConfiguration.getProperty(DetectProperty.DETECT_CPANM_PATH, PropertyAuthority.None));
    }

    @Override
    public File resolveGradle(final DetectableEnvironment environment) throws DetectableException {
        return resolveExecutableLocally(simpleExecutableResolver::resolveGradle, environment, detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_PATH, PropertyAuthority.None));
    }

    @Override
    public File resolveMaven(final DetectableEnvironment environment) throws DetectableException {
        return resolveExecutableLocally(simpleExecutableResolver::resolveMaven, environment, detectConfiguration.getProperty(DetectProperty.DETECT_MAVEN_PATH, PropertyAuthority.None));
    }

    @Override
    public File resolveNpm(final DetectableEnvironment environment) throws DetectableException {
        return resolveExecutableLocally(simpleExecutableResolver::resolveNpm, environment, detectConfiguration.getProperty(DetectProperty.DETECT_NPM_PATH, PropertyAuthority.None));
    }

    @Override
    public File resolvePear() throws DetectableException {
        return resolveExecutable("pear", simpleExecutableResolver::resolvePear, detectConfiguration.getProperty(DetectProperty.DETECT_PEAR_PATH, PropertyAuthority.None));
    }

    @Override
    public File resolvePip() throws DetectableException {
        return resolveExecutable("pip", simpleExecutableResolver::resolvePip, null);
    }

    @Override
    public File resolvePipenv() throws DetectableException {
        return resolveExecutable("pipenv", simpleExecutableResolver::resolvePipenv, detectConfiguration.getProperty(DetectProperty.DETECT_PIPENV_PATH, PropertyAuthority.None));
    }

    @Override
    public File resolvePython() throws DetectableException {
        return resolveExecutable("python", simpleExecutableResolver::resolvePython, detectConfiguration.getProperty(DetectProperty.DETECT_PYTHON_PATH, PropertyAuthority.None));
    }

    @Override
    public File resolveRebar3() throws DetectableException {
        return resolveExecutable("rebar3", simpleExecutableResolver::resolveRebar3, detectConfiguration.getProperty(DetectProperty.DETECT_HEX_REBAR3_PATH, PropertyAuthority.None));
    }

    @Override
    public File resolveYarn() throws DetectableException {
        return resolveExecutable("yarn", simpleExecutableResolver::resolveYarn, detectConfiguration.getProperty(DetectProperty.DETECT_YARN_PATH, PropertyAuthority.None));
    }

    @Override
    public File resolveJava() throws DetectableException {
        return resolveExecutable("java", simpleExecutableResolver::resolveJava, detectConfiguration.getProperty(DetectProperty.DETECT_JAVA_PATH, PropertyAuthority.None));
    }

    @Override
    public File resolveDocker() throws DetectableException {
        return resolveExecutable("docker", simpleExecutableResolver::resolveDocker, detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_PATH, PropertyAuthority.None));
    }

    @Override
    public File resolveDotNet() throws DetectableException {
        return resolveExecutable("dotnet", simpleExecutableResolver::resolveDotNet, detectConfiguration.getProperty(DetectProperty.DETECT_DOTNET_PATH, PropertyAuthority.None));
    }

    @Override
    public File resolveGit() throws DetectableException {
        return resolveExecutable("git", simpleExecutableResolver::resolveGit, detectConfiguration.getProperty(DetectProperty.DETECT_GIT_PATH, PropertyAuthority.None));
    }

    @Override
    public File resolveSwift() throws DetectableException {
        return resolveExecutable("swift", simpleExecutableResolver::resolveSwift, detectConfiguration.getProperty(DetectProperty.DETECT_SWIFT_PATH, PropertyAuthority.None));
    }

    @Override
    public File resolveGo() throws DetectableException {
        return resolveExecutable("go", simpleExecutableResolver::resolveSwift, detectConfiguration.getProperty(DetectProperty.DETECT_GO_PATH, PropertyAuthority.None));
    }
}
