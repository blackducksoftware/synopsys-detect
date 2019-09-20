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

import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;
import com.synopsys.integration.detector.rule.DetectorRuleSetBuilder;

public class DetectorRuleFactory {
    public DetectorRuleSet createRules(final DetectableFactory detectableFactory, final boolean buildless) {
        if (buildless) {
            return createBuildlessRules(detectableFactory);
        } else {
            return createRules(detectableFactory);
        }
    }

    //TODO: It would just be nice not to have to call 'build' after each of the addDetectors.
    private DetectorRuleSet createRules(final DetectableFactory detectableFactory) {
        final DetectorRuleSetBuilder ruleSet = new DetectorRuleSetBuilder();

        ruleSet.addDetector(DetectorType.BITBAKE, "Bitbake", detectableFactory::createBitbakeDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.COCOAPODS, "Pod Lock", detectableFactory::createPodLockDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.CONDA, "Conda Cli", detectableFactory::createCondaDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.CPAN, "Cpan Cli", detectableFactory::createCpanCliDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.CRAN, "Packrat Lock", detectableFactory::createPackratLockDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.GO_MOD, "Go Mod Cli", detectableFactory::createGoModCliDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.GO_GRADLE, "Go Gradle", detectableFactory::createGoGradleDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.GO_DEP, "Go Lock", detectableFactory::createGoLockDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.GO_VNDR, "Go Vndr", detectableFactory::createGoVndrDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.GO_VENDOR, "Go Vendor", detectableFactory::createGoVendorDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.GRADLE, "Gradle Inspector", detectableFactory::createGradleDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.HEX, "Rebar", detectableFactory::createRebarDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.MAVEN, "Maven Pom", detectableFactory::createMavenPomDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.MAVEN, "Maven Wrapper", detectableFactory::createMavenPomWrapperDetectable).defaults().build();

        final DetectorRule yarnLock = ruleSet.addDetector(DetectorType.YARN, "Yarn Lock", detectableFactory::createYarnLockDetectable).defaultLock().build();
        final DetectorRule npmPackageLock = ruleSet.addDetector(DetectorType.NPM, "Package Lock", detectableFactory::createNpmPackageLockDetectable).defaultLock().build();
        final DetectorRule npmShrinkwrap = ruleSet.addDetector(DetectorType.NPM, "Shrinkwrap", detectableFactory::createNpmShrinkwrapDetectable).defaultLock().build();
        final DetectorRule npmCli = ruleSet.addDetector(DetectorType.NPM, "Npm Cli", detectableFactory::createNpmCliDetectable).defaults().build();

        ruleSet.yield(npmShrinkwrap).to(npmPackageLock);
        ruleSet.yield(npmCli).to(npmPackageLock);
        ruleSet.yield(npmCli).to(npmShrinkwrap);

        ruleSet.yield(npmCli).to(yarnLock);
        ruleSet.yield(npmPackageLock).to(yarnLock);
        ruleSet.yield(npmShrinkwrap).to(yarnLock);

        final DetectorRule nugetSolution = ruleSet.addDetector(DetectorType.NUGET, "Solution", detectableFactory::createNugetSolutionDetectable).defaults().build();
        //The Project detectable is "notNestable" because it will falsely apply under a solution (the solution includes all of the projects).
        final DetectorRule nugetProject = ruleSet.addDetector(DetectorType.NUGET, "Project", detectableFactory::createNugetProjectDetectable).notNestable().noMaxDepth().build();

        ruleSet.yield(nugetProject).to(nugetSolution);

        ruleSet.addDetector(DetectorType.PACKAGIST, "Composer", detectableFactory::createComposerLockDetectable).defaults().build();

        final DetectorRule pipEnv = ruleSet.addDetector(DetectorType.PIP, "Pip Env", detectableFactory::createPipenvDetectable).defaults().build();
        final DetectorRule pipInspector = ruleSet.addDetector(DetectorType.PIP, "Pip Inspector", detectableFactory::createPipInspectorDetectable).defaults().build();

        ruleSet.yield(pipInspector).to(pipEnv);

        ruleSet.addDetector(DetectorType.RUBYGEMS, "Gemlock", detectableFactory::createGemlockDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.SBT, "Sbt Resolution Cache", detectableFactory::createSbtResolutionCacheDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.PEAR, "Pear", detectableFactory::createPearCliDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.CLANG, "Clang", detectableFactory::createClangDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.SWIFT, "Swift", detectableFactory::createSwiftCliDetectable).defaults().build();

        final DetectorRule gitParse = ruleSet.addDetector(DetectorType.GIT, "Git Parse", detectableFactory::createGitParseDetectable).defaults().build();
        final DetectorRule gitCli = ruleSet.addDetector(DetectorType.GIT, "Git Cli", detectableFactory::createGitCliDetectable).defaults().build();
        ruleSet.fallback(gitCli).to(gitParse);

        return ruleSet.build();
    }

    private DetectorRuleSet createBuildlessRules(final DetectableFactory detectableFactory) {
        final DetectorRuleSetBuilder ruleSet = new DetectorRuleSetBuilder();

        ruleSet.addDetector(DetectorType.COCOAPODS, "Pod Lock", detectableFactory::createPodLockDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.PACKAGIST, "Packrat Lock", detectableFactory::createPackratLockDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.GO_DEP, "Go Lock", detectableFactory::createGoLockDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.GO_VNDR, "Go Vndr", detectableFactory::createGoVndrDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.GO_VENDOR, "Go Vendor", detectableFactory::createGoVendorDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.GRADLE, "Gradle Parse", detectableFactory::createGradleParseDetectable).defaults().build();
        ruleSet.addDetector(DetectorType.GO_GRADLE, "Go Gradle", detectableFactory::createGoGradleDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.MAVEN, "Maven Pom Parse", detectableFactory::createMavenParseDetectable).defaults().build();

        final DetectorRule yarnLock = ruleSet.addDetector(DetectorType.YARN, "Yarn Lock", detectableFactory::createYarnLockDetectable).defaults().build();
        final DetectorRule npmPackageLock = ruleSet.addDetector(DetectorType.NPM, "Package Lock", detectableFactory::createNpmPackageLockDetectable).defaults().build();
        final DetectorRule npmShrinkwrap = ruleSet.addDetector(DetectorType.NPM, "Shrinkwrap", detectableFactory::createNpmShrinkwrapDetectable).defaults().build();
        final DetectorRule npmPackageJsonParse = ruleSet.addDetector(DetectorType.NPM, "Package Json Parse", detectableFactory::createNpmPackageJsonParseDetectable).defaults().build();

        ruleSet.yield(npmShrinkwrap).to(npmPackageLock);
        ruleSet.yield(npmPackageJsonParse).to(npmPackageLock);
        ruleSet.yield(npmPackageJsonParse).to(npmShrinkwrap);

        ruleSet.yield(npmPackageJsonParse).to(yarnLock);
        ruleSet.yield(npmPackageLock).to(yarnLock);
        ruleSet.yield(npmShrinkwrap).to(yarnLock);

        ruleSet.addDetector(DetectorType.PACKAGIST, "Composer", detectableFactory::createComposerLockDetectable).defaults().build();

        ruleSet.addDetector(DetectorType.PIP, "Pip Env", detectableFactory::createPipenvDetectable).defaults().build();

        final DetectorRule gemlock = ruleSet.addDetector(DetectorType.RUBYGEMS, "Gemlock", detectableFactory::createGemlockDetectable).defaults().build();
        final DetectorRule gemspec = ruleSet.addDetector(DetectorType.RUBYGEMS, "Gemspec", detectableFactory::createGemspecParseDetectable).defaults().build();

        ruleSet.yield(gemspec).to(gemlock);

        ruleSet.addDetector(DetectorType.SBT, "Sbt Resolution Cache", detectableFactory::createSbtResolutionCacheDetectable).defaults().build();

        final DetectorRule gitParse = ruleSet.addDetector(DetectorType.GIT, "Git Parse", detectableFactory::createGitParseDetectable).defaults().invisibleToNesting().build();
        final DetectorRule gitCli = ruleSet.addDetector(DetectorType.GIT, "Git Cli", detectableFactory::createGitCliDetectable).defaults().invisibleToNesting().build();
        ruleSet.yield(gitParse).to(gitCli);

        return ruleSet.build();
    }
}