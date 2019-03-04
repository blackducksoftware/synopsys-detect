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
package com.synopsys.integration.detect.tool.detector;

import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSetBuilder;

public class DetectorRuleFactory {

    public void createRules(DetectableFactory detectableFactory) {

        final DetectorRuleSetBuilder ruleSet = new DetectorRuleSetBuilder();

        ruleSet.addDetector(DetectorType.BITBAKE,"Bitbake", detectableFactory::createBitbakeDetectable).defaultNotNested().build();

        ruleSet.addDetector(DetectorType.COCOAPODS, "Pod Lock", detectableFactory::createPodLockDetectable).defaultNested().build();
        ruleSet.addDetector(DetectorType.CONDA, "Conda Cli", detectableFactory::createCondaDetectable).defaultNotNested().build();
        ruleSet.addDetector(DetectorType.CPAN, "Cpan Cli", detectableFactory::createCpanCliDetectable).defaultNotNested().build();
        ruleSet.addDetector(DetectorType.PACKAGIST, "Packrat Lock", detectableFactory::createPackratLockDetectable).defaultNotNested().build();

        DetectorRule goCli = ruleSet.addDetector(DetectorType.GO_DEP, "Go Cli", detectableFactory::createGoCliDetectable).defaultNested().build();
        DetectorRule goLock = ruleSet.addDetector(DetectorType.GO_DEP, "Go Lock", detectableFactory::createGoLockDetectable).defaultNested().build();
        DetectorRule goVnd = ruleSet.addDetector(DetectorType.GO_VNDR, "Go Vndr", detectableFactory::createGoVndrDetectable).defaultNested().build();
        DetectorRule goVendor = ruleSet.addDetector(DetectorType.GO_VENDOR, "Go Vendor", detectableFactory::createGoVendorDetectable).defaultNested().build();

        ruleSet.yield(goCli).to(goLock);
        ruleSet.yield(goCli).to(goVnd);
        ruleSet.yield(goCli).to(goVendor);

        ruleSet.addDetector(DetectorType.GRADLE, "Gradle Inspector", detectableFactory::createGradleInspectorDetectable).defaultNotNested().build();
        ruleSet.addDetector(DetectorType.HEX, "Rebar", detectableFactory::createRebarDetectable).defaultNotNested().build();

        ruleSet.addDetector(DetectorType.MAVEN, "Maven Pom", detectableFactory::createMavenPomDetectable).defaultNotNested().build();
        ruleSet.addDetector(DetectorType.MAVEN, "Maven Wrapper", detectableFactory::createMavenPomWrapperDetectable).defaultNotNested().build();

        DetectorRule yarnLock = ruleSet.addDetector(DetectorType.YARN, "Yarn Lock", detectableFactory::createYarnLockDetectable).defaultNested().build();
        DetectorRule npmPackageLock = ruleSet.addDetector(DetectorType.NPM, "Package Lock", detectableFactory::createNpmPackageLockDetectable).defaultNested().build();
        DetectorRule npmShrinkwrap = ruleSet.addDetector(DetectorType.NPM, "Shrinkwrap", detectableFactory::createNpmShrinkwrapDetectable).defaultNested().build();
        DetectorRule npmCli = ruleSet.addDetector(DetectorType.NPM, "Npm Cli", detectableFactory::createNpmCliDetectable).defaultNested().build();

        ruleSet.yield(npmShrinkwrap).to(npmPackageLock);
        ruleSet.yield(npmCli).to(npmPackageLock);
        ruleSet.yield(npmCli).to(npmShrinkwrap);

        ruleSet.yield(npmCli).to(yarnLock);
        ruleSet.yield(npmPackageLock).to(yarnLock);
        ruleSet.yield(npmShrinkwrap).to(yarnLock);

        DetectorRule nugetSolution = ruleSet.addDetector(DetectorType.NUGET, "Solution", detectableFactory::createNugetSolutionDetectable).defaultNested().build();
        DetectorRule nugetProject = ruleSet.addDetector(DetectorType.NUGET, "Project", detectableFactory::createNugetProjectDetectable).defaultNotNested().build();

        ruleSet.yield(nugetProject).to(nugetSolution);

        ruleSet.addDetector(DetectorType.PACKAGIST, "Composer", detectableFactory::createComposerLockDetectable).defaultNotNested();

        DetectorRule pipEnv = ruleSet.addDetector(DetectorType.PIP, "Pip Env", detectableFactory::createPipenvDetectable).defaultNotNested().build();
        DetectorRule pipInspector = ruleSet.addDetector(DetectorType.PIP, "Pip Inspector", detectableFactory::createPipInspectorDetectable).defaultNotNested().build();

        ruleSet.yield(pipInspector).to(pipEnv);

        ruleSet.addDetector(DetectorType.RUBYGEMS, "Gemlock", detectableFactory::createGemlockDetectable).defaultNotNested();
        ruleSet.addDetector(DetectorType.SBT, "Sbt Resolution Cache", detectableFactory::createSbtResolutionCacheDetectable).defaultNotNested();
        ruleSet.addDetector(DetectorType.PEAR, "Pear", detectableFactory::createPearCliDetectable).defaultNotNested();

        ruleSet.addDetector(DetectorType.CLANG, "Clang", detectableFactory::createClangDetectable).defaultNested();
    }
}