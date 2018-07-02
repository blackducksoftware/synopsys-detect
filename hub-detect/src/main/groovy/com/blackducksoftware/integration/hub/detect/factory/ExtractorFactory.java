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

import com.blackducksoftware.integration.hub.detect.bomtool.cocoapods.PodlockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.conda.CondaCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.cpan.CpanCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.cran.PackratLockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.docker.DockerExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoDepExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoVndrExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.hex.RebarExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmLockfileExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.packagist.ComposerLockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.pear.PearCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipenvExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.rubygems.GemlockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.SbtResolutionCacheExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.yarn.YarnLockExtractor;

public class ExtractorFactory {

    public PodlockExtractor createPodlockExtractor() {
        return null;
    }

    public CondaCliExtractor createCondaCliExtractor() {
        return null;
    }

    public CpanCliExtractor createCpanCliExtractor() {
        return null;
    }

    public PackratLockExtractor createPackratLockExtractor() {
        return null;
    }

    public DockerExtractor createDockerExtractor() {
        return null;
    }

    public GoVndrExtractor createGoVndrExtractor() {
        return null;
    }

    public GoDepExtractor createGoDepExtractor() {
        return null;
    }

    public GradleInspectorExtractor createGradleInspectorExtractor() {
        return null;
    }

    public RebarExtractor createRebarExtractor() {
        return null;
    }

    public MavenCliExtractor createMavenCliExtractor() {
        return null;
    }

    public NpmCliExtractor createNpmCliExtractor() {
        return null;
    }

    public NpmLockfileExtractor createNpmLockfileExtractor() {
        return null;
    }

    public NugetInspectorExtractor createNugetInspectorExtractor() {
        return null;
    }

    public ComposerLockExtractor createComposerLockExtractor() {
        return null;
    }

    public PearCliExtractor createPearCliExtractor() {
        return null;
    }

    public PipenvExtractor createPipenvExtractor() {
        return null;
    }

    public PipInspectorExtractor createPipInspectorExtractor() {
        return null;
    }

    public GemlockExtractor createGemlockExtractor() {
        return null;
    }

    public SbtResolutionCacheExtractor createSbtResolutionCacheExtractor() {
        return null;
    }

    public YarnLockExtractor createYarnLockExtractor() {
        return null;
    }
}
