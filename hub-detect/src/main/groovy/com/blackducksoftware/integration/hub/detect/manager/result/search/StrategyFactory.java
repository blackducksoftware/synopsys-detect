package com.blackducksoftware.integration.hub.detect.manager.result.search;

import org.springframework.beans.factory.annotation.Autowired;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.cocoapods.PodlockExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.cocoapods.PodlockStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.conda.CondaCliExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.conda.CondaCliStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.cpan.CpanCliExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.cpan.CpanCliStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.cran.PackratLockExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.cran.PackratLockStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.docker.DockerExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.docker.DockerInspectorManager;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.docker.DockerStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoDepExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoDepsExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoInspectorManager;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoVndrExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.strategy.GoCliStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.strategy.GoDepsStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.strategy.GoLockStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.strategy.GoVndrStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.gradle.GradleExecutableFinder;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.gradle.GradleInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.gradle.GradleInspectorManager;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.gradle.GradleInspectorStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.hex.RebarExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.hex.RebarStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.maven.MavenCliExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.maven.MavenExecutableFinder;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.maven.MavenPomStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.maven.MavenPomWrapperStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm.NpmCliExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm.NpmCliStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm.NpmExecutableFinder;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm.NpmLockfileExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm.NpmPackageLockStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm.NpmShrinkwrapStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.nuget.NugetInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.nuget.NugetInspectorManager;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.nuget.NugetProjectStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.nuget.NugetSolutionStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.packagist.ComposerLockExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.packagist.ComposerLockStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.pear.PearCliExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.pear.PearCliStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.pip.PipInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.pip.PipInspectorManager;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.pip.PipInspectorStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.pip.PipenvExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.pip.PipenvStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.pip.PythonExecutableFinder;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.rubygems.GemlockExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.rubygems.GemlockStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.sbt.SbtResolutionCacheExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.sbt.SbtResolutionCacheStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.yarn.YarnLockExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.yarn.YarnLockStrategy;
import com.blackducksoftware.integration.hub.detect.extraction.model.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.strategy.StrategySearchOptions;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

public class StrategyFactory {
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

    public StrategySet createStrategies(final StrategyEnvironment environment) {
        final StrategySet strategySet = new StrategySet();

        strategySet.addStrategy(createCocoapodsStrategy(environment), StrategySearchOptions.defaultNotNested());
        strategySet.addStrategy(createCondaStrategy(environment), StrategySearchOptions.defaultNotNested());
        strategySet.addStrategy(createCpanCliStrategy(environment), StrategySearchOptions.defaultNotNested());
        strategySet.addStrategy(createPackratLockStrategy(environment), StrategySearchOptions.defaultNotNested());
        strategySet.addStrategy(createDockerStrategy(environment), new StrategySearchOptions(0, false));

        strategySet.addStrategy(createGoCliStrategy(environment), StrategySearchOptions.defaultNotNested());
        strategySet.addStrategy(createGoDepsStrategy(environment), StrategySearchOptions.defaultNotNested());
        strategySet.addStrategy(createGoLockStrategy(environment), StrategySearchOptions.defaultNotNested());
        strategySet.addStrategy(createGoVndrStrategy(environment), StrategySearchOptions.defaultNotNested());

        strategySet.yield(StrategyType.GO_CLI, StrategyType.GO_DEPS);
        strategySet.yield(StrategyType.GO_CLI, StrategyType.GO_LOCK);
        strategySet.yield(StrategyType.GO_CLI, StrategyType.GO_VNDR);

        strategySet.addStrategy(createGradleInspectorStrategy(environment), StrategySearchOptions.defaultNotNested());
        strategySet.addStrategy(createRebarStrategy(environment), StrategySearchOptions.defaultNotNested());

        strategySet.addStrategy(createMavenPomStrategy(environment), StrategySearchOptions.defaultNotNested());
        strategySet.addStrategy(createMavenPomWrapperStrategy(environment), StrategySearchOptions.defaultNotNested());

        strategySet.addStrategy(createYarnLockStrategy(environment), StrategySearchOptions.defaultNested());

        strategySet.addStrategy(createNpmPackageLockStrategy(environment), StrategySearchOptions.defaultNested());
        strategySet.addStrategy(createNpmShrinkwrapStrategy(environment), StrategySearchOptions.defaultNested());
        strategySet.addStrategy(createNpmCliStrategy(environment), StrategySearchOptions.defaultNested());

        strategySet.yield(StrategyType.NPM_SHRINKWRAP, StrategyType.NPM_PACKAGELOCK);
        strategySet.yield(StrategyType.NPM_CLI, StrategyType.NPM_PACKAGELOCK);
        strategySet.yield(StrategyType.NPM_CLI, StrategyType.NPM_SHRINKWRAP);

        strategySet.yield(StrategyType.NPM_CLI, StrategyType.YARN_LOCK);
        strategySet.yield(StrategyType.NPM_PACKAGELOCK, StrategyType.YARN_LOCK);
        strategySet.yield(StrategyType.NPM_SHRINKWRAP, StrategyType.YARN_LOCK);

        strategySet.addStrategy(createNugetSolutionStrategy(environment), StrategySearchOptions.defaultNested());
        strategySet.addStrategy(createNugetProjectStrategy(environment), StrategySearchOptions.defaultNotNested());

        strategySet.yield(StrategyType.NUGET_PROJECT_INSPECTOR, StrategyType.NUGET_SOLUTION_INSPECTOR);

        strategySet.addStrategy(createComposerLockStrategy(environment), StrategySearchOptions.defaultNotNested());

        strategySet.addStrategy(createPipenvStrategy(environment), StrategySearchOptions.defaultNotNested());
        strategySet.addStrategy(createPipInspectorStrategy(environment), StrategySearchOptions.defaultNotNested());

        strategySet.yield(StrategyType.PIP_INSPECTOR, StrategyType.PIP_ENV);

        strategySet.addStrategy(createGemlockStrategy(environment), StrategySearchOptions.defaultNotNested());
        strategySet.addStrategy(createSbtResolutionCacheStrategy(environment), StrategySearchOptions.defaultNotNested());
        strategySet.addStrategy(createPearCliStrategy(environment), StrategySearchOptions.defaultNotNested());

        return strategySet;
    }

    private PodlockStrategy createCocoapodsStrategy(final StrategyEnvironment environment) {
        final PodlockStrategy strategy = new PodlockStrategy(environment, detectFileFinder, podlockExtractor);
        return strategy;
    }

    private CondaCliStrategy createCondaStrategy(final StrategyEnvironment environment) {
        final CondaCliStrategy strategy = new CondaCliStrategy(environment, detectFileFinder, standardExecutableFinder, condaCliExtractor);
        return strategy;
    }

    private CpanCliStrategy createCpanCliStrategy(final StrategyEnvironment environment) {
        final CpanCliStrategy strategy = new CpanCliStrategy(environment, detectFileFinder, standardExecutableFinder, cpanCliExtractor);
        return strategy;
    }

    private PackratLockStrategy createPackratLockStrategy(final StrategyEnvironment environment) {
        final PackratLockStrategy strategy = new PackratLockStrategy(environment, detectFileFinder, packratLockExtractor);
        return strategy;
    }

    private DockerStrategy createDockerStrategy(final StrategyEnvironment environment) {
        final String tar = detectConfiguration.getDockerTar();
        final String image = detectConfiguration.getDockerImage();
        final boolean dockerRequired = detectConfiguration.getDockerPathRequired();

        final DockerStrategy strategy = new DockerStrategy(environment, dockerInspectorManager, standardExecutableFinder, dockerRequired, image, tar, dockerExtractor);
        return strategy;
    }

    private GoCliStrategy createGoCliStrategy(final StrategyEnvironment environment) {
        final GoCliStrategy strategy = new GoCliStrategy(environment, detectFileFinder, standardExecutableFinder, goInspectorManager, goDepExtractor);
        return strategy;
    }

    private GoDepsStrategy createGoDepsStrategy(final StrategyEnvironment environment) {
        final GoDepsStrategy strategy = new GoDepsStrategy(environment, detectFileFinder, goDepsExtractor);
        return strategy;
    }

    private GoLockStrategy createGoLockStrategy(final StrategyEnvironment environment) {
        final GoLockStrategy strategy = new GoLockStrategy(environment, detectFileFinder, standardExecutableFinder, goInspectorManager, goDepExtractor);
        return strategy;
    }

    private GoVndrStrategy createGoVndrStrategy(final StrategyEnvironment environment) {
        final GoVndrStrategy strategy = new GoVndrStrategy(environment, detectFileFinder, goVndrExtractor);
        return strategy;
    }

    private GradleInspectorStrategy createGradleInspectorStrategy(final StrategyEnvironment environment) {
        final GradleInspectorStrategy strategy = new GradleInspectorStrategy(environment, detectFileFinder, gradleFinder, gradleInspectorManager, gradleInspectorExtractor);
        return strategy;
    }

    private RebarStrategy createRebarStrategy(final StrategyEnvironment environment) {
        final RebarStrategy strategy = new RebarStrategy(environment, detectFileFinder, standardExecutableFinder, rebarExtractor);
        return strategy;
    }

    private MavenPomStrategy createMavenPomStrategy(final StrategyEnvironment environment) {
        final MavenPomStrategy strategy = new MavenPomStrategy(environment, detectFileFinder, mavenExecutableFinder, mavenCliExtractor);
        return strategy;
    }

    private MavenPomWrapperStrategy createMavenPomWrapperStrategy(final StrategyEnvironment environment) {
        final MavenPomWrapperStrategy strategy = new MavenPomWrapperStrategy(environment, detectFileFinder, mavenExecutableFinder, mavenCliExtractor);
        return strategy;
    }

    private NpmCliStrategy createNpmCliStrategy(final StrategyEnvironment environment) {
        final NpmCliStrategy strategy = new NpmCliStrategy(environment, detectFileFinder, npmCliExtractor);
        return strategy;
    }

    private NpmPackageLockStrategy createNpmPackageLockStrategy(final StrategyEnvironment environment) {
        final NpmPackageLockStrategy strategy = new NpmPackageLockStrategy(environment, detectFileFinder, npmLockfileExtractor);
        return strategy;
    }

    private NpmShrinkwrapStrategy createNpmShrinkwrapStrategy(final StrategyEnvironment environment) {
        final NpmShrinkwrapStrategy strategy = new NpmShrinkwrapStrategy(environment, detectFileFinder, npmLockfileExtractor);
        return strategy;
    }

    private NugetSolutionStrategy createNugetSolutionStrategy(final StrategyEnvironment environment) {
        final NugetSolutionStrategy strategy = new NugetSolutionStrategy(environment, detectFileFinder, nugetInspectorManager, nugetInspectorExtractor);
        return strategy;
    }

    private NugetProjectStrategy createNugetProjectStrategy(final StrategyEnvironment environment) {
        final NugetProjectStrategy strategy = new NugetProjectStrategy(environment, detectFileFinder, nugetInspectorManager, nugetInspectorExtractor);
        return strategy;
    }

    private ComposerLockStrategy createComposerLockStrategy(final StrategyEnvironment environment) {
        final ComposerLockStrategy strategy = new ComposerLockStrategy(environment, detectFileFinder, composerLockExtractor);
        return strategy;
    }

    private PearCliStrategy createPearCliStrategy(final StrategyEnvironment environment) {
        final PearCliStrategy strategy = new PearCliStrategy(environment, detectFileFinder, standardExecutableFinder, pearCliExtractor);
        return strategy;
    }

    private PipenvStrategy createPipenvStrategy(final StrategyEnvironment environment) {
        final PipenvStrategy strategy = new PipenvStrategy(environment, detectFileFinder, pythonExecutableFinder, pipenvExtractor);
        return strategy;
    }

    private PipInspectorStrategy createPipInspectorStrategy(final StrategyEnvironment environment) {
        final String requirementsFile = detectConfiguration.getRequirementsFilePath();
        final PipInspectorStrategy strategy = new PipInspectorStrategy(environment, requirementsFile, detectFileFinder, pythonExecutableFinder, pipInspectorManager, pipInspectorExtractor);
        return strategy;
    }

    private GemlockStrategy createGemlockStrategy(final StrategyEnvironment environment) {
        final GemlockStrategy strategy = new GemlockStrategy(environment, detectFileFinder, gemlockExtractor);
        return strategy;
    }

    private SbtResolutionCacheStrategy createSbtResolutionCacheStrategy(final StrategyEnvironment environment) {
        final SbtResolutionCacheStrategy strategy = new SbtResolutionCacheStrategy(environment, detectFileFinder, sbtResolutionCacheExtractor);
        return strategy;
    }

    private YarnLockStrategy createYarnLockStrategy(final StrategyEnvironment environment) {
        final boolean productionDependenciesOnly = detectConfiguration.getYarnProductionDependenciesOnly();
        final YarnLockStrategy strategy = new YarnLockStrategy(environment, productionDependenciesOnly, detectFileFinder, standardExecutableFinder, yarnLockExtractor);
        return strategy;
    }
}
