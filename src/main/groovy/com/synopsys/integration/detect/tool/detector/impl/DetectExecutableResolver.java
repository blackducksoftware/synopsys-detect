/**
 * synopsys-detect
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
package com.synopsys.integration.detect.tool.detector.impl;

import java.io.File;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.BashResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.BazelResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CondaResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CpanResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CpanmResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.DockerResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.DotNetResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.GradleResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.JavaResolver;
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

// TODO: Implement this class. Will this do anything different from SimpleExecutableResolver?
public class DetectExecutableResolver
    implements JavaResolver, GradleResolver, BashResolver, CondaResolver, CpanmResolver, CpanResolver, PearResolver, Rebar3Resolver, YarnResolver, PythonResolver, PipResolver, PipenvResolver, MavenResolver, NpmResolver,
                   BazelResolver, DockerResolver, GoDepResolver, GoResolver, DotNetResolver {
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

    @Override
    public File resolveJava() throws DetectableException {
        return null;
    }

    @Override
    public File resolveDocker() throws DetectableException {
        return null;
    }

    @Override
    public File resolveDotNet() throws DetectableException {
        return null;
    }
}
