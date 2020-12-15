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
package com.synopsys.integration.detect.tool.detector.executable;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.BashResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.BazelResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CondaResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CpanResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CpanmResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.DockerResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.DotNetResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.GitResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.GoResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.GradleResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.JavaResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.LernaResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.MavenResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.NpmResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PearResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PipResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PipenvResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PythonResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.Rebar3Resolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.SwiftResolver;
import com.synopsys.integration.detectable.detectables.conan.cli.ConanResolver;

public class DetectExecutableResolver
    implements JavaResolver, GradleResolver, BashResolver, ConanResolver, CondaResolver, CpanmResolver, CpanResolver, PearResolver, Rebar3Resolver, PythonResolver, PipResolver, PipenvResolver, MavenResolver, NpmResolver, BazelResolver,
                   DockerResolver,
                   DotNetResolver, GitResolver, SwiftResolver, GoResolver, LernaResolver {

    private final DirectoryExecutableFinder directoryExecutableFinder;
    private final SystemPathExecutableFinder systemPathExecutableFinder;
    private final DetectExecutableOptions detectExecutableOptions;

    private final Map<String, File> cachedExecutables = new HashMap<>();

    public DetectExecutableResolver(DirectoryExecutableFinder directoryExecutableFinder, SystemPathExecutableFinder systemPathExecutableFinder,
        DetectExecutableOptions detectExecutableOptions) {
        this.directoryExecutableFinder = directoryExecutableFinder;
        this.systemPathExecutableFinder = systemPathExecutableFinder;
        this.detectExecutableOptions = detectExecutableOptions;
    }

    private File resolve(@Nullable String cacheKey, ExecutableResolverFunction... resolvers) throws DetectableException {
        File resolved = null;
        for (ExecutableResolverFunction resolver : resolvers) {
            resolved = resolver.resolve();
            if (resolved != null)
                break;
        }
        if (cacheKey != null) {
            cachedExecutables.put(cacheKey, resolved);
        }
        return resolved;
    }

    private File resolveCache(String cacheKey) {
        if (cachedExecutables.containsKey(cacheKey)) {
            return cachedExecutables.get(cacheKey);
        }
        return null;
    }

    private File resolveOverride(Path executableOverride) throws DetectableException {
        if (executableOverride != null) {
            File exe = executableOverride.toFile();
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
        return null;
    }

    private File resolveCachedSystemExecutable(String executableName, Path override) throws DetectableException {
        return resolveCachedSystemExecutable(executableName, executableName, override); //executableName is the cache key
    }

    private File resolveCachedSystemExecutable(String cacheKey, String executableName, Path override) throws DetectableException {
        return resolve(cacheKey,
            () -> resolveOverride(override),
            () -> resolveCache(cacheKey),
            () -> systemPathExecutableFinder.findExecutable(executableName));
    }

    private File resolveLocalNonCachedExecutable(String localName, String systemName, DetectableEnvironment environment, Path override) throws DetectableException {
        return resolve(/* not cached */ null,
            () -> resolveOverride(override),
            () -> directoryExecutableFinder.findExecutable(localName, environment.getDirectory()),
            () -> systemPathExecutableFinder.findExecutable(systemName));
    }

    @Override
    public File resolveBash() throws DetectableException {
        return resolveCachedSystemExecutable("bash", detectExecutableOptions.getBashUserPath());
    }

    @Override
    public File resolveBazel() throws DetectableException {
        return resolveCachedSystemExecutable("bazel", detectExecutableOptions.getBazelUserPath());
    }

    @Override
    public File resolveConda() throws DetectableException {
        return resolveCachedSystemExecutable("conda", detectExecutableOptions.getCondaUserPath());
    }

    @Override
    public File resolveCpan() throws DetectableException {
        return resolveCachedSystemExecutable("cpan", detectExecutableOptions.getCpanUserPath());
    }

    @Override
    public File resolveCpanm() throws DetectableException {
        return resolveCachedSystemExecutable("cpanm", detectExecutableOptions.getCpanmUserPath());
    }

    @Override
    public File resolveGradle(DetectableEnvironment environment) throws DetectableException {
        return resolveLocalNonCachedExecutable("gradlew", "gradle", environment, detectExecutableOptions.getGradleUserPath());
    }

    @Override
    public File resolveMaven(DetectableEnvironment environment) throws DetectableException {
        return resolveLocalNonCachedExecutable("mvnw", "mvn", environment, detectExecutableOptions.getMavenUserPath());
    }

    @Override
    public File resolveNpm(DetectableEnvironment environment) throws DetectableException {
        return resolveLocalNonCachedExecutable("npm", "npm", environment, detectExecutableOptions.getNpmUserPath());
    }

    @Override
    public File resolvePear() throws DetectableException {
        return resolveCachedSystemExecutable("pear", detectExecutableOptions.getPearUserPath());
    }

    @Override
    public File resolvePip() throws DetectableException {
        return resolveCachedSystemExecutable(detectExecutableOptions.isPython3() ? "pip3" : "pip", detectExecutableOptions.getPipUserPath());
    }

    @Override
    public File resolvePipenv() throws DetectableException {
        return resolveCachedSystemExecutable("pipenv", detectExecutableOptions.getPipenvUserPath());
    }

    @Override
    public File resolvePython() throws DetectableException {
        return resolveCachedSystemExecutable(detectExecutableOptions.isPython3() ? "python3" : "python", detectExecutableOptions.getPythonUserPath());
    }

    @Override
    public File resolveRebar3() throws DetectableException {
        return resolveCachedSystemExecutable("rebar3", detectExecutableOptions.getRebarUserPath());
    }

    @Override
    public File resolveJava() throws DetectableException {
        return resolveCachedSystemExecutable("java", detectExecutableOptions.getJavaUserPath());
    }

    @Override
    public File resolveDocker() throws DetectableException {
        return resolveCachedSystemExecutable("docker", detectExecutableOptions.getDockerUserPath());
    }

    @Override
    public File resolveDotNet() throws DetectableException {
        return resolveCachedSystemExecutable("dotnet", detectExecutableOptions.getDotnetUserPath());
    }

    @Override
    public ExecutableTarget resolveGit() throws DetectableException {
        return ExecutableTarget.forFile(resolveCachedSystemExecutable("git", detectExecutableOptions.getGitUserPath()));
    }

    @Override
    public File resolveSwift() throws DetectableException {
        return resolveCachedSystemExecutable("swift", detectExecutableOptions.getSwiftUserPath());
    }

    @Override
    public File resolveGo() throws DetectableException {
        return resolveCachedSystemExecutable("go", detectExecutableOptions.getGoUserPath());
    }

    @Override
    public File resolveLerna() throws DetectableException {
        return resolveCachedSystemExecutable("lerna", detectExecutableOptions.getLernaUserPath());
    }

    @Override
    public File resolveConan(DetectableEnvironment environment) throws DetectableException {
        return resolveCachedSystemExecutable("conan", detectExecutableOptions.getConanUserPath());
    }
}

