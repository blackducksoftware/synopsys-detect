/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.factory;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.BdioTransformer;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.common.util.parse.CommandParser;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.enums.DependencyType;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.resolver.*;
import com.synopsys.integration.detectable.detectable.inspector.GradleInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.PipInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.ProjectInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorOptions;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;
import com.synopsys.integration.detectable.detectables.bazel.BazelDetectable;
import com.synopsys.integration.detectable.detectables.bazel.BazelDetectableOptions;
import com.synopsys.integration.detectable.detectables.bazel.BazelExtractor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.WorkspaceRuleChooser;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectable;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectableOptions;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeExtractor;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeRecipesToLayerMapConverter;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeGraphTransformer;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeRecipesParser;
import com.synopsys.integration.detectable.detectables.bitbake.parse.GraphParserTransformer;
import com.synopsys.integration.detectable.detectables.cargo.CargoDetectable;
import com.synopsys.integration.detectable.detectables.cargo.CargoExtractor;
import com.synopsys.integration.detectable.detectables.cargo.parse.CargoLockParser;
import com.synopsys.integration.detectable.detectables.carthage.CartfileResolvedDependencyDeclarationParser;
import com.synopsys.integration.detectable.detectables.carthage.CarthageDetectable;
import com.synopsys.integration.detectable.detectables.carthage.CarthageExtractor;
import com.synopsys.integration.detectable.detectables.clang.*;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommandDatabaseParser;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommandParser;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.ClangPackageDetailsTransformer;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.DependencyFileDetailGenerator;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.DependencyListFileParser;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.FilePathGenerator;
import com.synopsys.integration.detectable.detectables.clang.linux.LinuxDistro;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerFactory;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfoFactory;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerRunner;
import com.synopsys.integration.detectable.detectables.cocoapods.PodlockDetectable;
import com.synopsys.integration.detectable.detectables.cocoapods.PodlockExtractor;
import com.synopsys.integration.detectable.detectables.cocoapods.parser.PodlockParser;
import com.synopsys.integration.detectable.detectables.conan.ConanCodeLocationGenerator;
import com.synopsys.integration.detectable.detectables.conan.cli.ConanCliDetectable;
import com.synopsys.integration.detectable.detectables.conan.cli.ConanCliExtractor;
import com.synopsys.integration.detectable.detectables.conan.cli.ConanCliExtractorOptions;
import com.synopsys.integration.detectable.detectables.conan.cli.ConanResolver;
import com.synopsys.integration.detectable.detectables.conan.cli.parser.ConanInfoLineAnalyzer;
import com.synopsys.integration.detectable.detectables.conan.cli.parser.ConanInfoNodeParser;
import com.synopsys.integration.detectable.detectables.conan.cli.parser.ConanInfoParser;
import com.synopsys.integration.detectable.detectables.conan.cli.parser.element.NodeElementParser;
import com.synopsys.integration.detectable.detectables.conan.lockfile.ConanLockfileDetectable;
import com.synopsys.integration.detectable.detectables.conan.lockfile.ConanLockfileExtractor;
import com.synopsys.integration.detectable.detectables.conan.lockfile.ConanLockfileExtractorOptions;
import com.synopsys.integration.detectable.detectables.conan.lockfile.parser.ConanLockfileParser;
import com.synopsys.integration.detectable.detectables.conda.CondaCliDetectable;
import com.synopsys.integration.detectable.detectables.conda.CondaCliDetectableOptions;
import com.synopsys.integration.detectable.detectables.conda.CondaCliExtractor;
import com.synopsys.integration.detectable.detectables.conda.parser.CondaDependencyCreator;
import com.synopsys.integration.detectable.detectables.conda.parser.CondaListParser;
import com.synopsys.integration.detectable.detectables.cpan.CpanCliDetectable;
import com.synopsys.integration.detectable.detectables.cpan.CpanCliExtractor;
import com.synopsys.integration.detectable.detectables.cpan.parse.CpanListParser;
import com.synopsys.integration.detectable.detectables.cran.PackratLockDetectable;
import com.synopsys.integration.detectable.detectables.cran.PackratLockExtractor;
import com.synopsys.integration.detectable.detectables.cran.parse.PackratDescriptionFileParser;
import com.synopsys.integration.detectable.detectables.cran.parse.PackratLockFileParser;
import com.synopsys.integration.detectable.detectables.dart.PubSpecYamlNameVersionParser;
import com.synopsys.integration.detectable.detectables.dart.pubdep.DartPubDepDetectable;
import com.synopsys.integration.detectable.detectables.dart.pubdep.DartPubDepsDetectableOptions;
import com.synopsys.integration.detectable.detectables.dart.pubdep.PubDepsExtractor;
import com.synopsys.integration.detectable.detectables.dart.pubdep.PubDepsParser;
import com.synopsys.integration.detectable.detectables.dart.pubspec.DartPubSpecLockDetectable;
import com.synopsys.integration.detectable.detectables.dart.pubspec.PubSpecExtractor;
import com.synopsys.integration.detectable.detectables.dart.pubspec.PubSpecLockParser;
import com.synopsys.integration.detectable.detectables.docker.DockerDetectable;
import com.synopsys.integration.detectable.detectables.docker.DockerDetectableOptions;
import com.synopsys.integration.detectable.detectables.docker.DockerExtractor;
import com.synopsys.integration.detectable.detectables.docker.DockerInspectorResolver;
import com.synopsys.integration.detectable.detectables.git.GitDetectable;
import com.synopsys.integration.detectable.detectables.git.GitParseDetectable;
import com.synopsys.integration.detectable.detectables.git.cli.GitCliExtractor;
import com.synopsys.integration.detectable.detectables.git.cli.GitUrlParser;
import com.synopsys.integration.detectable.detectables.git.parsing.GitParseExtractor;
import com.synopsys.integration.detectable.detectables.git.parsing.parse.GitConfigNameVersionTransformer;
import com.synopsys.integration.detectable.detectables.git.parsing.parse.GitConfigNodeTransformer;
import com.synopsys.integration.detectable.detectables.git.parsing.parse.GitFileParser;
import com.synopsys.integration.detectable.detectables.go.godep.GoDepExtractor;
import com.synopsys.integration.detectable.detectables.go.godep.GoDepLockDetectable;
import com.synopsys.integration.detectable.detectables.go.godep.parse.GoLockParser;
import com.synopsys.integration.detectable.detectables.go.gogradle.GoGradleDetectable;
import com.synopsys.integration.detectable.detectables.go.gogradle.GoGradleExtractor;
import com.synopsys.integration.detectable.detectables.go.gogradle.GoGradleLockParser;
import com.synopsys.integration.detectable.detectables.go.gomod.GoModCliDetectable;
import com.synopsys.integration.detectable.detectables.go.gomod.GoModCliDetectableOptions;
import com.synopsys.integration.detectable.detectables.go.gomod.GoModCliExtractor;
import com.synopsys.integration.detectable.detectables.go.gomod.GoModCommandExecutor;
import com.synopsys.integration.detectable.detectables.go.gomod.parse.GoGraphParser;
import com.synopsys.integration.detectable.detectables.go.gomod.parse.GoListParser;
import com.synopsys.integration.detectable.detectables.go.gomod.parse.GoModWhyParser;
import com.synopsys.integration.detectable.detectables.go.gomod.process.GoModGraphGenerator;
import com.synopsys.integration.detectable.detectables.go.vendor.GoVendorDetectable;
import com.synopsys.integration.detectable.detectables.go.vendor.GoVendorExtractor;
import com.synopsys.integration.detectable.detectables.go.vendr.GoVndrDetectable;
import com.synopsys.integration.detectable.detectables.go.vendr.GoVndrExtractor;
import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleDetectable;
import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleInspectorExtractor;
import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleInspectorOptions;
import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleRunner;
import com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleReportParser;
import com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleReportTransformer;
import com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleRootMetadataParser;
import com.synopsys.integration.detectable.detectables.gradle.parsing.GradleProjectInspectorDetectable;
import com.synopsys.integration.detectable.detectables.lerna.*;
import com.synopsys.integration.detectable.detectables.maven.cli.*;
import com.synopsys.integration.detectable.detectables.maven.parsing.MavenParseDetectable;
import com.synopsys.integration.detectable.detectables.maven.parsing.MavenParseExtractor;
import com.synopsys.integration.detectable.detectables.maven.parsing.MavenParseOptions;
import com.synopsys.integration.detectable.detectables.maven.parsing.MavenProjectInspectorDetectable;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliDetectable;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliExtractor;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliExtractorOptions;
import com.synopsys.integration.detectable.detectables.npm.cli.parse.NpmCliParser;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmLockfileExtractor;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmLockfileOptions;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmPackageLockDetectable;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmShrinkwrapDetectable;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockfilePackager;
import com.synopsys.integration.detectable.detectables.npm.packagejson.NpmPackageJsonParseDetectable;
import com.synopsys.integration.detectable.detectables.npm.packagejson.NpmPackageJsonParseDetectableOptions;
import com.synopsys.integration.detectable.detectables.npm.packagejson.PackageJsonExtractor;
import com.synopsys.integration.detectable.detectables.nuget.NugetInspectorExtractor;
import com.synopsys.integration.detectable.detectables.nuget.NugetProjectDetectable;
import com.synopsys.integration.detectable.detectables.nuget.NugetProjectInspectorDetectable;
import com.synopsys.integration.detectable.detectables.nuget.NugetSolutionDetectable;
import com.synopsys.integration.detectable.detectables.nuget.parse.NugetInspectorParser;
import com.synopsys.integration.detectable.detectables.packagist.ComposerLockDetectable;
import com.synopsys.integration.detectable.detectables.packagist.ComposerLockDetectableOptions;
import com.synopsys.integration.detectable.detectables.packagist.ComposerLockExtractor;
import com.synopsys.integration.detectable.detectables.packagist.parse.PackagistParser;
import com.synopsys.integration.detectable.detectables.pear.PearCliDetectable;
import com.synopsys.integration.detectable.detectables.pear.PearCliDetectableOptions;
import com.synopsys.integration.detectable.detectables.pear.PearCliExtractor;
import com.synopsys.integration.detectable.detectables.pear.parse.PearListParser;
import com.synopsys.integration.detectable.detectables.pear.parse.PearPackageDependenciesParser;
import com.synopsys.integration.detectable.detectables.pear.parse.PearPackageXmlParser;
import com.synopsys.integration.detectable.detectables.pear.transform.PearDependencyGraphTransformer;
import com.synopsys.integration.detectable.detectables.pip.inspector.PipInspectorDetectable;
import com.synopsys.integration.detectable.detectables.pip.inspector.PipInspectorDetectableOptions;
import com.synopsys.integration.detectable.detectables.pip.inspector.PipInspectorExtractor;
import com.synopsys.integration.detectable.detectables.pip.inspector.parser.PipInspectorTreeParser;
import com.synopsys.integration.detectable.detectables.pipenv.PipenvDetectable;
import com.synopsys.integration.detectable.detectables.pipenv.PipenvDetectableOptions;
import com.synopsys.integration.detectable.detectables.pipenv.PipenvExtractor;
import com.synopsys.integration.detectable.detectables.pipenv.parser.PipEnvJsonGraphParser;
import com.synopsys.integration.detectable.detectables.pipenv.parser.PipenvFreezeParser;
import com.synopsys.integration.detectable.detectables.pipenv.parser.PipenvTransformer;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.PnpmLockDetectable;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.PnpmLockExtractor;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.PnpmLockYamlParser;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.PnpmYamlTransformer;
import com.synopsys.integration.detectable.detectables.poetry.PoetryDetectable;
import com.synopsys.integration.detectable.detectables.poetry.PoetryExtractor;
import com.synopsys.integration.detectable.detectables.poetry.parser.PoetryLockParser;
import com.synopsys.integration.detectable.detectables.poetry.parser.ToolPoetrySectionParser;
import com.synopsys.integration.detectable.detectables.projectinspector.ProjectInspectorExtractor;
import com.synopsys.integration.detectable.detectables.projectinspector.ProjectInspectorOptions;
import com.synopsys.integration.detectable.detectables.projectinspector.ProjectInspectorParser;
import com.synopsys.integration.detectable.detectables.rebar.RebarDetectable;
import com.synopsys.integration.detectable.detectables.rebar.RebarExtractor;
import com.synopsys.integration.detectable.detectables.rebar.parse.Rebar3TreeParser;
import com.synopsys.integration.detectable.detectables.rubygems.gemlock.GemlockDetectable;
import com.synopsys.integration.detectable.detectables.rubygems.gemlock.GemlockExtractor;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.GemspecParseDetectable;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.GemspecParseDetectableOptions;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.GemspecParseExtractor;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.parse.GemspecLineParser;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.parse.GemspecParser;
import com.synopsys.integration.detectable.detectables.sbt.SbtDetectable;
import com.synopsys.integration.detectable.detectables.sbt.dot.*;
import com.synopsys.integration.detectable.detectables.sbt.parse.SbtResolutionCacheExtractor;
import com.synopsys.integration.detectable.detectables.sbt.parse.SbtResolutionCacheOptions;
import com.synopsys.integration.detectable.detectables.swift.SwiftCliDetectable;
import com.synopsys.integration.detectable.detectables.swift.SwiftCliParser;
import com.synopsys.integration.detectable.detectables.swift.SwiftExtractor;
import com.synopsys.integration.detectable.detectables.swift.SwiftPackageTransformer;
import com.synopsys.integration.detectable.detectables.yarn.*;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonFiles;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonReader;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.section.YarnLockDependencySpecParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.section.YarnLockEntrySectionParserSet;
import com.synopsys.integration.detectable.util.ToolVersionLogger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.List;

/*
 Entry point for creating detectables using most
 */
public class DetectableFactory {

    private final FileFinder fileFinder;
    private final DetectableExecutableRunner executableRunner;
    private final ExternalIdFactory externalIdFactory;
    private final Gson gson;
    private final ToolVersionLogger toolVersionLogger;

    public DetectableFactory(FileFinder fileFinder, DetectableExecutableRunner executableRunner, ExternalIdFactory externalIdFactory, Gson gson) {
        this.fileFinder = fileFinder;
        this.executableRunner = executableRunner;
        this.externalIdFactory = externalIdFactory;
        this.gson = gson;
        this.toolVersionLogger = new ToolVersionLogger(executableRunner);
    }

    //#region Detectables

    //Detectables
    //Should be scoped to Prototype so a new Detectable is created every time one is needed.
    //Should only be accessed through the DetectableFactory.

    public DockerDetectable createDockerDetectable(DetectableEnvironment environment, DockerDetectableOptions dockerDetectableOptions, DockerInspectorResolver dockerInspectorResolver, JavaResolver javaResolver,
        DockerResolver dockerResolver) {
        return new DockerDetectable(environment, dockerInspectorResolver, javaResolver, dockerResolver, dockerExtractor(), dockerDetectableOptions);
    }

    public BazelDetectable createBazelDetectable(DetectableEnvironment environment, BazelDetectableOptions bazelDetectableOptions, BazelResolver bazelResolver) {
        return new BazelDetectable(environment, fileFinder, bazelExtractor(), bazelResolver, bazelDetectableOptions);
    }

    public BitbakeDetectable createBitbakeDetectable(DetectableEnvironment environment, BitbakeDetectableOptions bitbakeDetectableOptions, BashResolver bashResolver) {
        return new BitbakeDetectable(environment, fileFinder, bitbakeDetectableOptions, bitbakeExtractor(), bashResolver);
    }

    public CargoDetectable createCargoDetectable(DetectableEnvironment environment) {
        return new CargoDetectable(environment, fileFinder, cargoExtractor());
    }

    public CarthageDetectable createCarthageDetectable(DetectableEnvironment environment) {
        return new CarthageDetectable(environment, fileFinder, carthageExtractor());
    }

    public ClangDetectable createClangDetectable(DetectableEnvironment environment, ClangDetectableOptions clangDetectableOptions) {
        return new ClangDetectable(environment, executableRunner, fileFinder, clangPackageManagerFactory().createPackageManagers(), clangExtractor(), clangDetectableOptions, clangPackageManagerRunner());
    }

    public ComposerLockDetectable createComposerDetectable(DetectableEnvironment environment, ComposerLockDetectableOptions composerLockDetectableOptions) {
        return new ComposerLockDetectable(environment, fileFinder, composerLockExtractor(), composerLockDetectableOptions);
    }

    public CondaCliDetectable createCondaCliDetectable(DetectableEnvironment environment, CondaResolver condaResolver, CondaCliDetectableOptions condaCliDetectableOptions) {
        return new CondaCliDetectable(environment, fileFinder, condaResolver, condaCliExtractor(), condaCliDetectableOptions);
    }

    public CpanCliDetectable createCpanCliDetectable(DetectableEnvironment environment, CpanResolver cpanResolver, CpanmResolver cpanmResolver) {
        return new CpanCliDetectable(environment, fileFinder, cpanResolver, cpanmResolver, cpanCliExtractor());
    }

    public DartPubSpecLockDetectable createDartPubSpecLockDetectable(DetectableEnvironment environment) {
        return new DartPubSpecLockDetectable(environment, fileFinder, pubSpecExtractor());
    }

    public DartPubDepDetectable createDartPubDepDetectable(DetectableEnvironment environment, DartPubDepsDetectableOptions dartPubDepsDetectableOptions, DartResolver dartResolver, FlutterResolver flutterResolver) {
        return new DartPubDepDetectable(environment, fileFinder, pubDepsExtractor(), dartPubDepsDetectableOptions, dartResolver, flutterResolver);
    }

    public GemlockDetectable createGemlockDetectable(DetectableEnvironment environment) {
        return new GemlockDetectable(environment, fileFinder, gemlockExtractor());
    }

    public GitDetectable createGitDetectable(DetectableEnvironment environment, GitResolver gitResolver) {
        return new GitDetectable(environment, fileFinder, gitCliExtractor(), gitResolver, gitParseExtractor());
    }

    public GitParseDetectable createGitParseDetectable(DetectableEnvironment environment) {
        return new GitParseDetectable(environment, fileFinder, gitParseExtractor());
    }

    public GoModCliDetectable createGoModCliDetectable(DetectableEnvironment environment, GoResolver goResolver, GoModCliDetectableOptions goModCliDetectableOptions) {
        return new GoModCliDetectable(environment, fileFinder, goResolver, goModCliExtractor(), goModCliDetectableOptions);
    }

    public GoDepLockDetectable createGoLockDetectable(DetectableEnvironment environment) {
        return new GoDepLockDetectable(environment, fileFinder, goDepExtractor());
    }

    public GoVndrDetectable createGoVndrDetectable(DetectableEnvironment environment) {
        return new GoVndrDetectable(environment, fileFinder, goVndrExtractor());
    }

    public GoVendorDetectable createGoVendorDetectable(DetectableEnvironment environment) {
        return new GoVendorDetectable(environment, fileFinder, goVendorExtractor());
    }

    public GoGradleDetectable createGoGradleDetectable(DetectableEnvironment environment) {
        return new GoGradleDetectable(environment, fileFinder, goGradleExtractor());
    }

    public GradleDetectable createGradleDetectable(DetectableEnvironment environment, GradleInspectorOptions gradleInspectorOptions, GradleInspectorResolver gradleInspectorResolver, GradleResolver gradleResolver) {
        return new GradleDetectable(environment, fileFinder, gradleResolver, gradleInspectorResolver, gradleInspectorExtractor(gradleInspectorOptions), gradleInspectorOptions);
    }

    public GradleProjectInspectorDetectable createMavenGradleInspectorDetectable(DetectableEnvironment detectableEnvironment, ProjectInspectorResolver projectInspectorResolver, ProjectInspectorOptions projectInspectorOptions) {
        return new GradleProjectInspectorDetectable(detectableEnvironment, fileFinder, projectInspectorResolver, projectInspectorExtractor(), projectInspectorOptions);
    }

    public GemspecParseDetectable createGemspecParseDetectable(DetectableEnvironment environment, GemspecParseDetectableOptions gemspecParseDetectableOptions) {
        return new GemspecParseDetectable(environment, fileFinder, gemspecExtractor(), gemspecParseDetectableOptions);
    }

    public MavenPomDetectable createMavenPomDetectable(DetectableEnvironment environment, MavenResolver mavenResolver, MavenCliExtractorOptions mavenCliExtractorOptions) {
        return new MavenPomDetectable(environment, fileFinder, mavenResolver, mavenCliExtractor(), mavenCliExtractorOptions);
    }

    public MavenPomWrapperDetectable createMavenPomWrapperDetectable(DetectableEnvironment environment, MavenResolver mavenResolver, MavenCliExtractorOptions mavenCliExtractorOptions) {
        return new MavenPomWrapperDetectable(environment, fileFinder, mavenResolver, mavenCliExtractor(), mavenCliExtractorOptions);
    }

    public MavenParseDetectable createMavenParseDetectable(DetectableEnvironment environment, MavenParseOptions mavenParseOptions) {
        return new MavenParseDetectable(environment, fileFinder, mavenParseExtractor(), mavenParseOptions);
    }

    public MavenProjectInspectorDetectable createMavenProjectInspectorDetectable(DetectableEnvironment detectableEnvironment, ProjectInspectorResolver projectInspectorResolver, MavenParseOptions mavenParseOptions,
        ProjectInspectorOptions projectInspectorOptions) {
        return new MavenProjectInspectorDetectable(detectableEnvironment, fileFinder, projectInspectorResolver, projectInspectorExtractor(), mavenParseOptions, projectInspectorOptions);
    }

    public ConanLockfileDetectable createConanLockfileDetectable(DetectableEnvironment environment, ConanLockfileExtractorOptions conanLockfileExtractorOptions) {
        return new ConanLockfileDetectable(environment, fileFinder, conanLockfileExtractor(), conanLockfileExtractorOptions);
    }

    public ConanCliDetectable createConanCliDetectable(DetectableEnvironment environment, ConanResolver conanResolver, ConanCliExtractorOptions conanCliExtractorOptions) {
        return new ConanCliDetectable(environment, fileFinder, conanResolver, conanCliExtractor(), conanCliExtractorOptions);
    }

    public NpmCliDetectable createNpmCliDetectable(DetectableEnvironment environment, NpmResolver npmResolver, NpmCliExtractorOptions npmCliExtractorOptions) {
        return new NpmCliDetectable(environment, fileFinder, npmResolver, npmCliExtractor(), npmCliExtractorOptions);
    }

    public NpmPackageLockDetectable createNpmPackageLockDetectable(DetectableEnvironment environment, NpmLockfileOptions npmLockfileOptions) {
        return new NpmPackageLockDetectable(environment, fileFinder, npmLockfileExtractor(), npmLockfileOptions);
    }

    public NugetProjectDetectable createNugetProjectDetectable(DetectableEnvironment environment, NugetInspectorOptions nugetInspectorOptions, NugetInspectorResolver nugetInspectorResolver) {
        return new NugetProjectDetectable(environment, fileFinder, nugetInspectorOptions, nugetInspectorResolver, nugetInspectorExtractor());
    }

    public NpmShrinkwrapDetectable createNpmShrinkwrapDetectable(DetectableEnvironment environment, NpmLockfileOptions npmLockfileOptions) {
        return new NpmShrinkwrapDetectable(environment, fileFinder, npmLockfileExtractor(), npmLockfileOptions);
    }

    public NpmPackageJsonParseDetectable createNpmPackageJsonParseDetectable(DetectableEnvironment environment, NpmPackageJsonParseDetectableOptions npmPackageJsonParseDetectableOptions) {
        return new NpmPackageJsonParseDetectable(environment, fileFinder, packageJsonExtractor(), npmPackageJsonParseDetectableOptions);
    }

    public NugetSolutionDetectable createNugetSolutionDetectable(DetectableEnvironment environment, NugetInspectorOptions nugetInspectorOptions, NugetInspectorResolver nugetInspectorResolver) {
        return new NugetSolutionDetectable(environment, fileFinder, nugetInspectorResolver, nugetInspectorExtractor(), nugetInspectorOptions);
    }

    public NugetProjectInspectorDetectable createNugetParseDetectable(DetectableEnvironment environment, NugetInspectorOptions nugetInspectorOptions, ProjectInspectorResolver projectInspectorResolver,
        ProjectInspectorOptions projectInspectorOptions) {
        return new NugetProjectInspectorDetectable(environment, fileFinder, nugetInspectorOptions, projectInspectorResolver, projectInspectorExtractor(), projectInspectorOptions);
    }

    public PackratLockDetectable createPackratLockDetectable(DetectableEnvironment environment) {
        return new PackratLockDetectable(environment, fileFinder, packratLockExtractor());
    }

    public PearCliDetectable createPearCliDetectable(DetectableEnvironment environment, PearCliDetectableOptions pearCliDetectableOptions, PearResolver pearResolver) {
        return new PearCliDetectable(environment, fileFinder, pearResolver, pearCliExtractor(), pearCliDetectableOptions);
    }

    public PipenvDetectable createPipenvDetectable(DetectableEnvironment environment, PipenvDetectableOptions pipenvDetectableOptions, PythonResolver pythonResolver, PipenvResolver pipenvResolver) {
        return new PipenvDetectable(environment, pipenvDetectableOptions, fileFinder, pythonResolver, pipenvResolver, pipenvExtractor());
    }

    public PipInspectorDetectable createPipInspectorDetectable(DetectableEnvironment environment, PipInspectorDetectableOptions pipInspectorDetectableOptions, PipInspectorResolver pipInspectorResolver,
        PythonResolver pythonResolver,
        PipResolver pipResolver) {
        return new PipInspectorDetectable(environment, fileFinder, pythonResolver, pipResolver, pipInspectorResolver, pipInspectorExtractor(), pipInspectorDetectableOptions);
    }

    public PnpmLockDetectable createPnpmLockDetectable(DetectableEnvironment environment, List<DependencyType> dependencyTypes) {
        return new PnpmLockDetectable(environment, fileFinder, pnpmLockExtractor(), dependencyTypes, packageJsonFiles());
    }

    public PodlockDetectable createPodLockDetectable(DetectableEnvironment environment) {
        return new PodlockDetectable(environment, fileFinder, podlockExtractor());
    }

    public PoetryDetectable createPoetryDetectable(DetectableEnvironment environment) {
        return new PoetryDetectable(environment, fileFinder, poetryExtractor(), toolPoetrySectionParser());
    }

    public RebarDetectable createRebarDetectable(DetectableEnvironment environment, Rebar3Resolver rebar3Resolver) {
        return new RebarDetectable(environment, fileFinder, rebar3Resolver, rebarExtractor());
    }

    public SbtDetectable createSbtDetectable(DetectableEnvironment environment, SbtResolver sbtResolver, SbtResolutionCacheOptions sbtResolutionCacheOptions) {
        return new SbtDetectable(environment, fileFinder, sbtResolutionCacheExtractor(), sbtResolutionCacheOptions, sbtResolver, sbtDotExtractor(), sbtPluginFinder());
    }

    public SwiftCliDetectable createSwiftCliDetectable(DetectableEnvironment environment, SwiftResolver swiftResolver) {
        return new SwiftCliDetectable(environment, fileFinder, swiftExtractor(), swiftResolver);
    }

    public YarnLockDetectable createYarnLockDetectable(DetectableEnvironment environment, YarnLockOptions yarnLockOptions) {
        return new YarnLockDetectable(environment, fileFinder, yarnLockExtractor(yarnLockOptions));
    }

    public LernaDetectable createLernaDetectable(DetectableEnvironment environment, LernaResolver lernaResolver, NpmLockfileOptions npmLockfileOptions, YarnLockOptions yarnLockOptions, LernaOptions lernaOptions) {
        return new LernaDetectable(environment, fileFinder, lernaResolver, lernaExtractor(npmLockfileOptions, yarnLockOptions, lernaOptions));
    }

    //#endregion

    //#region Utility

    private BazelExtractor bazelExtractor() {
        WorkspaceRuleChooser workspaceRuleChooser = new WorkspaceRuleChooser();
        return new BazelExtractor(executableRunner, externalIdFactory, workspaceRuleChooser);
    }

    private FilePathGenerator filePathGenerator() {
        return new FilePathGenerator(executableRunner, compileCommandParser(), dependenyListFileParser());
    }

    private DependencyListFileParser dependenyListFileParser() {
        return new DependencyListFileParser();
    }

    private DependencyFileDetailGenerator dependencyFileDetailGenerator() {
        return new DependencyFileDetailGenerator(filePathGenerator());
    }

    private CargoExtractor cargoExtractor() {
        return new CargoExtractor(new CargoLockParser());
    }

    private CarthageExtractor carthageExtractor() {
        return new CarthageExtractor(new CartfileResolvedDependencyDeclarationParser());
    }

    private ClangPackageDetailsTransformer clangPackageDetailsTransformer() {
        return new ClangPackageDetailsTransformer(externalIdFactory);
    }

    private ForgeChooser forgeChooser() {
        LinuxDistroToForgeMapper forgeGenerator = new LinuxDistroToForgeMapper();
        LinuxDistro linuxDistro = new LinuxDistro();
        return new ForgeChooser(forgeGenerator, linuxDistro);
    }

    private CompileCommandDatabaseParser compileCommandDatabaseParser() {
        return new CompileCommandDatabaseParser(gson);
    }

    private ClangExtractor clangExtractor() {
        return new ClangExtractor(executableRunner, dependencyFileDetailGenerator(), clangPackageDetailsTransformer(), compileCommandDatabaseParser(), forgeChooser());
    }

    private PodlockParser podlockParser() {
        return new PodlockParser(externalIdFactory);
    }

    private PodlockExtractor podlockExtractor() {
        return new PodlockExtractor(podlockParser());
    }

    private CondaListParser condaListParser() {
        return new CondaListParser(gson, condaDependencyCreator());
    }

    private CondaDependencyCreator condaDependencyCreator() {
        return new CondaDependencyCreator(externalIdFactory);
    }

    private CondaCliExtractor condaCliExtractor() {
        return new CondaCliExtractor(condaListParser(), executableRunner, toolVersionLogger);
    }

    private CpanListParser cpanListParser() {
        return new CpanListParser(externalIdFactory);
    }

    private CpanCliExtractor cpanCliExtractor() {
        return new CpanCliExtractor(cpanListParser(), executableRunner, toolVersionLogger);
    }

    private PackratLockFileParser packratLockFileParser() {
        return new PackratLockFileParser(externalIdFactory);
    }

    private PackratDescriptionFileParser packratDescriptionFileParser() {
        return new PackratDescriptionFileParser();
    }

    private PackratLockExtractor packratLockExtractor() {
        return new PackratLockExtractor(packratDescriptionFileParser(), packratLockFileParser(), fileFinder);
    }

    private GitFileParser gitFileParser() {
        return new GitFileParser();
    }

    private GitConfigNameVersionTransformer gitConfigNameVersionTransformer() {
        return new GitConfigNameVersionTransformer(gitUrlParser());
    }

    private GitConfigNodeTransformer gitConfigNodeTransformer() {
        return new GitConfigNodeTransformer();
    }

    private GitParseExtractor gitParseExtractor() {
        return new GitParseExtractor(gitFileParser(), gitConfigNameVersionTransformer(), gitConfigNodeTransformer());
    }

    private GitUrlParser gitUrlParser() {
        return new GitUrlParser();
    }

    private GitCliExtractor gitCliExtractor() {
        return new GitCliExtractor(executableRunner, gitUrlParser(), toolVersionLogger);
    }

    private GoLockParser goLockParser() {
        return new GoLockParser(externalIdFactory);
    }

    private GoDepExtractor goDepExtractor() {
        return new GoDepExtractor(goLockParser());
    }

    private GoModWhyParser goModWhyParser() {
        return new GoModWhyParser();
    }

    private GoModCommandExecutor goModCommandExecutor() {
        return new GoModCommandExecutor(executableRunner);
    }

    private GoModGraphGenerator goModGraphGraphGenerator() {
        return new GoModGraphGenerator(externalIdFactory);
    }

    private GoListParser goListParser() {
        return new GoListParser(gson);
    }

    private GoGraphParser goGraphParser() {
        return new GoGraphParser();
    }

    private GoModCliExtractor goModCliExtractor() {
        return new GoModCliExtractor(goModCommandExecutor(), goListParser(), goGraphParser(), goModWhyParser(), goModGraphGraphGenerator(), externalIdFactory);
    }

    private GoVndrExtractor goVndrExtractor() {
        return new GoVndrExtractor(externalIdFactory);
    }

    private GoVendorExtractor goVendorExtractor() {
        return new GoVendorExtractor(gson, externalIdFactory);
    }

    private GradleReportParser gradleReportParser() {
        return new GradleReportParser();
    }

    private GradleReportTransformer gradleReportTransformer(GradleInspectorOptions gradleInspectorOptions) {
        return new GradleReportTransformer(externalIdFactory, gradleInspectorOptions.shouldIncludeUnresolvedConfigurations());
    }

    private GradleRootMetadataParser gradleRootMetadataParser() {
        return new GradleRootMetadataParser();
    }

    private Rebar3TreeParser rebar3TreeParser() {
        return new Rebar3TreeParser(externalIdFactory);
    }

    private RebarExtractor rebarExtractor() {
        return new RebarExtractor(executableRunner, rebar3TreeParser(), toolVersionLogger);
    }

    private MavenCodeLocationPackager mavenCodeLocationPackager() {
        return new MavenCodeLocationPackager(externalIdFactory);
    }

    private MavenCliExtractor mavenCliExtractor() {
        return new MavenCliExtractor(executableRunner, mavenCodeLocationPackager(), commandParser(), toolVersionLogger);
    }

    private CommandParser commandParser() {
        return new CommandParser();
    }

    private CompileCommandParser compileCommandParser() {
        return new CompileCommandParser(commandParser());
    }

    private ConanLockfileExtractor conanLockfileExtractor() {
        ConanCodeLocationGenerator conanCodeLocationGenerator = new ConanCodeLocationGenerator();
        ConanLockfileParser conanLockfileParser = new ConanLockfileParser(gson, conanCodeLocationGenerator, externalIdFactory);
        return new ConanLockfileExtractor(conanLockfileParser);
    }

    private ConanCliExtractor conanCliExtractor() {
        ConanInfoLineAnalyzer conanInfoLineAnalyzer = new ConanInfoLineAnalyzer();
        ConanCodeLocationGenerator conanCodeLocationGenerator = new ConanCodeLocationGenerator();
        NodeElementParser nodeElementParser = new NodeElementParser(conanInfoLineAnalyzer);
        ConanInfoNodeParser conanInfoNodeParser = new ConanInfoNodeParser(conanInfoLineAnalyzer, nodeElementParser);
        ConanInfoParser conanInfoParser = new ConanInfoParser(conanInfoNodeParser, conanCodeLocationGenerator, externalIdFactory);
        return new ConanCliExtractor(executableRunner, conanInfoParser, toolVersionLogger);
    }

    private NpmCliParser npmCliDependencyFinder() {
        return new NpmCliParser(externalIdFactory);
    }

    private NpmLockfilePackager npmLockfilePackager() {
        return new NpmLockfilePackager(gson, externalIdFactory);
    }

    private NpmCliExtractor npmCliExtractor() {
        return new NpmCliExtractor(executableRunner, npmCliDependencyFinder(), gson, toolVersionLogger);
    }

    private NpmLockfileExtractor npmLockfileExtractor() {
        return new NpmLockfileExtractor(npmLockfilePackager());
    }

    private NugetInspectorParser nugetInspectorParser() {
        return new NugetInspectorParser(gson, externalIdFactory);
    }

    private NugetInspectorExtractor nugetInspectorExtractor() {
        return new NugetInspectorExtractor(nugetInspectorParser(), fileFinder);
    }

    private ProjectInspectorParser projectInspectorParser() {
        return new ProjectInspectorParser(gson, externalIdFactory);
    }

    private ProjectInspectorExtractor projectInspectorExtractor() {
        return new ProjectInspectorExtractor(executableRunner, projectInspectorParser());
    }

    private PackagistParser packagistParser() {
        return new PackagistParser(externalIdFactory);
    }

    private ComposerLockExtractor composerLockExtractor() {
        return new ComposerLockExtractor(packagistParser());
    }

    private PearListParser pearListParser() {
        return new PearListParser();
    }

    private PearPackageXmlParser pearPackageXmlParser() {
        return new PearPackageXmlParser();
    }

    private PearPackageDependenciesParser pearPackageDependenciesParser() {
        return new PearPackageDependenciesParser();
    }

    private PearDependencyGraphTransformer pearDependencyGraphTransformer() {
        return new PearDependencyGraphTransformer(externalIdFactory);
    }

    private PearCliExtractor pearCliExtractor() {
        return new PearCliExtractor(externalIdFactory, executableRunner, pearDependencyGraphTransformer(), pearPackageXmlParser(), pearPackageDependenciesParser(), pearListParser());
    }

    private PipEnvJsonGraphParser pipenvJsonGraphParser() {
        return new PipEnvJsonGraphParser(gson);
    }

    private PipenvFreezeParser pipenvFreezeParser() {
        return new PipenvFreezeParser();
    }

    private PipenvTransformer pipenvTransformer() {
        return new PipenvTransformer(externalIdFactory);
    }

    private PipenvExtractor pipenvExtractor() {
        return new PipenvExtractor(executableRunner, pipenvTransformer(), pipenvFreezeParser(), pipenvJsonGraphParser());
    }

    private PipInspectorTreeParser pipInspectorTreeParser() {
        return new PipInspectorTreeParser(externalIdFactory);
    }

    private PipInspectorExtractor pipInspectorExtractor() {
        return new PipInspectorExtractor(executableRunner, pipInspectorTreeParser(), toolVersionLogger);
    }

    private PnpmLockExtractor pnpmLockExtractor() {
        return new PnpmLockExtractor(pnpmLockYamlParser(), packageJsonFiles());
    }

    private PnpmLockYamlParser pnpmLockYamlParser() {
        return new PnpmLockYamlParser(pnpmTransformer());
    }

    private PnpmYamlTransformer pnpmTransformer() {
        return new PnpmYamlTransformer(externalIdFactory);
    }

    private PoetryExtractor poetryExtractor() {
        return new PoetryExtractor(new PoetryLockParser());
    }

    private PubSpecExtractor pubSpecExtractor() {
        return new PubSpecExtractor(pubSpecLockParser(), pubSpecYamlNameVersionParser());
    }

    private PubSpecLockParser pubSpecLockParser() {
        return new PubSpecLockParser(externalIdFactory);
    }

    private PubDepsExtractor pubDepsExtractor() {
        return new PubDepsExtractor(executableRunner, pubDepsParser(), pubSpecYamlNameVersionParser(), toolVersionLogger);
    }

    private PubDepsParser pubDepsParser() {
        return new PubDepsParser(externalIdFactory);
    }

    private PubSpecYamlNameVersionParser pubSpecYamlNameVersionParser() {
        return new PubSpecYamlNameVersionParser();
    }

    private ToolPoetrySectionParser toolPoetrySectionParser() {
        return new ToolPoetrySectionParser();
    }

    private GemlockExtractor gemlockExtractor() {
        return new GemlockExtractor(externalIdFactory);
    }

    private SbtResolutionCacheExtractor sbtResolutionCacheExtractor() {
        return new SbtResolutionCacheExtractor(fileFinder, externalIdFactory);
    }

    public SbtPluginFinder sbtPluginFinder() {
        return new SbtPluginFinder(executableRunner, new SbtCommandArgumentGenerator());
    }

    private SbtDotExtractor sbtDotExtractor() {
        return new SbtDotExtractor(executableRunner, sbtDotOutputParser(), sbtProjectMatcher(), sbtGraphParserTransformer(), sbtDotGraphNodeParser(), new SbtCommandArgumentGenerator());
    }

    private SbtDotOutputParser sbtDotOutputParser() {
        return new SbtDotOutputParser();
    }

    private SbtRootNodeFinder sbtProjectMatcher() {
        return new SbtRootNodeFinder(sbtDotGraphNodeParser());
    }

    private SbtDotGraphNodeParser sbtDotGraphNodeParser() {
        return new SbtDotGraphNodeParser(externalIdFactory);
    }

    private SbtGraphParserTransformer sbtGraphParserTransformer() {
        return new SbtGraphParserTransformer(sbtDotGraphNodeParser());
    }

    private YarnLockParser yarnLockParser() {
        YarnLockLineAnalyzer yarnLockLineAnalyzer = new YarnLockLineAnalyzer();
        YarnLockDependencySpecParser yarnLockDependencySpecParser = new YarnLockDependencySpecParser(yarnLockLineAnalyzer);
        YarnLockEntrySectionParserSet yarnLockEntryElementParser = new YarnLockEntrySectionParserSet(yarnLockLineAnalyzer, yarnLockDependencySpecParser);
        YarnLockEntryParser yarnLockEntryParser = new YarnLockEntryParser(yarnLockLineAnalyzer, yarnLockEntryElementParser);
        return new YarnLockParser(yarnLockEntryParser);
    }

    private YarnTransformer yarnTransformer() {
        return new YarnTransformer(externalIdFactory);
    }

    private YarnPackager yarnPackager() {
        return new YarnPackager(yarnTransformer());
    }

    private PackageJsonFiles packageJsonFiles() {
        return new PackageJsonFiles(packageJsonReader());
    }

    private PackageJsonReader packageJsonReader() {
        return new PackageJsonReader(gson);
    }

    private YarnLockExtractor yarnLockExtractor(YarnLockOptions yarnLockOptions) {
        return new YarnLockExtractor(yarnLockParser(), yarnPackager(), packageJsonFiles(), yarnLockOptions);
    }

    private BitbakeRecipesParser bitbakeRecipesParser() {
        return new BitbakeRecipesParser();
    }

    private BitbakeRecipesToLayerMapConverter bitbakeRecipesToLayerMap() {
        return new BitbakeRecipesToLayerMapConverter();
    }

    private BitbakeExtractor bitbakeExtractor() {
        return new BitbakeExtractor(executableRunner, fileFinder, graphParserTransformer(), bitbakeGraphTransformer(), bitbakeRecipesParser(), bitbakeRecipesToLayerMap(), toolVersionLogger);
    }

    private GraphParserTransformer graphParserTransformer() {
        return new GraphParserTransformer();
    }

    private BitbakeGraphTransformer bitbakeGraphTransformer() {
        return new BitbakeGraphTransformer(externalIdFactory);
    }

    private ClangPackageManagerInfoFactory clangPackageManagerInfoFactory() {
        return new ClangPackageManagerInfoFactory();
    }

    private ClangPackageManagerFactory clangPackageManagerFactory() {
        return new ClangPackageManagerFactory(clangPackageManagerInfoFactory());
    }

    private ClangPackageManagerRunner clangPackageManagerRunner() {
        return new ClangPackageManagerRunner();
    }

    private GradleRunner gradleRunner() {
        return new GradleRunner(executableRunner);
    }

    private GradleInspectorExtractor gradleInspectorExtractor(GradleInspectorOptions gradleInspectorOptions) {
        return new GradleInspectorExtractor(fileFinder, gradleRunner(), gradleReportParser(), gradleReportTransformer(gradleInspectorOptions), gradleRootMetadataParser(),
                toolVersionLogger);
    }

    private DockerExtractor dockerExtractor() {
        return new DockerExtractor(fileFinder, executableRunner, new BdioTransformer(), new ExternalIdFactory(), gson);
    }

    private GemspecLineParser gemspecLineParser() {
        return new GemspecLineParser();
    }

    private GemspecParser gemspecParser() {
        return new GemspecParser(externalIdFactory, gemspecLineParser());
    }

    private GemspecParseExtractor gemspecExtractor() {
        return new GemspecParseExtractor(gemspecParser());
    }

    private GoGradleLockParser goGradleLockParser() {
        return new GoGradleLockParser(externalIdFactory);
    }

    private GoGradleExtractor goGradleExtractor() {
        return new GoGradleExtractor(goGradleLockParser());
    }

    private PackageJsonExtractor packageJsonExtractor() {
        return new PackageJsonExtractor(gson, externalIdFactory);
    }

    private SAXParser saxParser() {
        try {
            return SAXParserFactory.newInstance().newSAXParser();
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException("Unable to create SAX Parser.", e);
        }
    }

    private MavenParseExtractor mavenParseExtractor() {
        return new MavenParseExtractor(externalIdFactory, saxParser());
    }

    private SwiftCliParser swiftCliParser() {
        return new SwiftCliParser(gson);
    }

    private SwiftPackageTransformer swiftPackageTransformer() {
        return new SwiftPackageTransformer(externalIdFactory);
    }

    private SwiftExtractor swiftExtractor() {
        return new SwiftExtractor(executableRunner, swiftCliParser(), swiftPackageTransformer(), toolVersionLogger);
    }

    private LernaPackageDiscoverer lernaPackageDiscoverer() {
        return new LernaPackageDiscoverer(executableRunner, gson);
    }

    private LernaPackager lernaPackager(NpmLockfileOptions npmLockfileOptions, YarnLockOptions yarnLockOptions, LernaOptions lernaOptions) {
        return new LernaPackager(fileFinder, packageJsonReader(), yarnLockParser(), yarnLockOptions, npmLockfilePackager(), npmLockfileOptions, yarnPackager(), lernaOptions);
    }

    private LernaExtractor lernaExtractor(NpmLockfileOptions npmLockfileOptions, YarnLockOptions yarnLockOptions, LernaOptions lernaOptions) {
        return new LernaExtractor(lernaPackageDiscoverer(), lernaPackager(npmLockfileOptions, yarnLockOptions, lernaOptions), lernaOptions);
    }

    //#endregion Utility

}
