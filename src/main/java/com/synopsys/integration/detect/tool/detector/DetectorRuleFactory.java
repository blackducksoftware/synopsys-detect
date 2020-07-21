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
package com.synopsys.integration.detect.tool.detector;

import com.synopsys.integration.detect.tool.detector.impl.DetectDetectableFactory;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectable;
import com.synopsys.integration.detectable.detectables.cargo.CargoDetectable;
import com.synopsys.integration.detectable.detectables.clang.ClangDetectable;
import com.synopsys.integration.detectable.detectables.cocoapods.PodlockDetectable;
import com.synopsys.integration.detectable.detectables.conda.CondaCliDetectable;
import com.synopsys.integration.detectable.detectables.cpan.CpanCliDetectable;
import com.synopsys.integration.detectable.detectables.cran.PackratLockDetectable;
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
import com.synopsys.integration.detectable.detectables.pip.poetry.PoetryDetectable;
import com.synopsys.integration.detectable.detectables.rebar.RebarDetectable;
import com.synopsys.integration.detectable.detectables.rubygems.gemlock.GemlockDetectable;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.GemspecParseDetectable;
import com.synopsys.integration.detectable.detectables.sbt.SbtResolutionCacheDetectable;
import com.synopsys.integration.detectable.detectables.swift.SwiftCliDetectable;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockDetectable;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;
import com.synopsys.integration.detector.rule.DetectorRuleSetBuilder;

public class DetectorRuleFactory {
    public DetectorRuleSet createRules(final DetectDetectableFactory detectableFactory, final boolean buildless) {
        if (buildless) {
            return createBuildlessRules(detectableFactory);
        } else {
            return createRules(detectableFactory);
        }
    }

    //TODO: It would just be nice not to have to call 'build' after each of the addDetectors.
    private DetectorRuleSet createRules(final DetectDetectableFactory detectableFactory) {
        final DetectorRuleSetBuilder ruleSet = new DetectorRuleSetBuilder();

        //TODO: Verify we still need to pass detector name here. We may now be able to get it from the detectable class - before we could not as it was not instantiated.
        ruleSet.addDetector(DetectorType.CARGO, "Cargo", CargoDetectable.class, detectableFactory::createCargoDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.BITBAKE, "Bitbake", BitbakeDetectable.class, detectableFactory::createBitbakeDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.COCOAPODS, "Pod Lock", PodlockDetectable.class, detectableFactory::createPodLockDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.CONDA, "Conda Cli", CondaCliDetectable.class, detectableFactory::createCondaCliDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.CPAN, "Cpan Cli", CpanCliDetectable.class, detectableFactory::createCpanCliDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.CRAN, "Packrat Lock", PackratLockDetectable.class, detectableFactory::createPackratLockDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.GO_MOD, "Go Mod Cli", GoModCliDetectable.class, detectableFactory::createGoModCliDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.GO_GRADLE, "Go Gradle", GoGradleDetectable.class, detectableFactory::createGoGradleDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.GO_DEP, "Go Lock", GoDepLockDetectable.class, detectableFactory::createGoLockDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.GO_VNDR, "Go Vndr", GoVndrDetectable.class, detectableFactory::createGoVndrDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.GO_VENDOR, "Go Vendor", GoVendorDetectable.class, detectableFactory::createGoVendorDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.GRADLE, "Gradle Inspector", GradleDetectable.class, detectableFactory::createGradleDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.HEX, "Rebar", RebarDetectable.class, detectableFactory::createRebarDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.MAVEN, "Maven Pom", MavenPomDetectable.class, detectableFactory::createMavenPomDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.MAVEN, "Maven Wrapper", MavenPomWrapperDetectable.class, detectableFactory::createMavenPomWrapperDetectable).defaults().build();

        final DetectorRule lernaDetectable = ruleSet.addDetector(DetectorType.LERNA, "Lerna", LernaDetectable.class, detectableFactory::createLernaDetectable).defaults().build();
        final DetectorRule yarnLock = ruleSet.addDetector(DetectorType.YARN, "Yarn Lock", YarnLockDetectable.class, detectableFactory::createYarnLockDetectable).defaultLock().build();
        final DetectorRule npmPackageLock = ruleSet.addDetector(DetectorType.NPM, "Package Lock", NpmPackageLockDetectable.class, detectableFactory::createNpmPackageLockDetectable).defaultLock().build();
        final DetectorRule npmShrinkwrap = ruleSet.addDetector(DetectorType.NPM, "Shrinkwrap", NpmShrinkwrapDetectable.class, detectableFactory::createNpmShrinkwrapDetectable).defaultLock().build();
        final DetectorRule npmCli = ruleSet.addDetector(DetectorType.NPM, "Npm Cli", NpmCliDetectable.class, detectableFactory::createNpmCliDetectable).defaults().build();

        ruleSet.yield(npmPackageLock).to(lernaDetectable);
        ruleSet.yield(npmShrinkwrap).to(lernaDetectable);
        ruleSet.yield(npmCli).to(lernaDetectable);
        ruleSet.yield(yarnLock).to(lernaDetectable);

        ruleSet.yield(npmShrinkwrap).to(npmPackageLock);
        ruleSet.yield(npmCli).to(npmPackageLock);
        ruleSet.yield(npmCli).to(npmShrinkwrap);

        ruleSet.yield(npmCli).to(yarnLock);
        ruleSet.yield(npmPackageLock).to(yarnLock);
        ruleSet.yield(npmShrinkwrap).to(yarnLock);

        final DetectorRule nugetSolution = ruleSet.addDetector(DetectorType.NUGET, "Solution", NugetSolutionDetectable.class, detectableFactory::createNugetSolutionDetectable).defaults().build();
        //The Project detectable is "notNestable" because it will falsely apply under a solution (the solution includes all of the projects).
        final DetectorRule nugetProject = ruleSet.addDetector(DetectorType.NUGET, "Project", NugetProjectDetectable.class, detectableFactory::createNugetProjectDetectable).notNestable().noMaxDepth().build();

        ruleSet.yield(nugetProject).to(nugetSolution);

        ruleSet.addDetector(DetectorType.PACKAGIST, "Composer", ComposerLockDetectable.class, detectableFactory::createComposerDetectable).defaults().build();

        final DetectorRule pipEnv = ruleSet.addDetector(DetectorType.PIP, "Pip Env", PipenvDetectable.class, detectableFactory::createPipenvDetectable).defaults().build();
        final DetectorRule pipInspector = ruleSet.addDetector(DetectorType.PIP, "Pip Inspector", PipInspectorDetectable.class, detectableFactory::createPipInspectorDetectable).defaults().build();
        ruleSet.yield(pipInspector).to(pipEnv);

        ruleSet.addDetector(DetectorType.PIP, "Poetry", PoetryDetectable.class, detectableFactory::createPoetryDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.RUBYGEMS, "Gemlock", GemlockDetectable.class, detectableFactory::createGemlockDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.SBT, "Sbt Resolution Cache", SbtResolutionCacheDetectable.class, detectableFactory::createSbtResolutionCacheDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.PEAR, "Pear", PearCliDetectable.class, detectableFactory::createPearCliDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.CLANG, "Clang", ClangDetectable.class, detectableFactory::createClangDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.SWIFT, "Swift", SwiftCliDetectable.class, detectableFactory::createSwiftCliDetectable).defaults().build();

        final DetectorRule gitParse = ruleSet.addDetector(DetectorType.GIT, "Git Parse", GitParseDetectable.class, detectableFactory::createGitParseDetectable).defaults().build();
        final DetectorRule gitCli = ruleSet.addDetector(DetectorType.GIT, "Git Cli", GitCliDetectable.class, detectableFactory::createGitCliDetectable).defaults().build();
        ruleSet.fallback(gitCli).to(gitParse);

        return ruleSet.build();
    }

    private DetectorRuleSet createBuildlessRules(final DetectDetectableFactory detectableFactory) {
        final DetectorRuleSetBuilder ruleSet = new DetectorRuleSetBuilder();

        ruleSet.addDetector(DetectorType.CARGO, "Cargo", CargoDetectable.class, detectableFactory::createCargoDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.COCOAPODS, "Pod Lock", PodlockDetectable.class, detectableFactory::createPodLockDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.PACKAGIST, "Packrat Lock", PackratLockDetectable.class, detectableFactory::createPackratLockDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.GO_DEP, "Go Lock", GoDepLockDetectable.class, detectableFactory::createGoLockDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.GO_VNDR, "Go Vndr", GoVndrDetectable.class, detectableFactory::createGoVndrDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.GO_VENDOR, "Go Vendor", GoVendorDetectable.class, detectableFactory::createGoVendorDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.GRADLE, "Gradle Parse", GradleParseDetectable.class, detectableFactory::createGradleParseDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.GO_GRADLE, "Go Gradle", GoGradleDetectable.class, detectableFactory::createGoGradleDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.MAVEN, "Maven Pom Parse", MavenParseDetectable.class, detectableFactory::createMavenParseDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.PIP, "Poetry", PoetryDetectable.class, detectableFactory::createPoetryDetectable).defaults().build();


        final DetectorRule yarnLock = ruleSet.addDetector(DetectorType.YARN, "Yarn Lock", YarnLockDetectable.class, detectableFactory::createYarnLockDetectable).defaults().build();
        final DetectorRule npmPackageLock = ruleSet.addDetector(DetectorType.NPM, "Package Lock", NpmPackageLockDetectable.class, detectableFactory::createNpmPackageLockDetectable).defaults().build();
        final DetectorRule npmShrinkwrap = ruleSet.addDetector(DetectorType.NPM, "Shrinkwrap", NpmShrinkwrapDetectable.class, detectableFactory::createNpmShrinkwrapDetectable).defaults().build();
        final DetectorRule npmPackageJsonParse = ruleSet.addDetector(DetectorType.NPM, "Package Json Parse", NpmPackageJsonParseDetectable.class, detectableFactory::createNpmPackageJsonParseDetectable).defaults().build();

        ruleSet.yield(npmShrinkwrap).to(npmPackageLock);
        ruleSet.yield(npmPackageJsonParse).to(npmPackageLock);
        ruleSet.yield(npmPackageJsonParse).to(npmShrinkwrap);

        ruleSet.yield(npmPackageJsonParse).to(yarnLock);
        ruleSet.yield(npmPackageLock).to(yarnLock);
        ruleSet.yield(npmShrinkwrap).to(yarnLock);

        ruleSet.addDetector(DetectorType.PACKAGIST, "Composer", ComposerLockDetectable.class, detectableFactory::createComposerDetectable).defaults().build();

        final DetectorRule gemlock = ruleSet.addDetector(DetectorType.RUBYGEMS, "Gemlock", GemlockDetectable.class, detectableFactory::createGemlockDetectable).defaults().build();
        final DetectorRule gemspec = ruleSet.addDetector(DetectorType.RUBYGEMS, "Gemspec", GemspecParseDetectable.class, detectableFactory::createGemspecParseDetectable).defaults().build();

        ruleSet.yield(gemspec).to(gemlock);

        ruleSet.addDetector(DetectorType.SBT, "Sbt Resolution Cache", SbtResolutionCacheDetectable.class, detectableFactory::createSbtResolutionCacheDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.GIT, "Git Parse", GitParseDetectable.class, detectableFactory::createGitParseDetectable).defaults().invisibleToNesting().build();

        return ruleSet.build();
    }
}