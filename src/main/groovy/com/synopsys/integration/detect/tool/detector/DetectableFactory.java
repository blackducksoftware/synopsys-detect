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
package com.synopsys.integration.detect.tool.detector;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectables.bazel.BazelDetectable;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectable;
import com.synopsys.integration.detectable.detectables.clang.ClangDetectable;
import com.synopsys.integration.detectable.detectables.cocoapods.PodlockDetectable;
import com.synopsys.integration.detectable.detectables.conda.CondaCliDetectable;
import com.synopsys.integration.detectable.detectables.cpan.CpanCliDetectable;
import com.synopsys.integration.detectable.detectables.cran.PackratLockDetectable;
import com.synopsys.integration.detectable.detectables.docker.DockerDetectable;
import com.synopsys.integration.detectable.detectables.go.godep.GoDepCliDetectable;
import com.synopsys.integration.detectable.detectables.go.godep.GoDepLockDetectable;
import com.synopsys.integration.detectable.detectables.go.vendor.GoVendorDetectable;
import com.synopsys.integration.detectable.detectables.go.vendr.GoVndrDetectable;
import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleInspectorDetectable;
import com.synopsys.integration.detectable.detectables.gradle.parsing.GradleParseDetectable;
import com.synopsys.integration.detectable.detectables.hex.RebarDetectable;
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
import com.synopsys.integration.detectable.detectables.rubygems.gemlock.GemlockDetectable;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.GemspecParseDetectable;
import com.synopsys.integration.detectable.detectables.sbt.SbtResolutionCacheDetectable;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockDetectable;

public class DetectableFactory implements BeanFactoryAware {

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public BazelDetectable createBazelDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(BazelDetectable.class, environment);
    }

    public DockerDetectable createDockerDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(DockerDetectable.class, environment);
    }

    public BitbakeDetectable createBitbakeDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(BitbakeDetectable.class, environment);
    }

    public ClangDetectable createClangDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(ClangDetectable.class, environment);
    }

    public ComposerLockDetectable createComposerLockDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(ComposerLockDetectable.class, environment);
    }

    public CondaCliDetectable createCondaDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(CondaCliDetectable.class, environment);
    }

    public CpanCliDetectable createCpanCliDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(CpanCliDetectable.class, environment);
    }

    public GemlockDetectable createGemlockDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(GemlockDetectable.class, environment);
    }

    public GemspecParseDetectable createGemspecParseDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(GemspecParseDetectable.class, environment);
    }

    public GoDepCliDetectable createGoCliDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(GoDepCliDetectable.class, environment);
    }

    public GoDepLockDetectable createGoLockDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(GoDepLockDetectable.class, environment);
    }

    public GoVndrDetectable createGoVndrDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(GoVndrDetectable.class, environment);
    }

    public GoVendorDetectable createGoVendorDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(GoVendorDetectable.class, environment);
    }

    public GradleParseDetectable createGradleParseDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(GradleParseDetectable.class, environment);
    }

    public GradleInspectorDetectable createGradleInspectorDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(GradleInspectorDetectable.class, environment);
    }

    public MavenPomDetectable createMavenPomDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(MavenPomDetectable.class, environment);
    }

    public MavenPomWrapperDetectable createMavenPomWrapperDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(MavenPomWrapperDetectable.class, environment);
    }

    public MavenParseDetectable createMavenParseDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(MavenParseDetectable.class, environment);
    }

    public NpmCliDetectable createNpmCliDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(NpmCliDetectable.class, environment);
    }

    public NpmPackageJsonParseDetectable createNpmPackageJsonParseDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(NpmPackageJsonParseDetectable.class, environment);
    }

    public NpmPackageLockDetectable createNpmPackageLockDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(NpmPackageLockDetectable.class, environment);
    }

    public NugetProjectDetectable createNugetProjectDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(NugetProjectDetectable.class, environment);
    }

    public NpmShrinkwrapDetectable createNpmShrinkwrapDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(NpmShrinkwrapDetectable.class, environment);
    }

    public NugetSolutionDetectable createNugetSolutionDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(NugetSolutionDetectable.class, environment);
    }

    public PackratLockDetectable createPackratLockDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(PackratLockDetectable.class, environment);
    }

    public PearCliDetectable createPearCliDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(PearCliDetectable.class, environment);
    }

    public PipenvDetectable createPipenvDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(PipenvDetectable.class, environment);
    }

    public PipInspectorDetectable createPipInspectorDetectable(final DetectableEnvironment environment) {
        //final String requirementsFile = detectConfiguration.getProperty(DetectProperty.DETECT_PIP_REQUIREMENTS_PATH, PropertyAuthority.None);
        return beanFactory.getBean(PipInspectorDetectable.class, environment);
    }

    public PodlockDetectable createPodLockDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(PodlockDetectable.class, environment);
    }

    public RebarDetectable createRebarDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(RebarDetectable.class, environment);
    }

    public SbtResolutionCacheDetectable createSbtResolutionCacheDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(SbtResolutionCacheDetectable.class, environment);
    }

    public YarnLockDetectable createYarnLockDetectable(final DetectableEnvironment environment) {
        return beanFactory.getBean(YarnLockDetectable.class, environment);
    }

}