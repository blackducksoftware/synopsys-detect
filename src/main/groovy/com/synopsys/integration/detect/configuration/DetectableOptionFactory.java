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
package com.synopsys.integration.detect.configuration;

import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorOptions;
import com.synopsys.integration.detectable.detectables.bazel.BazelDetectableOptions;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectableOptions;
import com.synopsys.integration.detectable.detectables.clang.ClangDetectableOptions;
import com.synopsys.integration.detectable.detectables.conda.CondaCliDetectableOptions;
import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleInspectorOptions;
import com.synopsys.integration.detectable.detectables.gradle.inspection.inspector.GradleInspectorScriptOptions;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenCliExtractorOptions;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliExtractorOptions;
import com.synopsys.integration.detectable.detectables.packagist.ComposerLockDetectableOptions;
import com.synopsys.integration.detectable.detectables.pear.PearCliDetectableOptions;
import com.synopsys.integration.detectable.detectables.pip.PipInspectorDetectableOptions;
import com.synopsys.integration.detectable.detectables.pip.PipenvDetectableOptions;
import com.synopsys.integration.detectable.detectables.sbt.SbtResolutionCacheDetectableOptions;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockOptions;

public class DetectableOptionFactory {
    private DetectConfiguration detectConfiguration;

    public DetectableOptionFactory(final DetectConfiguration detectConfiguration) {this.detectConfiguration = detectConfiguration;}

    public CondaCliDetectableOptions createCondaOptions() {
        return new CondaCliDetectableOptions("");
    }

    public MavenCliExtractorOptions createMavenCliOptions() {
        return new MavenCliExtractorOptions("", "", "" , "");
    }

    public NpmCliExtractorOptions createNpmCliExtractorOptions() {
        return new NpmCliExtractorOptions(false, "");
    }

    public ComposerLockDetectableOptions createComposerLockDetectableOptions() {
        return new ComposerLockDetectableOptions(false);
    }

    public PearCliDetectableOptions createPearCliDetectableOptions() {//TODO: this is actually used by a transformer. feels wrong.
        return new PearCliDetectableOptions(false);
    }

    public PipenvDetectableOptions createPipenvDetectableOptions() {//TODO: this is actually used by a transformer. feels wrong.
        return new PipenvDetectableOptions("", "");
    }

    public PipInspectorDetectableOptions createPipInspectorDetectableOptions(){
        return new PipInspectorDetectableOptions("");
    }

    public SbtResolutionCacheDetectableOptions createSbtResolutionCacheDetectableOptions(){
        return new SbtResolutionCacheDetectableOptions("", "", 0);
    }

    public YarnLockOptions createYarnLockOptions() {
        return new YarnLockOptions(false);
    }

    public BazelDetectableOptions createBazelDetectableOptions(){
        return new BazelDetectableOptions("", "");
    }

    public ClangDetectableOptions createClangDetectableOptions(){
        return new ClangDetectableOptions(false, 0);
    }

    public GradleInspectorOptions createGradleInspectorOptions(){
        GradleInspectorScriptOptions scriptOptions = new GradleInspectorScriptOptions("","","","","","","");
        return new GradleInspectorOptions("", scriptOptions);
    }

    public NugetInspectorOptions createNugetInspectorOptions(){
        return null;
    }

    public BitbakeDetectableOptions createBitbakeDetectableOptions() {
    //    String buildEnvName = detectConfiguration.getProperty(DetectProperty.DETECT_BITBAKE_BUILD_ENV_NAME, PropertyAuthority.None);
    //    String[] bitbakePackageNames = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_BITBAKE_PACKAGE_NAMES, PropertyAuthority.None);
        return new BitbakeDetectableOptions("", new String[2]);
    }

}
