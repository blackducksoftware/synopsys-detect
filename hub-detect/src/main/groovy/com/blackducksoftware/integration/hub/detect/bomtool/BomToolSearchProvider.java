package com.blackducksoftware.integration.hub.detect.bomtool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.evaluation.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.factory.BomToolFactory;

@Component
public class BomToolSearchProvider {

    @Autowired
    BomToolFactory bomToolFactory;

    public BomToolSearchRuleSet createStrategies(final BomToolEnvironment environment) {
        final BomToolSearchRuleSetBuilder searchRuleSet = new BomToolSearchRuleSetBuilder(environment);

        searchRuleSet.addBomTool(bomToolFactory.createCocoapodsBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(bomToolFactory.createCondaBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(bomToolFactory.createCpanCliBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(bomToolFactory.createPackratLockBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(bomToolFactory.createDockerBomTool(environment)).nestable(false).maxDepth(0);

        searchRuleSet.addBomTool(bomToolFactory.createGoCliBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(bomToolFactory.createGoDepsBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(bomToolFactory.createGoLockBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(bomToolFactory.createGoVndrBomTool(environment)).defaultNotNested();

        searchRuleSet.yield(BomToolType.GO_CLI).to(BomToolType.GO_DEPS);
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

        return searchRuleSet.build();
    }
}
