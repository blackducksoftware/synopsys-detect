package com.synopsys.integration.detect.tool.detector.factory;

import com.synopsys.integration.detect.configuration.DetectableOptionFactory;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableResolver;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.inspector.GradleInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.PipInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.ProjectInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;
import com.synopsys.integration.detectable.detectables.bazel.BazelDetectable;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectable;
import com.synopsys.integration.detectable.detectables.cargo.CargoLockDetectable;
import com.synopsys.integration.detectable.detectables.carthage.CarthageLockDetectable;
import com.synopsys.integration.detectable.detectables.clang.ClangDetectable;
import com.synopsys.integration.detectable.detectables.cocoapods.PodlockDetectable;
import com.synopsys.integration.detectable.detectables.conan.cli.ConanCliDetectable;
import com.synopsys.integration.detectable.detectables.conan.lockfile.ConanLockfileDetectable;
import com.synopsys.integration.detectable.detectables.conda.CondaCliDetectable;
import com.synopsys.integration.detectable.detectables.cpan.CpanCliDetectable;
import com.synopsys.integration.detectable.detectables.cran.PackratLockDetectable;
import com.synopsys.integration.detectable.detectables.dart.pubdep.DartPubDepDetectable;
import com.synopsys.integration.detectable.detectables.dart.pubspec.DartPubSpecLockDetectable;
import com.synopsys.integration.detectable.detectables.docker.DockerDetectable;
import com.synopsys.integration.detectable.detectables.docker.DockerInspectorResolver;
import com.synopsys.integration.detectable.detectables.git.GitCliDetectable;
import com.synopsys.integration.detectable.detectables.git.GitParseDetectable;
import com.synopsys.integration.detectable.detectables.go.godep.GoDepLockDetectable;
import com.synopsys.integration.detectable.detectables.go.gogradle.GoGradleDetectable;
import com.synopsys.integration.detectable.detectables.go.gomod.GoModCliDetectable;
import com.synopsys.integration.detectable.detectables.go.vendor.GoVendorDetectable;
import com.synopsys.integration.detectable.detectables.go.vendr.GoVndrDetectable;
import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleInspectorDetectable;
import com.synopsys.integration.detectable.detectables.gradle.parsing.GradleProjectInspectorDetectable;
import com.synopsys.integration.detectable.detectables.ivy.IvyParseDetectable;
import com.synopsys.integration.detectable.detectables.lerna.LernaDetectable;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenPomDetectable;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenPomWrapperDetectable;
import com.synopsys.integration.detectable.detectables.maven.parsing.MavenProjectInspectorDetectable;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliDetectable;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmPackageLockDetectable;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmShrinkwrapDetectable;
import com.synopsys.integration.detectable.detectables.npm.packagejson.NpmPackageJsonParseDetectable;
import com.synopsys.integration.detectable.detectables.nuget.NugetProjectDetectable;
import com.synopsys.integration.detectable.detectables.nuget.NugetProjectInspectorDetectable;
import com.synopsys.integration.detectable.detectables.nuget.NugetSolutionDetectable;
import com.synopsys.integration.detectable.detectables.packagist.ComposerLockDetectable;
import com.synopsys.integration.detectable.detectables.pear.PearCliDetectable;
import com.synopsys.integration.detectable.detectables.pip.inspector.PipInspectorDetectable;
import com.synopsys.integration.detectable.detectables.pipenv.build.PipenvDetectable;
import com.synopsys.integration.detectable.detectables.pipenv.parse.PipfileLockDetectable;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.PnpmLockDetectable;
import com.synopsys.integration.detectable.detectables.poetry.PoetryDetectable;
import com.synopsys.integration.detectable.detectables.rebar.RebarDetectable;
import com.synopsys.integration.detectable.detectables.rubygems.gemlock.GemlockDetectable;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.GemspecParseDetectable;
import com.synopsys.integration.detectable.detectables.sbt.SbtDetectable;
import com.synopsys.integration.detectable.detectables.swift.cli.SwiftCliDetectable;
import com.synopsys.integration.detectable.detectables.swift.lock.SwiftPackageResolvedDetectable;
import com.synopsys.integration.detectable.detectables.xcode.XcodeProjectDetectable;
import com.synopsys.integration.detectable.detectables.xcode.XcodeWorkspaceDetectable;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockDetectable;
import com.synopsys.integration.detectable.factory.DetectableFactory;

public class DetectDetectableFactory {
    private final DetectableFactory detectableFactory;
    private final DetectableOptionFactory detectableOptionFactory;
    private final DetectExecutableResolver detectExecutableResolver;

    private final DockerInspectorResolver dockerInspectorResolver;
    private final GradleInspectorResolver gradleInspectorResolver;
    private final NugetInspectorResolver nugetInspectorResolver;
    private final PipInspectorResolver pipInspectorResolver;
    private final ProjectInspectorResolver projectInspectorResolver;

    public DetectDetectableFactory(
        DetectableFactory detectableFactory,
        DetectableOptionFactory detectableOptionFactory,
        DetectExecutableResolver detectExecutableResolver,
        DockerInspectorResolver dockerInspectorResolver,
        GradleInspectorResolver gradleInspectorResolver,
        NugetInspectorResolver nugetInspectorResolver,
        PipInspectorResolver pipInspectorResolver,
        ProjectInspectorResolver projectInspectorResolver
    ) {
        this.detectableFactory = detectableFactory;
        this.detectableOptionFactory = detectableOptionFactory;
        this.detectExecutableResolver = detectExecutableResolver;
        this.dockerInspectorResolver = dockerInspectorResolver;
        this.gradleInspectorResolver = gradleInspectorResolver;
        this.nugetInspectorResolver = nugetInspectorResolver;
        this.pipInspectorResolver = pipInspectorResolver;
        this.projectInspectorResolver = projectInspectorResolver;
    }

    public DockerDetectable createDockerDetectable(DetectableEnvironment environment) {
        return detectableFactory.createDockerDetectable(
            environment,
            detectableOptionFactory.createDockerDetectableOptions(),
            dockerInspectorResolver,
            detectExecutableResolver,
            detectExecutableResolver
        );
    }

    public BazelDetectable createBazelDetectable(DetectableEnvironment environment) {
        return detectableFactory.createBazelDetectable(environment, detectableOptionFactory.createBazelDetectableOptions(), detectExecutableResolver);
    }

    public BitbakeDetectable createBitbakeDetectable(DetectableEnvironment environment) {
        return detectableFactory.createBitbakeDetectable(environment, detectableOptionFactory.createBitbakeDetectableOptions(), detectExecutableResolver);
    }

    public CargoLockDetectable createCargoDetectable(DetectableEnvironment environment) {
        return detectableFactory.createCargoDetectable(environment);
    }

    public CarthageLockDetectable createCarthageDetectable(DetectableEnvironment environment) {
        return detectableFactory.createCarthageDetectable(environment);
    }

    public ClangDetectable createClangDetectable(DetectableEnvironment environment) {
        return detectableFactory.createClangDetectable(environment, detectableOptionFactory.createClangDetectableOptions());
    }

    public ComposerLockDetectable createComposerDetectable(DetectableEnvironment environment) {
        return detectableFactory.createComposerDetectable(environment, detectableOptionFactory.createComposerLockDetectableOptions());
    }

    public CondaCliDetectable createCondaCliDetectable(DetectableEnvironment environment) {
        return detectableFactory.createCondaCliDetectable(environment, detectExecutableResolver, detectableOptionFactory.createCondaOptions());
    }

    public CpanCliDetectable createCpanCliDetectable(DetectableEnvironment environment) {
        return detectableFactory.createCpanCliDetectable(environment, detectExecutableResolver, detectExecutableResolver);
    }

    public DartPubSpecLockDetectable createDartPubSpecLockDetectable(DetectableEnvironment environment) {
        return detectableFactory.createDartPubSpecLockDetectable(environment);
    }

    public DartPubDepDetectable createDartPubDepDetectable(DetectableEnvironment environment) {
        return detectableFactory.createDartPubDepDetectable(
            environment,
            detectableOptionFactory.createDartPubDepsDetectableOptions(),
            detectExecutableResolver,
            detectExecutableResolver
        );
    }

    public GemlockDetectable createGemlockDetectable(DetectableEnvironment environment) {
        return detectableFactory.createGemlockDetectable(environment);
    }

    public GitCliDetectable createGitDetectable(DetectableEnvironment environment) {
        return detectableFactory.createGitDetectable(environment, detectExecutableResolver);
    }

    public GitParseDetectable createGitParseDetectable(DetectableEnvironment environment) {
        return detectableFactory.createGitParseDetectable(environment);
    }

    public GoModCliDetectable createGoModCliDetectable(DetectableEnvironment environment) {
        return detectableFactory.createGoModCliDetectable(environment, detectExecutableResolver, detectableOptionFactory.createGoModCliDetectableOptions());
    }

    public GoDepLockDetectable createGoLockDetectable(DetectableEnvironment environment) {
        return detectableFactory.createGoLockDetectable(environment);
    }

    public GoVndrDetectable createGoVndrDetectable(DetectableEnvironment environment) {
        return detectableFactory.createGoVndrDetectable(environment);
    }

    public GoVendorDetectable createGoVendorDetectable(DetectableEnvironment environment) {
        return detectableFactory.createGoVendorDetectable(environment);
    }

    public GoGradleDetectable createGoGradleDetectable(DetectableEnvironment environment) {
        return detectableFactory.createGoGradleDetectable(environment);
    }

    public GradleInspectorDetectable createGradleDetectable(DetectableEnvironment environment) {
        return detectableFactory.createGradleDetectable(environment, detectableOptionFactory.createGradleInspectorOptions(), gradleInspectorResolver, detectExecutableResolver);
    }

    public GemspecParseDetectable createGemspecParseDetectable(DetectableEnvironment environment) {
        return detectableFactory.createGemspecParseDetectable(environment, detectableOptionFactory.createGemspecParseDetectableOptions());
    }

    public IvyParseDetectable createIvyParseDetectable(DetectableEnvironment environment) {
        return detectableFactory.createIvyParseDetectable(environment);
    }

    public MavenPomDetectable createMavenPomDetectable(DetectableEnvironment environment) {
        return detectableFactory.createMavenPomDetectable(environment, detectExecutableResolver, detectableOptionFactory.createMavenCliOptions());
    }

    public MavenPomWrapperDetectable createMavenPomWrapperDetectable(DetectableEnvironment environment) {
        return detectableFactory.createMavenPomWrapperDetectable(environment, detectExecutableResolver, detectableOptionFactory.createMavenCliOptions());
    }

    public ConanCliDetectable createConanCliDetectable(DetectableEnvironment environment) {
        return detectableFactory.createConanCliDetectable(environment, detectExecutableResolver, detectableOptionFactory.createConanCliOptions());
    }

    public ConanLockfileDetectable createConanLockfileDetectable(DetectableEnvironment environment) {
        return detectableFactory.createConanLockfileDetectable(environment, detectableOptionFactory.createConanLockfileOptions());
    }

    public NpmCliDetectable createNpmCliDetectable(DetectableEnvironment environment) {
        return detectableFactory.createNpmCliDetectable(environment, detectExecutableResolver, detectableOptionFactory.createNpmCliExtractorOptions());
    }

    public NpmPackageLockDetectable createNpmPackageLockDetectable(DetectableEnvironment environment) {
        return detectableFactory.createNpmPackageLockDetectable(environment, detectableOptionFactory.createNpmLockfileOptions());
    }

    public NugetProjectDetectable createNugetProjectDetectable(DetectableEnvironment environment) {
        return detectableFactory.createNugetProjectDetectable(environment, detectableOptionFactory.createNugetInspectorOptions(), nugetInspectorResolver);
    }

    public NpmShrinkwrapDetectable createNpmShrinkwrapDetectable(DetectableEnvironment environment) {
        return detectableFactory.createNpmShrinkwrapDetectable(environment, detectableOptionFactory.createNpmLockfileOptions());
    }

    public NpmPackageJsonParseDetectable createNpmPackageJsonParseDetectable(DetectableEnvironment environment) {
        return detectableFactory.createNpmPackageJsonParseDetectable(environment, detectableOptionFactory.createNpmPackageJsonParseDetectableOptions());
    }

    public NugetSolutionDetectable createNugetSolutionDetectable(DetectableEnvironment environment) {
        return detectableFactory.createNugetSolutionDetectable(environment, detectableOptionFactory.createNugetInspectorOptions(), nugetInspectorResolver);
    }

    public PackratLockDetectable createPackratLockDetectable(DetectableEnvironment environment) {
        return detectableFactory.createPackratLockDetectable(environment);
    }

    public PearCliDetectable createPearCliDetectable(DetectableEnvironment environment) {
        return detectableFactory.createPearCliDetectable(environment, detectableOptionFactory.createPearCliDetectableOptions(), detectExecutableResolver);
    }

    public PipenvDetectable createPipenvDetectable(DetectableEnvironment environment) {
        return detectableFactory.createPipenvDetectable(environment, detectableOptionFactory.createPipenvDetectableOptions(), detectExecutableResolver, detectExecutableResolver);
    }

    public PipfileLockDetectable createPipfileLockDetectable(DetectableEnvironment environment) {
        return detectableFactory.createPipfileLockDetectable(environment, detectableOptionFactory.createPipfileLockDetectableOptions());
    }

    public PipInspectorDetectable createPipInspectorDetectable(DetectableEnvironment environment) {
        return detectableFactory.createPipInspectorDetectable(
            environment,
            detectableOptionFactory.createPipInspectorDetectableOptions(),
            pipInspectorResolver,
            detectExecutableResolver,
            detectExecutableResolver
        );
    }

    public PnpmLockDetectable createPnpmLockDetectable(DetectableEnvironment environment) {
        return detectableFactory.createPnpmLockDetectable(environment, detectableOptionFactory.createPnpmLockOptions());
    }

    public PodlockDetectable createPodLockDetectable(DetectableEnvironment environment) {
        return detectableFactory.createPodLockDetectable(environment);
    }

    public PoetryDetectable createPoetryDetectable(DetectableEnvironment environment) {
        return detectableFactory.createPoetryDetectable(environment);
    }

    public RebarDetectable createRebarDetectable(DetectableEnvironment environment) {
        return detectableFactory.createRebarDetectable(environment, detectExecutableResolver);
    }

    public SbtDetectable createSbtDetectable(DetectableEnvironment environment) {
        return detectableFactory.createSbtDetectable(environment, detectExecutableResolver, detectableOptionFactory.createSbtDetectableOptions());
    }

    public SwiftCliDetectable createSwiftCliDetectable(DetectableEnvironment environment) {
        return detectableFactory.createSwiftCliDetectable(environment, detectExecutableResolver);
    }

    public YarnLockDetectable createYarnLockDetectable(DetectableEnvironment environment) {
        return detectableFactory.createYarnLockDetectable(environment, detectableOptionFactory.createYarnLockOptions());
    }

    public LernaDetectable createLernaDetectable(DetectableEnvironment environment) {
        return detectableFactory.createLernaDetectable(
            environment,
            detectExecutableResolver,
            detectableOptionFactory.createNpmLockfileOptions(),
            detectableOptionFactory.createLernaOptions(),
            detectableOptionFactory.createYarnLockOptions()
        );
    }

    public NugetProjectInspectorDetectable createNugetParseDetectable(DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createNugetParseDetectable(
            detectableEnvironment,
            projectInspectorResolver,
            detectableOptionFactory.createProjectInspectorOptions()
        );
    }

    public MavenProjectInspectorDetectable createMavenProjectInspectorDetectable(DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createMavenProjectInspectorDetectable(
            detectableEnvironment,
            projectInspectorResolver,
            detectableOptionFactory.createProjectInspectorOptions()
        );
    }

    public GradleProjectInspectorDetectable createGradleProjectInspectorDetectable(DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createMavenGradleInspectorDetectable(detectableEnvironment, projectInspectorResolver, detectableOptionFactory.createProjectInspectorOptions());
    }

    public SwiftPackageResolvedDetectable createSwiftPackageResolvedDetectable(DetectableEnvironment environment) {
        return detectableFactory.createSwiftPackageResolvedDetectable(environment);
    }

    public XcodeWorkspaceDetectable createXcodeWorkspaceDetectable(DetectableEnvironment environment) {
        return detectableFactory.createXcodeWorkspaceDetectable(environment);
    }

    public XcodeProjectDetectable createXcodeProjectDetectable(DetectableEnvironment environment) {
        return detectableFactory.createXcodeProjectDetectable(environment);
    }
}
