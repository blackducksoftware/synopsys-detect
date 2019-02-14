/**
 * detectable
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.synopsys.integration.detectable.detectable.executable.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.BashResolver;
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

//this will cache the find result.
public class SimpleExecutableResolver implements GradleResolver, BashResolver, CondaResolver, CpanmResolver, CpanResolver, PearResolver, Rebar3Resolver, YarnResolver, PythonResolver, PipResolver, PipenvResolver, MavenResolver, NpmResolver {
    private final CachedExecutableResolverOptions executableResolverOptions;
    private final SimpleLocalExecutableFinder localExecutableFinder;
    private final SimpleSystemExecutableFinder systemExecutableFinder;

    private final Map<String, File> cached = new HashMap<>();

    public SimpleExecutableResolver(final CachedExecutableResolverOptions executableResolverOptions, final SimpleLocalExecutableFinder localExecutableFinder, final SimpleSystemExecutableFinder systemExecutableFinder) {
        this.executableResolverOptions = executableResolverOptions;
        this.localExecutableFinder = localExecutableFinder;
        this.systemExecutableFinder = systemExecutableFinder;
    }

    private File findCachedSystem(final String name) {
        if (!cached.containsKey(name)) {
            final File found = systemExecutableFinder.findExecutable(name);
            cached.put(name, found);
        }

        return cached.get(name);
    }

    private File findLocalOrSystem(final String localName, final String systemName, final DetectableEnvironment environment) {
        final File local = localExecutableFinder.findExecutable(localName, environment.getDirectory());
        if (local != null) {
            return local;
        } else {
            return findCachedSystem(systemName);
        }
    }

    private File findLocalOrSystem(final String name, final DetectableEnvironment environment) {
        return findLocalOrSystem(name, name, environment);
    }

    @Override
    public File resolveGradle(final DetectableEnvironment environment) {
        return findLocalOrSystem("gradlew", "gradle", environment);
    }

    @Override
    public File resolveBash() {
        return findCachedSystem("bash");
    }

    @Override
    public File resolveConda() {
        return findCachedSystem("conda");
    }

    @Override
    public File resolveCpan() {
        return findCachedSystem("cpan");
    }

    @Override
    public File resolvePear() {
        return findCachedSystem("pear");
    }

    @Override
    public File resolveRebar3() {
        return findCachedSystem("rebar3");
    }

    @Override
    public File resolveYarn() {
        return findCachedSystem("yarn");
    }

    @Override
    public File resolvePip() {
        final String suffix = executableResolverOptions.python3 ? "3" : "";
        return findCachedSystem("python" + suffix);
    }

    @Override
    public File resolvePython() {
        final String suffix = executableResolverOptions.python3 ? "3" : "";
        return findCachedSystem("pip" + suffix);
    }

    @Override
    public File resolvePipenv() {
        return findCachedSystem("pipenv");
    }

    @Override
    public File resolveMaven(final DetectableEnvironment environment) {
        return findLocalOrSystem("mvnw", "mvn", environment);
    }

    @Override
    public File resolveNpm(final DetectableEnvironment environment) {
        return findCachedSystem("npm");
    }
}
