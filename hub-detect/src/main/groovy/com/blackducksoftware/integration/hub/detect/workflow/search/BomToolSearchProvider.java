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
package com.blackducksoftware.integration.hub.detect.workflow.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.factory.BomToolFactory;

@Component
public class BomToolSearchProvider {

    @Autowired
    BomToolFactory bomToolFactory;

    public BomToolSearchRuleSet createBomTools(final BomToolEnvironment environment) {
        final BomToolSearchRuleSetBuilder searchRuleSet = new BomToolSearchRuleSetBuilder(environment);

        searchRuleSet.addBomTool(bomToolFactory.createCocoapodsBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(bomToolFactory.createCondaBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(bomToolFactory.createCpanCliBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(bomToolFactory.createPackratLockBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(bomToolFactory.createDockerBomTool(environment)).nestable(false).maxDepth(0);

        searchRuleSet.addBomTool(bomToolFactory.createGoCliBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(bomToolFactory.createGoLockBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(bomToolFactory.createGoVndrBomTool(environment)).defaultNotNested();

        searchRuleSet.yield(BomToolType.GO_CLI).to(BomToolType.GO_LOCK);
        searchRuleSet.yield(BomToolType.GO_CLI).to(BomToolType.GO_VNDR);

        searchRuleSet.addBomTool(bomToolFactory.createGradleInspectorBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(bomToolFactory.createRebarBomTool(environment)).defaultNotNested();

        searchRuleSet.addBomTool(bomToolFactory.createMavenPomBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(bomToolFactory.createMavenPomWrapperBomTool(environment)).defaultNotNested();

        searchRuleSet.addBomTool(bomToolFactory.createYarnLockBomTool(environment)).defaultNested();

        searchRuleSet.addBomTool(bomToolFactory.createNpmPackageLockBomTool(environment)).defaultNested();
        searchRuleSet.addBomTool(bomToolFactory.createNpmShrinkwrapBomTool(environment)).defaultNested();
        searchRuleSet.addBomTool(bomToolFactory.createNpmCliBomTool(environment)).defaultNested();

        searchRuleSet.yield(BomToolType.NPM_SHRINKWRAP).to(BomToolType.NPM_PACKAGELOCK);
        searchRuleSet.yield(BomToolType.NPM_CLI).to(BomToolType.NPM_PACKAGELOCK);
        searchRuleSet.yield(BomToolType.NPM_CLI).to(BomToolType.NPM_SHRINKWRAP);

        searchRuleSet.yield(BomToolType.NPM_CLI).to(BomToolType.YARN_LOCK);
        searchRuleSet.yield(BomToolType.NPM_PACKAGELOCK).to(BomToolType.YARN_LOCK);
        searchRuleSet.yield(BomToolType.NPM_SHRINKWRAP).to(BomToolType.YARN_LOCK);

        searchRuleSet.addBomTool(bomToolFactory.createNugetSolutionBomTool(environment)).defaultNested();
        searchRuleSet.addBomTool(bomToolFactory.createNugetProjectBomTool(environment)).defaultNotNested();

        searchRuleSet.yield(BomToolType.NUGET_PROJECT_INSPECTOR).to(BomToolType.NUGET_SOLUTION_INSPECTOR);

        searchRuleSet.addBomTool(bomToolFactory.createComposerLockBomTool(environment)).defaultNotNested();

        searchRuleSet.addBomTool(bomToolFactory.createPipenvBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(bomToolFactory.createPipInspectorBomTool(environment)).defaultNotNested();

        searchRuleSet.yield(BomToolType.PIP_INSPECTOR).to(BomToolType.PIP_ENV);

        searchRuleSet.addBomTool(bomToolFactory.createGemlockBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(bomToolFactory.createSbtResolutionCacheBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(bomToolFactory.createPearCliBomTool(environment)).defaultNotNested();

        searchRuleSet.addBomTool(bomToolFactory.createCLangBomTool(environment)).defaultNested();

        return searchRuleSet.build();
    }
}
