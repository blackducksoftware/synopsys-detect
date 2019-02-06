/**
 * hub-detect
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
package com.blackducksoftware.integration.hub.detect.workflow.search.rules;

import com.blackducksoftware.integration.hub.detect.detector.Detector;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.detector.DetectorFactory;

public class DetectorSearchProvider {
    private final DetectorFactory detectorFactory;

    public DetectorSearchProvider(final DetectorFactory detectorFactory) {
        this.detectorFactory = detectorFactory;
    }

    public DetectorSearchRuleSet createBomToolSearchRuleSet(final DetectorEnvironment environment) {
        final DetectorSearchRuleSetBuilder searchRuleSet = new DetectorSearchRuleSetBuilder(environment);

        searchRuleSet.addBomTool(detectorFactory.createBitbakeBomTool(environment)).defaultNotNested();

        //searchRuleSet.addBomTool(detectorFactory.createPodLockBomTool(environment)).defaultNested();
        //searchRuleSet.addBomTool(detectorFactory.createCondaBomTool(environment)).defaultNotNested();
        //searchRuleSet.addBomTool(detectorFactory.createCpanCliBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(detectorFactory.createPackratLockBomTool(environment)).defaultNotNested();

        Detector goCli = detectorFactory.createGoCliBomTool(environment);
        Detector goLock = detectorFactory.createGoLockBomTool(environment);
        Detector goVnd = detectorFactory.createGoVndrBomTool(environment);
        Detector goVendor = detectorFactory.createGoVendorBomTool(environment);

        searchRuleSet.addBomTool(goLock).defaultNotNested();
        searchRuleSet.addBomTool(goVnd).defaultNotNested();
        searchRuleSet.addBomTool(goVendor).defaultNotNested();
        searchRuleSet.addBomTool(goCli).defaultNotNested();

        searchRuleSet.yield(goCli).to(goLock);
        searchRuleSet.yield(goCli).to(goVnd);
        searchRuleSet.yield(goCli).to(goVendor);

        searchRuleSet.addBomTool(detectorFactory.createGradleInspectorBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(detectorFactory.createRebarBomTool(environment)).defaultNotNested();

        searchRuleSet.addBomTool(detectorFactory.createMavenPomBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(detectorFactory.createMavenPomWrapperBomTool(environment)).defaultNotNested();

        Detector yarnLock = detectorFactory.createYarnLockBomTool(environment);
        searchRuleSet.addBomTool(yarnLock).defaultNested();

        Detector npmPackageLock = detectorFactory.createNpmPackageLockBomTool(environment);
        Detector npmShrinkwrap = detectorFactory.createNpmShrinkwrapBomTool(environment);
        Detector npmCli = detectorFactory.createNpmCliBomTool(environment);

        searchRuleSet.addBomTool(npmPackageLock).defaultNested();
        searchRuleSet.addBomTool(npmShrinkwrap).defaultNested();
        searchRuleSet.addBomTool(npmCli).defaultNested();

        searchRuleSet.yield(npmShrinkwrap).to(npmPackageLock);
        searchRuleSet.yield(npmCli).to(npmPackageLock);
        searchRuleSet.yield(npmCli).to(npmShrinkwrap);

        searchRuleSet.yield(npmCli).to(yarnLock);
        searchRuleSet.yield(npmPackageLock).to(yarnLock);
        searchRuleSet.yield(npmShrinkwrap).to(yarnLock);

        Detector nugetSolution = detectorFactory.createNugetSolutionBomTool(environment);
        Detector nugetProject = detectorFactory.createNugetProjectBomTool(environment);
        searchRuleSet.addBomTool(nugetSolution).defaultNested();
        searchRuleSet.addBomTool(nugetProject).defaultNotNested();

        searchRuleSet.yield(nugetProject).to(nugetSolution);

        searchRuleSet.addBomTool(detectorFactory.createComposerLockBomTool(environment)).defaultNotNested();

        Detector pipEnv = detectorFactory.createPipenvBomTool(environment);
        Detector pipInspector = detectorFactory.createPipInspectorBomTool(environment);
        searchRuleSet.addBomTool(pipEnv).defaultNotNested();
        searchRuleSet.addBomTool(pipInspector).defaultNotNested();

        searchRuleSet.yield(pipInspector).to(pipEnv);

        searchRuleSet.addBomTool(detectorFactory.createGemlockBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(detectorFactory.createSbtResolutionCacheBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(detectorFactory.createPearCliBomTool(environment)).defaultNotNested();

        searchRuleSet.addBomTool(detectorFactory.createClangBomTool(environment)).defaultNested();

        return searchRuleSet.build();
    }
}
