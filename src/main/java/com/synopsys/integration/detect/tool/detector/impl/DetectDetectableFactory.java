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

import com.synopsys.integration.detect.configuration.DetectableOptionFactory;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.inspector.GradleInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.PipInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;
import com.synopsys.integration.detectable.detectables.bazel.BazelDetectable;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectable;
import com.synopsys.integration.detectable.detectables.clang.ClangDetectable;
import com.synopsys.integration.detectable.detectables.cocoapods.PodlockDetectable;
import com.synopsys.integration.detectable.detectables.conda.CondaCliDetectable;
import com.synopsys.integration.detectable.detectables.cpan.CpanCliDetectable;
import com.synopsys.integration.detectable.detectables.cran.PackratLockDetectable;
import com.synopsys.integration.detectable.detectables.docker.DockerDetectable;
import com.synopsys.integration.detectable.detectables.docker.DockerInspectorResolver;
import com.synopsys.integration.detectable.detectables.git.cli.GitCliDetectable;
import com.synopsys.integration.detectable.detectables.git.parsing.GitParseDetectable;
import com.synopsys.integration.detectable.detectables.go.godep.GoDepLockDetectable;
import com.synopsys.integration.detectable.detectables.go.gogradle.GoGradleDetectable;
import com.synopsys.integration.detectable.detectables.go.gomod.GoModCliDetectable;
import com.synopsys.integration.detectable.detectables.go.vendor.GoVendorDetectable;
import com.synopsys.integration.detectable.detectables.go.vendr.GoVndrDetectable;
import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleDetectable;
import com.synopsys.integration.detectable.detectables.gradle.parsing.GradleParseDetectable;
import com.synopsys.integration.detectable.detectables.lerna.LernaDetectable;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenPomDetectable;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenPomWrapperDetectable;
import com.synopsys.integration.detectable.detectables.maven.parsing.MavenParseDetectable;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliDetectable;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmPackageLockDetectable;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmShrinkwrapDetectable;
import com.synopsys.integration.detectable.detectables.npm.packagejson.NpmPackageJsonParseDetectable;
import com.synopsys.integration.detectable.detectables.nuget.NugetProjectDetectable;
import com.synopsys.integration.detectable.detectables.nuget.NugetSolutionDetectable;
import com.synopsys.integration.detectable.detectables.packagist.ComposerLockDetectable;
import com.synopsys.integration.detectable.detectables.pear.PearCliDetectable;
import com.synopsys.integration.detectable.detectables.pip.PipInspectorDetectable;
import com.synopsys.integration.detectable.detectables.pip.PipenvDetectable;
import com.synopsys.integration.detectable.detectables.rebar.RebarDetectable;
import com.synopsys.integration.detectable.detectables.rubygems.gemlock.GemlockDetectable;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.GemspecParseDetectable;
import com.synopsys.integration.detectable.detectables.sbt.SbtResolutionCacheDetectable;
import com.synopsys.integration.detectable.detectables.swift.SwiftCliDetectable;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockDetectable;
import com.synopsys.integration.detectable.factory.DetectableFactory;

public class DetectDetectableFactory {
    private final DetectableFactory detectableFactory;
    private final DetectableOptionFactory detectableOptionFactory;
    private final DetectExecutableResolver detectExecutableResolver;

    private final DockerInspectorResolver dockerInspectorResolver;
    private final GradleInspectorResolver gradleInspectorResolver;
    private final NugetInspectorResolver nugetInspectorResolver;
    private final PipInspectorResolver pipInspectorResolver;

    public DetectDetectableFactory(final DetectableFactory detectableFactory, final DetectableOptionFactory detectableOptionFactory, final DetectExecutableResolver detectExecutableResolver,
        final DockerInspectorResolver dockerInspectorResolver, final GradleInspectorResolver gradleInspectorResolver, final NugetInspectorResolver nugetInspectorResolver,
        final PipInspectorResolver pipInspectorResolver) {
        this.detectableFactory = detectableFactory;
        this.detectableOptionFactory = detectableOptionFactory;
        this.detectExecutableResolver = detectExecutableResolver;
        this.dockerInspectorResolver = dockerInspectorResolver;
        this.gradleInspectorResolver = gradleInspectorResolver;
        this.nugetInspectorResolver = nugetInspectorResolver;
        this.pipInspectorResolver = pipInspectorResolver;
    }

    public DockerDetectable createDockerDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createDockerDetectable(environment, detectableOptionFactory.createDockerDetectableOptions(), dockerInspectorResolver, detectExecutableResolver, detectExecutableResolver, detectExecutableResolver);
    }

    public BazelDetectable createBazelDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createBazelDetectable(environment, detectableOptionFactory.createBazelDetectableOptions(), detectExecutableResolver);
    }

    public BitbakeDetectable createBitbakeDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createBitbakeDetectable(environment, detectableOptionFactory.createBitbakeDetectableOptions(), detectExecutableResolver);
    }

    public ClangDetectable createClangDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createClangDetectable(environment, detectableOptionFactory.createClangDetectableOptions());
    }

    public ComposerLockDetectable createComposerDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createComposerDetectable(environment, detectableOptionFactory.createComposerLockDetectableOptions());
    }

    public CondaCliDetectable createCondaCliDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createCondaCliDetectable(environment, detectExecutableResolver, detectableOptionFactory.createCondaOptions());
    }

    public CpanCliDetectable createCpanCliDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createCpanCliDetectable(environment, detectExecutableResolver, detectExecutableResolver);
    }

    public GemlockDetectable createGemlockDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createGemlockDetectable(environment);
    }

    public GitParseDetectable createGitParseDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createGitParseDetectable(environment);
    }

    public GitCliDetectable createGitCliDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createGitCliDetectable(environment, detectExecutableResolver);
    }

    public GoModCliDetectable createGoModCliDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createGoModCliDetectable(environment, detectExecutableResolver);
    }

    public GoDepLockDetectable createGoLockDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createGoLockDetectable(environment);
    }

    public GoVndrDetectable createGoVndrDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createGoVndrDetectable(environment);
    }

    public GoVendorDetectable createGoVendorDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createGoVendorDetectable(environment);
    }

    public GoGradleDetectable createGoGradleDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createGoGradleDetectable(environment);
    }

    public GradleDetectable createGradleDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createGradleDetectable(environment, detectableOptionFactory.createGradleInspectorOptions(), gradleInspectorResolver, detectExecutableResolver);
    }

    public GradleParseDetectable createGradleParseDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createGradleParseDetectable(environment);
    }

    public GemspecParseDetectable createGemspecParseDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createGemspecParseDetectable(environment, detectableOptionFactory.createGemspecParseDetectableOptions());
    }

    public MavenPomDetectable createMavenPomDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createMavenPomDetectable(environment, detectExecutableResolver, detectableOptionFactory.createMavenCliOptions());
    }

    public MavenPomWrapperDetectable createMavenPomWrapperDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createMavenPomWrapperDetectable(environment, detectExecutableResolver, detectableOptionFactory.createMavenCliOptions());
    }

    public MavenParseDetectable createMavenParseDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createMavenParseDetectable(environment, detectableOptionFactory.createMavenParseOptions());
    }

    public NpmCliDetectable createNpmCliDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createNpmCliDetectable(environment, detectExecutableResolver, detectableOptionFactory.createNpmCliExtractorOptions());
    }

    public NpmPackageLockDetectable createNpmPackageLockDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createNpmPackageLockDetectable(environment, detectableOptionFactory.createNpmLockfileOptions());
    }

    public NugetProjectDetectable createNugetProjectDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createNugetProjectDetectable(environment, detectableOptionFactory.createNugetInspectorOptions(), nugetInspectorResolver);
    }

    public NpmShrinkwrapDetectable createNpmShrinkwrapDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createNpmShrinkwrapDetectable(environment, detectableOptionFactory.createNpmLockfileOptions());
    }

    public NpmPackageJsonParseDetectable createNpmPackageJsonParseDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createNpmPackageJsonParseDetectable(environment, detectableOptionFactory.createNpmPackageJsonParseDetectableOptions());
    }

    public NugetSolutionDetectable createNugetSolutionDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createNugetSolutionDetectable(environment, detectableOptionFactory.createNugetInspectorOptions(), nugetInspectorResolver);
    }

    public PackratLockDetectable createPackratLockDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createPackratLockDetectable(environment);
    }

    public PearCliDetectable createPearCliDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createPearCliDetectable(environment, detectableOptionFactory.createPearCliDetectableOptions(), detectExecutableResolver);
    }

    public PipenvDetectable createPipenvDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createPipenvDetectable(environment, detectableOptionFactory.createPipenvDetectableOptions(), detectExecutableResolver, detectExecutableResolver);
    }

    public PipInspectorDetectable createPipInspectorDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createPipInspectorDetectable(environment, detectableOptionFactory.createPipInspectorDetectableOptions(), pipInspectorResolver, detectExecutableResolver, detectExecutableResolver);
    }

    public PodlockDetectable createPodLockDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createPodLockDetectable(environment);
    }

    public RebarDetectable createRebarDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createRebarDetectable(environment, detectExecutableResolver);
    }

    public SbtResolutionCacheDetectable createSbtResolutionCacheDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createSbtResolutionCacheDetectable(environment, detectableOptionFactory.createSbtResolutionCacheDetectableOptions());
    }

    public SwiftCliDetectable createSwiftCliDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createSwiftCliDetectable(environment, detectExecutableResolver);
    }

    public YarnLockDetectable createYarnLockDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createYarnLockDetectable(environment, detectableOptionFactory.createYarnLockOptions());
    }

    public LernaDetectable createLernaDetectable(final DetectableEnvironment environment) {
        return detectableFactory.createLernaDetectable(environment, detectExecutableResolver, detectableOptionFactory.createYarnLockOptions(), detectableOptionFactory.createNpmLockfileOptions());
    }
}
