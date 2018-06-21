package com.blackducksoftware.integration.hub.detect.bomtool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.bomtool.cocoapods.PodlockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.cocoapods.PodlockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.conda.CondaCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.conda.CondaCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.cpan.CpanCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.cpan.CpanCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.cran.PackratLockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.cran.PackratLockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.docker.DockerBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.docker.DockerExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.docker.DockerInspectorManager;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoDepsBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoLockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoVndrBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.go.extraction.GoDepExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.go.extraction.GoDepsExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.go.extraction.GoInspectorManager;
import com.blackducksoftware.integration.hub.detect.bomtool.go.extraction.GoVndrExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleExecutableFinder;
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleInspectorBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleInspectorManager;
import com.blackducksoftware.integration.hub.detect.bomtool.hex.RebarBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.hex.RebarExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenExecutableFinder;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenPomBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenPomWrapperBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmExecutableFinder;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmLockfileExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmPackageLockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmShrinkwrapBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetInspectorManager;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetProjectBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetSolutionBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.packagist.ComposerLockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.packagist.ComposerLockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.pear.PearCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.pear.PearCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipInspectorBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipInspectorManager;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipenvBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipenvExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PythonExecutableFinder;
import com.blackducksoftware.integration.hub.detect.bomtool.rubygems.GemlockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.rubygems.GemlockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.SbtResolutionCacheBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.SbtResolutionCacheExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.yarn.YarnLockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.yarn.YarnLockExtractor;
import com.blackducksoftware.integration.hub.detect.evaluation.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.extraction.model.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class BomToolFactory {
    @Autowired
    PodlockExtractor podlockExtractor;

    @Autowired
    CondaCliExtractor condaCliExtractor;

    @Autowired
    CpanCliExtractor cpanCliExtractor;

    @Autowired
    PackratLockExtractor packratLockExtractor;

    @Autowired
    DockerExtractor dockerExtractor;

    @Autowired
    GoVndrExtractor goVndrExtractor;

    @Autowired
    GoDepsExtractor goDepsExtractor;

    @Autowired
    GoDepExtractor goDepExtractor;

    @Autowired
    GradleInspectorExtractor gradleInspectorExtractor;

    @Autowired
    RebarExtractor rebarExtractor;

    @Autowired
    MavenCliExtractor mavenCliExtractor;

    @Autowired
    NpmCliExtractor npmCliExtractor;

    @Autowired
    NpmLockfileExtractor npmLockfileExtractor;

    @Autowired
    NugetInspectorExtractor nugetInspectorExtractor;

    @Autowired
    ComposerLockExtractor composerLockExtractor;

    @Autowired
    PearCliExtractor pearCliExtractor;

    @Autowired
    PipenvExtractor pipenvExtractor;

    @Autowired
    PipInspectorExtractor pipInspectorExtractor;

    @Autowired
    GemlockExtractor gemlockExtractor;

    @Autowired
    SbtResolutionCacheExtractor sbtResolutionCacheExtractor;

    @Autowired
    YarnLockExtractor yarnLockExtractor;

    @Autowired
    DetectFileFinder detectFileFinder;

    @Autowired
    StandardExecutableFinder standardExecutableFinder;

    @Autowired
    DockerInspectorManager dockerInspectorManager;

    @Autowired
    PipInspectorManager pipInspectorManager;

    @Autowired
    GoInspectorManager goInspectorManager;

    @Autowired
    NugetInspectorManager nugetInspectorManager;

    @Autowired
    PythonExecutableFinder pythonExecutableFinder;

    @Autowired
    GradleExecutableFinder gradleFinder;

    @Autowired
    GradleInspectorManager gradleInspectorManager;

    @Autowired
    MavenExecutableFinder mavenExecutableFinder;

    @Autowired
    NpmExecutableFinder npmExecutableFinder;

    @Autowired
    DetectConfiguration detectConfiguration;

    public BomToolSearchRuleSet createStrategies(final BomToolEnvironment environment) {
        final BomToolSearchRuleSetBuilder searchRuleSet = new BomToolSearchRuleSetBuilder(environment);

        searchRuleSet.addBomTool(createCocoapodsBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createCondaBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createCpanCliBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createPackratLockBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createDockerBomTool(environment)).nestable(false).maxDepth(0);

        searchRuleSet.addBomTool(createGoCliBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createGoDepsBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createGoLockBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createGoVndrBomTool(environment)).defaultNotNested();

        searchRuleSet.yield(BomToolType.GO_CLI).to(BomToolType.GO_DEPS);
        searchRuleSet.yield(BomToolType.GO_CLI).to(BomToolType.GO_LOCK);
        searchRuleSet.yield(BomToolType.GO_CLI).to(BomToolType.GO_VNDR);

        searchRuleSet.addBomTool(createGradleInspectorBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createRebarBomTool(environment)).defaultNotNested();

        searchRuleSet.addBomTool(createMavenPomBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createMavenPomWrapperBomTool(environment)).defaultNotNested();

        searchRuleSet.addBomTool(createYarnLockBomTool(environment)).defaultNested();

        searchRuleSet.addBomTool(createNpmPackageLockBomTool(environment)).defaultNested();
        searchRuleSet.addBomTool(createNpmShrinkwrapBomTool(environment)).defaultNested();
        searchRuleSet.addBomTool(createNpmCliBomTool(environment)).defaultNested();

        searchRuleSet.yield(BomToolType.NPM_SHRINKWRAP).to(BomToolType.NPM_PACKAGELOCK);
        searchRuleSet.yield(BomToolType.NPM_CLI).to(BomToolType.NPM_PACKAGELOCK);
        searchRuleSet.yield(BomToolType.NPM_CLI).to(BomToolType.NPM_SHRINKWRAP);

        searchRuleSet.yield(BomToolType.NPM_CLI).to(BomToolType.YARN_LOCK);
        searchRuleSet.yield(BomToolType.NPM_PACKAGELOCK).to(BomToolType.YARN_LOCK);
        searchRuleSet.yield(BomToolType.NPM_SHRINKWRAP).to(BomToolType.YARN_LOCK);

        searchRuleSet.addBomTool(createNugetSolutionBomTool(environment)).defaultNested();
        searchRuleSet.addBomTool(createNugetProjectBomTool(environment)).defaultNotNested();

        searchRuleSet.yield(BomToolType.NUGET_PROJECT_INSPECTOR).to(BomToolType.NUGET_SOLUTION_INSPECTOR);

        searchRuleSet.addBomTool(createComposerLockBomTool(environment)).defaultNotNested();

        searchRuleSet.addBomTool(createPipenvBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createPipInspectorBomTool(environment)).defaultNotNested();

        searchRuleSet.yield(BomToolType.PIP_INSPECTOR).to(BomToolType.PIP_ENV);

        searchRuleSet.addBomTool(createGemlockBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createSbtResolutionCacheBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createPearCliBomTool(environment)).defaultNotNested();

        return searchRuleSet.build();
    }

    private PodlockBomTool createCocoapodsBomTool(final BomToolEnvironment environment) {
        final PodlockBomTool bomTool = new PodlockBomTool(environment, detectFileFinder, podlockExtractor);
        return bomTool;
    }

    private CondaCliBomTool createCondaBomTool(final BomToolEnvironment environment) {
        final CondaCliBomTool bomTool = new CondaCliBomTool(environment, detectFileFinder, standardExecutableFinder, condaCliExtractor);
        return bomTool;
    }

    private CpanCliBomTool createCpanCliBomTool(final BomToolEnvironment environment) {
        final CpanCliBomTool bomTool = new CpanCliBomTool(environment, detectFileFinder, standardExecutableFinder, cpanCliExtractor);
        return bomTool;
    }

    private PackratLockBomTool createPackratLockBomTool(final BomToolEnvironment environment) {
        final PackratLockBomTool bomTool = new PackratLockBomTool(environment, detectFileFinder, packratLockExtractor);
        return bomTool;
    }

    private DockerBomTool createDockerBomTool(final BomToolEnvironment environment) {
        final String tar = detectConfiguration.getDockerTar();
        final String image = detectConfiguration.getDockerImage();
        final boolean dockerRequired = detectConfiguration.getDockerPathRequired();

        final DockerBomTool bomTool = new DockerBomTool(environment, dockerInspectorManager, standardExecutableFinder, dockerRequired, image, tar, dockerExtractor);
        return bomTool;
    }

    private GoCliBomTool createGoCliBomTool(final BomToolEnvironment environment) {
        final GoCliBomTool bomTool = new GoCliBomTool(environment, detectFileFinder, standardExecutableFinder, goInspectorManager, goDepExtractor);
        return bomTool;
    }

    private GoDepsBomTool createGoDepsBomTool(final BomToolEnvironment environment) {
        final GoDepsBomTool bomTool = new GoDepsBomTool(environment, detectFileFinder, goDepsExtractor);
        return bomTool;
    }

    private GoLockBomTool createGoLockBomTool(final BomToolEnvironment environment) {
        final GoLockBomTool bomTool = new GoLockBomTool(environment, detectFileFinder, standardExecutableFinder, goInspectorManager, goDepExtractor);
        return bomTool;
    }

    private GoVndrBomTool createGoVndrBomTool(final BomToolEnvironment environment) {
        final GoVndrBomTool bomTool = new GoVndrBomTool(environment, detectFileFinder, goVndrExtractor);
        return bomTool;
    }

    private GradleInspectorBomTool createGradleInspectorBomTool(final BomToolEnvironment environment) {
        final GradleInspectorBomTool bomTool = new GradleInspectorBomTool(environment, detectFileFinder, gradleFinder, gradleInspectorManager, gradleInspectorExtractor);
        return bomTool;
    }

    private RebarBomTool createRebarBomTool(final BomToolEnvironment environment) {
        final RebarBomTool bomTool = new RebarBomTool(environment, detectFileFinder, standardExecutableFinder, rebarExtractor);
        return bomTool;
    }

    private MavenPomBomTool createMavenPomBomTool(final BomToolEnvironment environment) {
        final MavenPomBomTool bomTool = new MavenPomBomTool(environment, detectFileFinder, mavenExecutableFinder, mavenCliExtractor);
        return bomTool;
    }

    private MavenPomWrapperBomTool createMavenPomWrapperBomTool(final BomToolEnvironment environment) {
        final MavenPomWrapperBomTool bomTool = new MavenPomWrapperBomTool(environment, detectFileFinder, mavenExecutableFinder, mavenCliExtractor);
        return bomTool;
    }

    private NpmCliBomTool createNpmCliBomTool(final BomToolEnvironment environment) {
        final NpmCliBomTool bomTool = new NpmCliBomTool(environment, detectFileFinder, npmCliExtractor);
        return bomTool;
    }

    private NpmPackageLockBomTool createNpmPackageLockBomTool(final BomToolEnvironment environment) {
        final NpmPackageLockBomTool bomTool = new NpmPackageLockBomTool(environment, detectFileFinder, npmLockfileExtractor);
        return bomTool;
    }

    private NpmShrinkwrapBomTool createNpmShrinkwrapBomTool(final BomToolEnvironment environment) {
        final NpmShrinkwrapBomTool bomTool = new NpmShrinkwrapBomTool(environment, detectFileFinder, npmLockfileExtractor);
        return bomTool;
    }

    private NugetSolutionBomTool createNugetSolutionBomTool(final BomToolEnvironment environment) {
        final NugetSolutionBomTool bomTool = new NugetSolutionBomTool(environment, detectFileFinder, nugetInspectorManager, nugetInspectorExtractor);
        return bomTool;
    }

    private NugetProjectBomTool createNugetProjectBomTool(final BomToolEnvironment environment) {
        final NugetProjectBomTool bomTool = new NugetProjectBomTool(environment, detectFileFinder, nugetInspectorManager, nugetInspectorExtractor);
        return bomTool;
    }

    private ComposerLockBomTool createComposerLockBomTool(final BomToolEnvironment environment) {
        final ComposerLockBomTool bomTool = new ComposerLockBomTool(environment, detectFileFinder, composerLockExtractor);
        return bomTool;
    }

    private PearCliBomTool createPearCliBomTool(final BomToolEnvironment environment) {
        final PearCliBomTool bomTool = new PearCliBomTool(environment, detectFileFinder, standardExecutableFinder, pearCliExtractor);
        return bomTool;
    }

    private PipenvBomTool createPipenvBomTool(final BomToolEnvironment environment) {
        final PipenvBomTool bomTool = new PipenvBomTool(environment, detectFileFinder, pythonExecutableFinder, pipenvExtractor);
        return bomTool;
    }

    private PipInspectorBomTool createPipInspectorBomTool(final BomToolEnvironment environment) {
        final String requirementsFile = detectConfiguration.getRequirementsFilePath();
        final PipInspectorBomTool bomTool = new PipInspectorBomTool(environment, requirementsFile, detectFileFinder, pythonExecutableFinder, pipInspectorManager, pipInspectorExtractor);
        return bomTool;
    }

    private GemlockBomTool createGemlockBomTool(final BomToolEnvironment environment) {
        final GemlockBomTool bomTool = new GemlockBomTool(environment, detectFileFinder, gemlockExtractor);
        return bomTool;
    }

    private SbtResolutionCacheBomTool createSbtResolutionCacheBomTool(final BomToolEnvironment environment) {
        final SbtResolutionCacheBomTool bomTool = new SbtResolutionCacheBomTool(environment, detectFileFinder, sbtResolutionCacheExtractor);
        return bomTool;
    }

    private YarnLockBomTool createYarnLockBomTool(final BomToolEnvironment environment) {
        final boolean productionDependenciesOnly = detectConfiguration.getYarnProductionDependenciesOnly();
        final YarnLockBomTool bomTool = new YarnLockBomTool(environment, productionDependenciesOnly, detectFileFinder, standardExecutableFinder, yarnLockExtractor);
        return bomTool;
    }
}
