/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.bitbake.BitbakeBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.ClangBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.cocoapods.PodlockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.conda.CondaCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.cpan.CpanCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.cran.PackratLockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.docker.DockerBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.docker.DockerBomToolOptions;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoLockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoVndrBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleInspectorBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.hex.RebarBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenPomBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenPomWrapperBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmPackageLockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmShrinkwrapBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetProjectBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetSolutionBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.packagist.ComposerLockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.pear.PearCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipInspectorBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipenvBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.rubygems.GemlockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.SbtResolutionCacheBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.yarn.YarnLockBomTool;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;

public class BomToolFactory implements BeanFactoryAware {

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public BitbakeBomTool createBitbakeBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(BitbakeBomTool.class, environment);
    }

    public ClangBomTool createClangBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(ClangBomTool.class, environment);
    }

    public ComposerLockBomTool createComposerLockBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(ComposerLockBomTool.class, environment);
    }

    public CondaCliBomTool createCondaBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(CondaCliBomTool.class, environment);
    }

    public CpanCliBomTool createCpanCliBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(CpanCliBomTool.class, environment);
    }

    public DockerBomTool createDockerBomTool(final BomToolEnvironment environment) {
        DetectConfiguration detectConfiguration = beanFactory.getBean(DetectConfiguration.class);
        final String tar = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_TAR);
        final String image = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_IMAGE);
        final boolean dockerRequired = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_DOCKER_PATH_REQUIRED);
        DockerBomToolOptions options = new DockerBomToolOptions(dockerRequired, image, tar);

        return beanFactory.getBean(DockerBomTool.class, environment, options);
    }

    public GemlockBomTool createGemlockBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(GemlockBomTool.class, environment);
    }

    public GoCliBomTool createGoCliBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(GoCliBomTool.class, environment);
    }

    public GoLockBomTool createGoLockBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(GoLockBomTool.class, environment);
    }

    public GoVndrBomTool createGoVndrBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(GoVndrBomTool.class, environment);
    }

    public GradleInspectorBomTool createGradleInspectorBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(GradleInspectorBomTool.class, environment);
    }

    public MavenPomBomTool createMavenPomBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(MavenPomBomTool.class, environment);
    }

    public MavenPomWrapperBomTool createMavenPomWrapperBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(MavenPomWrapperBomTool.class, environment);
    }

    public NpmCliBomTool createNpmCliBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(NpmCliBomTool.class, environment);
    }

    public NpmPackageLockBomTool createNpmPackageLockBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(NpmPackageLockBomTool.class, environment);
    }

    public NugetProjectBomTool createNugetProjectBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(NugetProjectBomTool.class, environment);
    }

    public NpmShrinkwrapBomTool createNpmShrinkwrapBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(NpmShrinkwrapBomTool.class, environment);
    }

    public NugetSolutionBomTool createNugetSolutionBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(NugetSolutionBomTool.class, environment);
    }

    public PackratLockBomTool createPackratLockBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(PackratLockBomTool.class, environment);
    }

    public PearCliBomTool createPearCliBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(PearCliBomTool.class, environment);
    }

    public PipenvBomTool createPipenvBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(PipenvBomTool.class, environment);
    }

    public PipInspectorBomTool createPipInspectorBomTool(final BomToolEnvironment environment) {
        //final String requirementsFile = detectConfiguration.getProperty(DetectProperty.DETECT_PIP_REQUIREMENTS_PATH);
        return beanFactory.getBean(PipInspectorBomTool.class, environment);
    }

    public PodlockBomTool createPodLockBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(PodlockBomTool.class, environment);
    }

    public RebarBomTool createRebarBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(RebarBomTool.class, environment);
    }

    public SbtResolutionCacheBomTool createSbtResolutionCacheBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(SbtResolutionCacheBomTool.class, environment);
    }

    public YarnLockBomTool createYarnLockBomTool(final BomToolEnvironment environment) {
        return beanFactory.getBean(YarnLockBomTool.class, environment);
    }

}
