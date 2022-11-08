package com.synopsys.integration.detectable.factory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.BdioTransformer;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.common.util.parse.CommandParser;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.resolver.BashResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.BazelResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CondaResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CpanResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CpanmResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.DartResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.DockerResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.FlutterResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.GitResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.GoResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.GradleResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.JavaResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.LernaResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.MavenResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.NpmResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PearResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PipResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PipenvResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PythonResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.Rebar3Resolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.SbtResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.SwiftResolver;
import com.synopsys.integration.detectable.detectable.inspector.GradleInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.PipInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.ProjectInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;
import com.synopsys.integration.detectable.detectables.bazel.BazelDetectable;
import com.synopsys.integration.detectable.detectables.bazel.BazelDetectableOptions;
import com.synopsys.integration.detectable.detectables.bazel.BazelExtractor;
import com.synopsys.integration.detectable.detectables.bazel.BazelProjectNameGenerator;
import com.synopsys.integration.detectable.detectables.bazel.BazelWorkspaceFileParser;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.WorkspaceRuleChooser;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.BazelVariableSubstitutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.HaskellCabalLibraryJsonProtoParser;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectable;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectableOptions;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeExtractor;
import com.synopsys.integration.detectable.detectables.bitbake.collect.BitbakeCommandRunner;
import com.synopsys.integration.detectable.detectables.bitbake.collect.BuildFileFinder;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeEnvironmentParser;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeRecipesParser;
import com.synopsys.integration.detectable.detectables.bitbake.parse.GraphNodeLabelParser;
import com.synopsys.integration.detectable.detectables.bitbake.parse.LicenseManifestParser;
import com.synopsys.integration.detectable.detectables.bitbake.parse.PwdOutputParser;
import com.synopsys.integration.detectable.detectables.bitbake.transform.BitbakeDependencyGraphTransformer;
import com.synopsys.integration.detectable.detectables.bitbake.transform.BitbakeGraphTransformer;
import com.synopsys.integration.detectable.detectables.cargo.CargoExtractor;
import com.synopsys.integration.detectable.detectables.cargo.CargoLockDetectable;
import com.synopsys.integration.detectable.detectables.cargo.parse.CargoDependencyLineParser;
import com.synopsys.integration.detectable.detectables.cargo.parse.CargoTomlParser;
import com.synopsys.integration.detectable.detectables.cargo.transform.CargoLockPackageDataTransformer;
import com.synopsys.integration.detectable.detectables.cargo.transform.CargoLockPackageTransformer;
import com.synopsys.integration.detectable.detectables.carthage.CarthageExtractor;
import com.synopsys.integration.detectable.detectables.carthage.CarthageLockDetectable;
import com.synopsys.integration.detectable.detectables.carthage.parse.CartfileResolvedParser;
import com.synopsys.integration.detectable.detectables.carthage.transform.CarthageDeclarationTransformer;
import com.synopsys.integration.detectable.detectables.clang.ClangDetectable;
import com.synopsys.integration.detectable.detectables.clang.ClangDetectableOptions;
import com.synopsys.integration.detectable.detectables.clang.ClangExtractor;
import com.synopsys.integration.detectable.detectables.clang.ForgeChooser;
import com.synopsys.integration.detectable.detectables.clang.LinuxDistroToForgeMapper;
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
import com.synopsys.integration.detectable.detectables.conan.cli.ConanResolver;
import com.synopsys.integration.detectable.detectables.conan.cli.config.ConanCliOptions;
import com.synopsys.integration.detectable.detectables.conan.cli.parser.ConanInfoLineAnalyzer;
import com.synopsys.integration.detectable.detectables.conan.cli.parser.ConanInfoNodeParser;
import com.synopsys.integration.detectable.detectables.conan.cli.parser.ConanInfoParser;
import com.synopsys.integration.detectable.detectables.conan.cli.parser.element.NodeElementParser;
import com.synopsys.integration.detectable.detectables.conan.cli.process.ConanCommandRunner;
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
import com.synopsys.integration.detectable.detectables.docker.ImageIdentifierGenerator;
import com.synopsys.integration.detectable.detectables.docker.parser.DockerInspectorResultsFileParser;
import com.synopsys.integration.detectable.detectables.git.GitCliDetectable;
import com.synopsys.integration.detectable.detectables.git.GitParseDetectable;
import com.synopsys.integration.detectable.detectables.git.cli.GitCliExtractor;
import com.synopsys.integration.detectable.detectables.git.cli.GitCommandRunner;
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
import com.synopsys.integration.detectable.detectables.go.gomod.GoModCommandRunner;
import com.synopsys.integration.detectable.detectables.go.gomod.parse.GoGraphParser;
import com.synopsys.integration.detectable.detectables.go.gomod.parse.GoListParser;
import com.synopsys.integration.detectable.detectables.go.gomod.parse.GoModWhyParser;
import com.synopsys.integration.detectable.detectables.go.gomod.parse.GoVersionParser;
import com.synopsys.integration.detectable.detectables.go.gomod.process.GoModGraphGenerator;
import com.synopsys.integration.detectable.detectables.go.vendor.GoVendorDetectable;
import com.synopsys.integration.detectable.detectables.go.vendor.GoVendorExtractor;
import com.synopsys.integration.detectable.detectables.go.vendr.GoVndrDetectable;
import com.synopsys.integration.detectable.detectables.go.vendr.GoVndrExtractor;
import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleInspectorDetectable;
import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleInspectorExtractor;
import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleInspectorOptions;
import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleRunner;
import com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleReportParser;
import com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleReportTransformer;
import com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleRootMetadataParser;
import com.synopsys.integration.detectable.detectables.gradle.parsing.GradleProjectInspectorDetectable;
import com.synopsys.integration.detectable.detectables.ivy.IvyParseDetectable;
import com.synopsys.integration.detectable.detectables.ivy.IvyParseExtractor;
import com.synopsys.integration.detectable.detectables.ivy.parse.IvyProjectNameParser;
import com.synopsys.integration.detectable.detectables.lerna.LernaDetectable;
import com.synopsys.integration.detectable.detectables.lerna.LernaExtractor;
import com.synopsys.integration.detectable.detectables.lerna.LernaOptions;
import com.synopsys.integration.detectable.detectables.lerna.LernaPackageDiscoverer;
import com.synopsys.integration.detectable.detectables.lerna.LernaPackager;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenCliExtractor;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenCliExtractorOptions;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenCodeLocationPackager;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenPomDetectable;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenPomWrapperDetectable;
import com.synopsys.integration.detectable.detectables.maven.parsing.MavenProjectInspectorDetectable;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliDetectable;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliExtractor;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliExtractorOptions;
import com.synopsys.integration.detectable.detectables.npm.cli.parse.NpmCliParser;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmLockfileExtractor;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmLockfileOptions;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmPackageLockDetectable;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmShrinkwrapDetectable;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockFileProjectIdTransformer;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockfileGraphTransformer;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockfilePackager;
import com.synopsys.integration.detectable.detectables.npm.packagejson.NpmPackageJsonParseDetectable;
import com.synopsys.integration.detectable.detectables.npm.packagejson.NpmPackageJsonParseDetectableOptions;
import com.synopsys.integration.detectable.detectables.npm.packagejson.PackageJsonExtractor;
import com.synopsys.integration.detectable.detectables.nuget.NugetInspectorExtractor;
import com.synopsys.integration.detectable.detectables.nuget.NugetInspectorOptions;
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
import com.synopsys.integration.detectable.detectables.pipenv.build.PipenvDetectable;
import com.synopsys.integration.detectable.detectables.pipenv.build.PipenvDetectableOptions;
import com.synopsys.integration.detectable.detectables.pipenv.build.PipenvExtractor;
import com.synopsys.integration.detectable.detectables.pipenv.build.parser.PipEnvJsonGraphParser;
import com.synopsys.integration.detectable.detectables.pipenv.build.parser.PipenvFreezeParser;
import com.synopsys.integration.detectable.detectables.pipenv.build.parser.PipenvTransformer;
import com.synopsys.integration.detectable.detectables.pipenv.parse.PipfileLockDependencyTransformer;
import com.synopsys.integration.detectable.detectables.pipenv.parse.PipfileLockDependencyVersionParser;
import com.synopsys.integration.detectable.detectables.pipenv.parse.PipfileLockDetectable;
import com.synopsys.integration.detectable.detectables.pipenv.parse.PipfileLockDetectableOptions;
import com.synopsys.integration.detectable.detectables.pipenv.parse.PipfileLockExtractor;
import com.synopsys.integration.detectable.detectables.pipenv.parse.PipfileLockTransformer;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.PnpmLockDetectable;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.PnpmLockExtractor;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.PnpmLockOptions;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.process.PnpmLockYamlParser;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.process.PnpmYamlTransformer;
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
import com.synopsys.integration.detectable.detectables.sbt.SbtDetectableOptions;
import com.synopsys.integration.detectable.detectables.sbt.dot.SbtCommandArgumentGenerator;
import com.synopsys.integration.detectable.detectables.sbt.dot.SbtDotExtractor;
import com.synopsys.integration.detectable.detectables.sbt.dot.SbtDotGraphNodeParser;
import com.synopsys.integration.detectable.detectables.sbt.dot.SbtDotOutputParser;
import com.synopsys.integration.detectable.detectables.sbt.dot.SbtGraphParserTransformer;
import com.synopsys.integration.detectable.detectables.sbt.dot.SbtPluginFinder;
import com.synopsys.integration.detectable.detectables.sbt.dot.SbtRootNodeFinder;
import com.synopsys.integration.detectable.detectables.swift.cli.SwiftCliDetectable;
import com.synopsys.integration.detectable.detectables.swift.cli.SwiftCliParser;
import com.synopsys.integration.detectable.detectables.swift.cli.SwiftExtractor;
import com.synopsys.integration.detectable.detectables.swift.cli.SwiftPackageTransformer;
import com.synopsys.integration.detectable.detectables.swift.lock.PackageResolvedExtractor;
import com.synopsys.integration.detectable.detectables.swift.lock.SwiftPackageResolvedDetectable;
import com.synopsys.integration.detectable.detectables.swift.lock.parse.PackageResolvedDataChecker;
import com.synopsys.integration.detectable.detectables.swift.lock.parse.PackageResolvedFormatChecker;
import com.synopsys.integration.detectable.detectables.swift.lock.parse.PackageResolvedFormatParser;
import com.synopsys.integration.detectable.detectables.swift.lock.parse.PackageResolvedParser;
import com.synopsys.integration.detectable.detectables.swift.lock.transform.PackageResolvedTransformer;
import com.synopsys.integration.detectable.detectables.xcode.XcodeProjectDetectable;
import com.synopsys.integration.detectable.detectables.xcode.XcodeWorkspaceDetectable;
import com.synopsys.integration.detectable.detectables.xcode.XcodeWorkspaceExtractor;
import com.synopsys.integration.detectable.detectables.xcode.parse.XcodeWorkspaceFormatChecker;
import com.synopsys.integration.detectable.detectables.xcode.parse.XcodeWorkspaceParser;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockDetectable;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockExtractor;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockOptions;
import com.synopsys.integration.detectable.detectables.yarn.YarnPackager;
import com.synopsys.integration.detectable.detectables.yarn.YarnTransformer;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonFiles;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonReader;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.section.YarnLockDependencySpecParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.section.YarnLockEntrySectionParserSet;
import com.synopsys.integration.detectable.util.ToolVersionLogger;

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

    public DockerDetectable createDockerDetectable(
        DetectableEnvironment environment,
        DockerDetectableOptions dockerDetectableOptions,
        DockerInspectorResolver dockerInspectorResolver,
        JavaResolver javaResolver,
        DockerResolver dockerResolver
    ) {
        return new DockerDetectable(environment, dockerInspectorResolver, javaResolver, dockerResolver, dockerExtractor(), dockerDetectableOptions);
    }

    public BazelDetectable createBazelDetectable(DetectableEnvironment environment, BazelDetectableOptions bazelDetectableOptions, BazelResolver bazelResolver) {
        return new BazelDetectable(environment, fileFinder, bazelExtractor(bazelDetectableOptions), bazelResolver, bazelDetectableOptions.getTargetName().orElse(null));
    }

    public BitbakeDetectable createBitbakeDetectable(DetectableEnvironment environment, BitbakeDetectableOptions bitbakeDetectableOptions, BashResolver bashResolver) {
        BitbakeExtractor bitbakeExtractor = new BitbakeExtractor(
            toolVersionLogger,
            new BitbakeCommandRunner(executableRunner, bitbakeDetectableOptions.getSourceArguments()),
            new BuildFileFinder(fileFinder, bitbakeDetectableOptions.isFollowSymLinks(), bitbakeDetectableOptions.getSearchDepth()),
            new PwdOutputParser(),
            new BitbakeEnvironmentParser(),
            new BitbakeRecipesParser(),
            new LicenseManifestParser(),
            new BitbakeGraphTransformer(new GraphNodeLabelParser()),
            new BitbakeDependencyGraphTransformer(bitbakeDetectableOptions.getDependencyTypeFilter()),
            bitbakeDetectableOptions.getPackageNames(),
            bitbakeDetectableOptions.getDependencyTypeFilter()
        );
        return new BitbakeDetectable(environment, fileFinder, bitbakeDetectableOptions, bitbakeExtractor, bashResolver);
    }

    public CargoLockDetectable createCargoDetectable(DetectableEnvironment environment) {
        CargoTomlParser cargoTomlParser = new CargoTomlParser();
        CargoDependencyLineParser cargoDependencyLineParser = new CargoDependencyLineParser();
        CargoLockPackageDataTransformer cargoLockPackageDataTransformer = new CargoLockPackageDataTransformer(cargoDependencyLineParser);
        CargoLockPackageTransformer cargoLockPackageTransformer = new CargoLockPackageTransformer();
        CargoExtractor cargoExtractor = new CargoExtractor(cargoTomlParser, cargoLockPackageDataTransformer, cargoLockPackageTransformer);
        return new CargoLockDetectable(environment, fileFinder, cargoExtractor);
    }

    public CarthageLockDetectable createCarthageDetectable(DetectableEnvironment environment) {
        CartfileResolvedParser cartfileResolvedParser = new CartfileResolvedParser();
        CarthageDeclarationTransformer carthageDeclarationTransformer = new CarthageDeclarationTransformer();
        CarthageExtractor carthageExtractor = new CarthageExtractor(cartfileResolvedParser, carthageDeclarationTransformer);
        return new CarthageLockDetectable(environment, fileFinder, carthageExtractor);
    }

    public ClangDetectable createClangDetectable(DetectableEnvironment environment, ClangDetectableOptions clangDetectableOptions) {
        return new ClangDetectable(
            environment,
            executableRunner,
            fileFinder,
            clangPackageManagerFactory().createPackageManagers(),
            clangExtractor(),
            clangDetectableOptions,
            clangPackageManagerRunner()
        );
    }

    public ComposerLockDetectable createComposerDetectable(DetectableEnvironment environment, ComposerLockDetectableOptions composerLockDetectableOptions) {
        PackagistParser packagistParser = new PackagistParser(externalIdFactory, composerLockDetectableOptions.getDependencyTypeFilter());
        ComposerLockExtractor composerLockExtractor = new ComposerLockExtractor(packagistParser);
        return new ComposerLockDetectable(environment, fileFinder, composerLockExtractor);
    }

    public CondaCliDetectable createCondaCliDetectable(DetectableEnvironment environment, CondaResolver condaResolver, CondaCliDetectableOptions condaCliDetectableOptions) {
        return new CondaCliDetectable(environment, fileFinder, condaResolver, condaCliExtractor(), condaCliDetectableOptions);
    }

    public CpanCliDetectable createCpanCliDetectable(DetectableEnvironment environment, CpanResolver cpanResolver, CpanmResolver cpanmResolver) {
        return new CpanCliDetectable(environment, fileFinder, cpanResolver, cpanmResolver, cpanCliExtractor());
    }

    public DartPubSpecLockDetectable createDartPubSpecLockDetectable(DetectableEnvironment environment) {
        PubSpecLockParser pubSpecLockParser = new PubSpecLockParser();
        PubSpecYamlNameVersionParser pubSpecYamlNameVersionParser = new PubSpecYamlNameVersionParser();
        PubSpecExtractor pubSpecExtractor = new PubSpecExtractor(pubSpecLockParser, pubSpecYamlNameVersionParser);
        return new DartPubSpecLockDetectable(environment, fileFinder, pubSpecExtractor);
    }

    public DartPubDepDetectable createDartPubDepDetectable(
        DetectableEnvironment environment,
        DartPubDepsDetectableOptions dartPubDepsDetectableOptions,
        DartResolver dartResolver,
        FlutterResolver flutterResolver
    ) {
        PubDepsParser pubDepsParser = new PubDepsParser();
        PubSpecYamlNameVersionParser pubSpecYamlNameVersionParser = new PubSpecYamlNameVersionParser();
        PubDepsExtractor pubDepsExtractor = new PubDepsExtractor(executableRunner, pubDepsParser, pubSpecYamlNameVersionParser, toolVersionLogger);
        return new DartPubDepDetectable(environment, fileFinder, pubDepsExtractor, dartPubDepsDetectableOptions, dartResolver, flutterResolver);
    }

    public GemlockDetectable createGemlockDetectable(DetectableEnvironment environment) {
        return new GemlockDetectable(environment, fileFinder, gemlockExtractor());
    }

    public GitCliDetectable createGitDetectable(DetectableEnvironment environment, GitResolver gitResolver) {
        return new GitCliDetectable(environment, fileFinder, gitCliExtractor(), gitResolver);
    }

    public GitParseDetectable createGitParseDetectable(DetectableEnvironment environment) {
        return new GitParseDetectable(environment, fileFinder, gitParseExtractor());
    }

    public GoModCliDetectable createGoModCliDetectable(DetectableEnvironment environment, GoResolver goResolver, GoModCliDetectableOptions options) {
        return new GoModCliDetectable(environment, fileFinder, goResolver, goModCliExtractor(options));
    }

    public GoDepLockDetectable createGoLockDetectable(DetectableEnvironment environment) {
        GoLockParser goLockParser = new GoLockParser();
        GoDepExtractor goDepExtractor = new GoDepExtractor(goLockParser);
        return new GoDepLockDetectable(environment, fileFinder, goDepExtractor);
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

    public GradleInspectorDetectable createGradleDetectable(
        DetectableEnvironment environment,
        GradleInspectorOptions gradleInspectorOptions,
        GradleInspectorResolver gradleInspectorResolver,
        GradleResolver gradleResolver
    ) {
        return new GradleInspectorDetectable(
            environment,
            fileFinder,
            gradleResolver,
            gradleInspectorResolver,
            gradleInspectorExtractor(gradleInspectorOptions),
            gradleInspectorOptions
        );
    }

    public GradleProjectInspectorDetectable createMavenGradleInspectorDetectable(
        DetectableEnvironment detectableEnvironment,
        ProjectInspectorResolver projectInspectorResolver,
        ProjectInspectorOptions projectInspectorOptions
    ) {
        return new GradleProjectInspectorDetectable(detectableEnvironment, fileFinder, projectInspectorResolver, projectInspectorExtractor(), projectInspectorOptions);
    }

    public GemspecParseDetectable createGemspecParseDetectable(DetectableEnvironment environment, GemspecParseDetectableOptions gemspecOptions) {
        GemspecLineParser gemspecLineParser = new GemspecLineParser();
        GemspecParser gemspecParser = new GemspecParser(gemspecLineParser, gemspecOptions.getDependencyTypeFilter());
        GemspecParseExtractor gemspecParseExtractor = new GemspecParseExtractor(gemspecParser);
        return new GemspecParseDetectable(environment, fileFinder, gemspecParseExtractor);
    }

    public IvyParseDetectable createIvyParseDetectable(DetectableEnvironment environment) {
        return new IvyParseDetectable(environment, fileFinder, ivyParseExtractor());
    }

    public MavenPomDetectable createMavenPomDetectable(DetectableEnvironment environment, MavenResolver mavenResolver, MavenCliExtractorOptions mavenCliExtractorOptions) {
        return new MavenPomDetectable(environment, fileFinder, mavenResolver, mavenCliExtractor(), mavenCliExtractorOptions);
    }

    public MavenPomWrapperDetectable createMavenPomWrapperDetectable(
        DetectableEnvironment environment,
        MavenResolver mavenResolver,
        MavenCliExtractorOptions mavenCliExtractorOptions
    ) {
        return new MavenPomWrapperDetectable(environment, fileFinder, mavenResolver, mavenCliExtractor(), mavenCliExtractorOptions);
    }

    public MavenProjectInspectorDetectable createMavenProjectInspectorDetectable(
        DetectableEnvironment detectableEnvironment, ProjectInspectorResolver projectInspectorResolver,
        ProjectInspectorOptions projectInspectorOptions
    ) {
        return new MavenProjectInspectorDetectable(
            detectableEnvironment,
            fileFinder,
            projectInspectorResolver,
            projectInspectorExtractor(),
            projectInspectorOptions
        );
    }

    public ConanLockfileDetectable createConanLockfileDetectable(DetectableEnvironment environment, ConanLockfileExtractorOptions conanLockfileExtractorOptions) {
        return new ConanLockfileDetectable(environment, fileFinder, conanLockfileExtractor(conanLockfileExtractorOptions), conanLockfileExtractorOptions);
    }

    public ConanCliDetectable createConanCliDetectable(DetectableEnvironment environment, ConanResolver conanResolver, ConanCliOptions conanCliOptions) {
        return new ConanCliDetectable(environment, fileFinder, conanResolver, conanCliExtractor(conanCliOptions));
    }

    public NpmCliDetectable createNpmCliDetectable(DetectableEnvironment environment, NpmResolver npmResolver, NpmCliExtractorOptions npmCliExtractorOptions) {
        NpmCliParser npmCliParser = new NpmCliParser(externalIdFactory, npmCliExtractorOptions.getDependencyTypeFilter());
        NpmCliExtractor npmCliExtractor = new NpmCliExtractor(executableRunner, npmCliParser, gson, toolVersionLogger);
        return new NpmCliDetectable(environment, fileFinder, npmResolver, npmCliExtractor, npmCliExtractorOptions);
    }

    public NpmPackageLockDetectable createNpmPackageLockDetectable(DetectableEnvironment environment, NpmLockfileOptions npmLockfileOptions) {
        return new NpmPackageLockDetectable(environment, fileFinder, npmLockfileExtractor(npmLockfileOptions));
    }

    public NugetProjectDetectable createNugetProjectDetectable(
        DetectableEnvironment environment,
        NugetInspectorOptions nugetInspectorOptions,
        NugetInspectorResolver nugetInspectorResolver
    ) {
        return new NugetProjectDetectable(environment, fileFinder, nugetInspectorOptions, nugetInspectorResolver, nugetInspectorExtractor());
    }

    public NpmShrinkwrapDetectable createNpmShrinkwrapDetectable(DetectableEnvironment environment, NpmLockfileOptions npmLockfileOptions) {
        return new NpmShrinkwrapDetectable(environment, fileFinder, npmLockfileExtractor(npmLockfileOptions));
    }

    public NpmPackageJsonParseDetectable createNpmPackageJsonParseDetectable(DetectableEnvironment environment, NpmPackageJsonParseDetectableOptions npmPackageJsonOptions) {
        PackageJsonExtractor packageJsonExtractor = new PackageJsonExtractor(gson, externalIdFactory, npmPackageJsonOptions.getNpmDependencyTypeFilter());
        return new NpmPackageJsonParseDetectable(environment, fileFinder, packageJsonExtractor);
    }

    public NugetSolutionDetectable createNugetSolutionDetectable(
        DetectableEnvironment environment,
        NugetInspectorOptions nugetInspectorOptions,
        NugetInspectorResolver nugetInspectorResolver
    ) {
        return new NugetSolutionDetectable(environment, fileFinder, nugetInspectorResolver, nugetInspectorExtractor(), nugetInspectorOptions);
    }

    public NugetProjectInspectorDetectable createNugetParseDetectable(
        DetectableEnvironment environment,
        ProjectInspectorResolver projectInspectorResolver,
        ProjectInspectorOptions projectInspectorOptions
    ) {
        return new NugetProjectInspectorDetectable(environment, fileFinder, projectInspectorResolver, projectInspectorExtractor(), projectInspectorOptions);
    }

    public PackratLockDetectable createPackratLockDetectable(DetectableEnvironment environment) {
        return new PackratLockDetectable(environment, fileFinder, packratLockExtractor());
    }

    public PearCliDetectable createPearCliDetectable(DetectableEnvironment environment, PearCliDetectableOptions pearCliDetectableOptions, PearResolver pearResolver) {
        PearDependencyGraphTransformer pearDependencyGraphTransformer = new PearDependencyGraphTransformer(externalIdFactory, pearCliDetectableOptions.getDependencyTypeFilter());
        PearPackageXmlParser pearPackageXmlParser = new PearPackageXmlParser();
        PearPackageDependenciesParser pearPackageDependenciesParser = new PearPackageDependenciesParser();
        PearListParser pearListParser = new PearListParser();
        PearCliExtractor pearCliExtractor = new PearCliExtractor(
            externalIdFactory,
            executableRunner,
            pearDependencyGraphTransformer,
            pearPackageXmlParser,
            pearPackageDependenciesParser,
            pearListParser
        );
        return new PearCliDetectable(environment, fileFinder, pearResolver, pearCliExtractor);
    }

    public PipenvDetectable createPipenvDetectable(
        DetectableEnvironment environment,
        PipenvDetectableOptions pipenvDetectableOptions,
        PythonResolver pythonResolver,
        PipenvResolver pipenvResolver
    ) {
        return new PipenvDetectable(environment, pipenvDetectableOptions, fileFinder, pythonResolver, pipenvResolver, pipenvExtractor());
    }

    public PipfileLockDetectable createPipfileLockDetectable(
        DetectableEnvironment environment,
        PipfileLockDetectableOptions pipfileLockDetectableOptions
    ) {
        PipfileLockDependencyVersionParser dependencyVersionParser = new PipfileLockDependencyVersionParser();
        PipfileLockTransformer pipfileLockTransformer = new PipfileLockTransformer(dependencyVersionParser, pipfileLockDetectableOptions.getDependencyTypeFilter());
        PipfileLockDependencyTransformer pipfileLockDependencyTransformer = new PipfileLockDependencyTransformer();
        PipfileLockExtractor pipfileLockExtractor = new PipfileLockExtractor(gson, pipfileLockTransformer, pipfileLockDependencyTransformer);
        return new PipfileLockDetectable(environment, fileFinder, pipfileLockExtractor);
    }

    public PipInspectorDetectable createPipInspectorDetectable(
        DetectableEnvironment environment, PipInspectorDetectableOptions pipInspectorDetectableOptions, PipInspectorResolver pipInspectorResolver,
        PythonResolver pythonResolver,
        PipResolver pipResolver
    ) {
        return new PipInspectorDetectable(environment, fileFinder, pythonResolver, pipResolver, pipInspectorResolver, pipInspectorExtractor(), pipInspectorDetectableOptions);
    }

    public PnpmLockDetectable createPnpmLockDetectable(DetectableEnvironment environment, PnpmLockOptions pnpmLockOptions) {
        PnpmYamlTransformer pnpmYamlTransformer = new PnpmYamlTransformer(pnpmLockOptions.getDependencyTypeFilter());
        PnpmLockYamlParser pnpmLockYamlParser = new PnpmLockYamlParser(pnpmYamlTransformer);
        PnpmLockExtractor pnpmLockExtractor = new PnpmLockExtractor(pnpmLockYamlParser, packageJsonFiles());
        return new PnpmLockDetectable(environment, fileFinder, pnpmLockExtractor, packageJsonFiles());
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

    public SbtDetectable createSbtDetectable(DetectableEnvironment environment, SbtResolver sbtResolver, SbtDetectableOptions sbtDetectableOptions) {
        return new SbtDetectable(environment, fileFinder, sbtDetectableOptions.getSbtCommandAdditionalArguments(), sbtResolver, sbtDotExtractor(), sbtPluginFinder());
    }

    public SwiftCliDetectable createSwiftCliDetectable(DetectableEnvironment environment, SwiftResolver swiftResolver) {
        return new SwiftCliDetectable(environment, fileFinder, swiftExtractor(), swiftResolver);
    }

    public SwiftPackageResolvedDetectable createSwiftPackageResolvedDetectable(DetectableEnvironment environment) {
        PackageResolvedExtractor packageResolvedExtractor = createPackageResolvedExtractor();
        return new SwiftPackageResolvedDetectable(environment, fileFinder, packageResolvedExtractor);
    }

    public YarnLockDetectable createYarnLockDetectable(DetectableEnvironment environment, YarnLockOptions yarnLockOptions) {
        return new YarnLockDetectable(environment, fileFinder, yarnLockExtractor(yarnLockOptions));
    }

    public LernaDetectable createLernaDetectable(
        DetectableEnvironment environment,
        LernaResolver lernaResolver,
        NpmLockfileOptions npmLockfileOptions,
        LernaOptions lernaOptions,
        YarnLockOptions yarnLockOptions
    ) {
        LernaPackageDiscoverer lernaPackageDiscoverer = new LernaPackageDiscoverer(executableRunner, gson, lernaOptions.getExcludedPackages(), lernaOptions.getIncludedPackages());
        LernaPackager lernaPackager = new LernaPackager(
            fileFinder,
            packageJsonReader(),
            yarnLockParser(),
            npmLockfilePackager(npmLockfileOptions),
            yarnPackager(yarnLockOptions),
            lernaOptions.getLernaPackageTypeFilter()
        );
        LernaExtractor lernaExtractor = new LernaExtractor(lernaPackageDiscoverer, lernaPackager);
        return new LernaDetectable(environment, fileFinder, lernaResolver, lernaExtractor);
    }

    public XcodeProjectDetectable createXcodeProjectDetectable(DetectableEnvironment environment) {
        PackageResolvedExtractor packageResolvedExtractor = createPackageResolvedExtractor();
        return new XcodeProjectDetectable(environment, fileFinder, packageResolvedExtractor);
    }

    public XcodeWorkspaceDetectable createXcodeWorkspaceDetectable(DetectableEnvironment environment) {
        PackageResolvedExtractor packageResolvedExtractor = createPackageResolvedExtractor();
        XcodeWorkspaceParser xcodeWorkspaceParser = new XcodeWorkspaceParser();
        XcodeWorkspaceFormatChecker xcodeWorkspaceFormatChecker = new XcodeWorkspaceFormatChecker();
        XcodeWorkspaceExtractor xcodeWorkspaceExtractor = new XcodeWorkspaceExtractor(xcodeWorkspaceParser, xcodeWorkspaceFormatChecker, packageResolvedExtractor, fileFinder);

        return new XcodeWorkspaceDetectable(environment, fileFinder, packageResolvedExtractor, xcodeWorkspaceExtractor);
    }

    // Used by three Detectables
    private PackageResolvedExtractor createPackageResolvedExtractor() {
        PackageResolvedParser parser = new PackageResolvedParser(gson);
        PackageResolvedFormatParser formatParser = new PackageResolvedFormatParser(gson);
        PackageResolvedFormatChecker formatChecker = new PackageResolvedFormatChecker();
        PackageResolvedDataChecker packageResolvedDataChecker = new PackageResolvedDataChecker();
        GitUrlParser gitUrlParser = new GitUrlParser();
        PackageResolvedTransformer transformer = new PackageResolvedTransformer(gitUrlParser);
        return new PackageResolvedExtractor(parser, formatParser, formatChecker, packageResolvedDataChecker, transformer);
    }

    //#endregion

    //#region Utility

    private BazelExtractor bazelExtractor(BazelDetectableOptions bazelDetectableOptions) {
        WorkspaceRuleChooser workspaceRuleChooser = new WorkspaceRuleChooser();
        BazelWorkspaceFileParser bazelWorkspaceFileParser = new BazelWorkspaceFileParser();
        HaskellCabalLibraryJsonProtoParser haskellCabalLibraryJsonProtoParser = new HaskellCabalLibraryJsonProtoParser(gson);
        BazelVariableSubstitutor bazelVariableSubstitutor = new BazelVariableSubstitutor(
            bazelDetectableOptions.getTargetName().orElse(null),
            bazelDetectableOptions.getBazelCqueryAdditionalOptions()
        );
        BazelProjectNameGenerator bazelProjectNameGenerator = new BazelProjectNameGenerator();
        return new BazelExtractor(
            executableRunner,
            externalIdFactory,
            bazelWorkspaceFileParser,
            workspaceRuleChooser,
            toolVersionLogger,
            haskellCabalLibraryJsonProtoParser,
            bazelDetectableOptions.getTargetName().orElse(null),
            bazelDetectableOptions.getWorkspaceRulesFromProperty(),
            bazelVariableSubstitutor,
            bazelProjectNameGenerator
        );
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
        GitCommandRunner gitCommandRunner = new GitCommandRunner(executableRunner);
        return new GitCliExtractor(gitUrlParser(), toolVersionLogger, gitCommandRunner);
    }

    private GoModCliExtractor goModCliExtractor(GoModCliDetectableOptions options) {
        GoModCommandRunner goModCommandRunner = new GoModCommandRunner(executableRunner);
        GoListParser goListParser = new GoListParser(gson);
        GoGraphParser goGraphParser = new GoGraphParser();
        GoModWhyParser goModWhyParser = new GoModWhyParser();
        GoVersionParser goVersionParser = new GoVersionParser();
        GoModGraphGenerator goModGraphGenerator = new GoModGraphGenerator(externalIdFactory);
        return new GoModCliExtractor(
            goModCommandRunner,
            goListParser,
            goGraphParser,
            goModWhyParser,
            goVersionParser,
            goModGraphGenerator,
            externalIdFactory,
            options.getExcludedDependencyTypes()
        );
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
        return new GradleReportTransformer(gradleInspectorOptions.getConfigurationTypeFilter());
    }

    private GradleRootMetadataParser gradleRootMetadataParser() {
        return new GradleRootMetadataParser();
    }

    private IvyParseExtractor ivyParseExtractor() {
        return new IvyParseExtractor(saxParser(), ivyProjectNameParser());
    }

    private IvyProjectNameParser ivyProjectNameParser() {
        return new IvyProjectNameParser(saxParser());
    }

    private Rebar3TreeParser rebar3TreeParser() {
        return new Rebar3TreeParser();
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

    private ConanLockfileExtractor conanLockfileExtractor(ConanLockfileExtractorOptions options) {
        ConanCodeLocationGenerator conanCodeLocationGenerator = new ConanCodeLocationGenerator(options.getDependencyTypeFilter(), options.preferLongFormExternalIds());
        ConanLockfileParser conanLockfileParser = new ConanLockfileParser(gson, conanCodeLocationGenerator, externalIdFactory);
        return new ConanLockfileExtractor(conanLockfileParser);
    }

    private ConanCliExtractor conanCliExtractor(ConanCliOptions options) {
        ConanCommandRunner conanCommandRunner = new ConanCommandRunner(executableRunner, options.getLockfilePath().orElse(null), options.getAdditionalArguments().orElse(null));
        ConanInfoLineAnalyzer conanInfoLineAnalyzer = new ConanInfoLineAnalyzer();
        ConanCodeLocationGenerator conanCodeLocationGenerator = new ConanCodeLocationGenerator(options.getDependencyTypeFilter(), options.preferLongFormExternalIds());
        NodeElementParser nodeElementParser = new NodeElementParser(conanInfoLineAnalyzer);
        ConanInfoNodeParser conanInfoNodeParser = new ConanInfoNodeParser(conanInfoLineAnalyzer, nodeElementParser);
        ConanInfoParser conanInfoParser = new ConanInfoParser(conanInfoNodeParser, conanCodeLocationGenerator, externalIdFactory);
        return new ConanCliExtractor(conanCommandRunner, conanInfoParser, toolVersionLogger);
    }

    private NpmLockfilePackager npmLockfilePackager(NpmLockfileOptions npmLockfileOptions) {
        NpmLockfileGraphTransformer npmLockfileGraphTransformer = new NpmLockfileGraphTransformer(npmLockfileOptions.getNpmDependencyTypeFilter());
        return new NpmLockfilePackager(gson, externalIdFactory, npmLockFileProjectIdTransformer(), npmLockfileGraphTransformer);
    }

    private NpmLockFileProjectIdTransformer npmLockFileProjectIdTransformer() {
        return new NpmLockFileProjectIdTransformer(gson, externalIdFactory);
    }

    private NpmLockfileExtractor npmLockfileExtractor(NpmLockfileOptions npmLockfileOptions) {
        return new NpmLockfileExtractor(npmLockfilePackager(npmLockfileOptions));
    }

    private NugetInspectorParser nugetInspectorParser() {
        return new NugetInspectorParser(gson, externalIdFactory);
    }

    private NugetInspectorExtractor nugetInspectorExtractor() {
        return new NugetInspectorExtractor(nugetInspectorParser(), fileFinder, executableRunner);
    }

    private ProjectInspectorParser projectInspectorParser() {
        return new ProjectInspectorParser(gson, externalIdFactory);
    }

    private ProjectInspectorExtractor projectInspectorExtractor() {
        return new ProjectInspectorExtractor(executableRunner, projectInspectorParser());
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

    private PoetryExtractor poetryExtractor() {
        return new PoetryExtractor(new PoetryLockParser());
    }

    private ToolPoetrySectionParser toolPoetrySectionParser() {
        return new ToolPoetrySectionParser();
    }

    private GemlockExtractor gemlockExtractor() {
        return new GemlockExtractor(externalIdFactory);
    }

    public SbtPluginFinder sbtPluginFinder() {
        return new SbtPluginFinder(executableRunner, new SbtCommandArgumentGenerator());
    }

    private SbtDotExtractor sbtDotExtractor() {
        return new SbtDotExtractor(
            executableRunner,
            sbtDotOutputParser(),
            sbtProjectMatcher(),
            sbtGraphParserTransformer(),
            sbtDotGraphNodeParser(),
            new SbtCommandArgumentGenerator()
        );
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

    private YarnPackager yarnPackager(YarnLockOptions yarnLockOptions) {
        YarnTransformer yarnTransformer = new YarnTransformer(externalIdFactory, yarnLockOptions.getYarnDependencyTypeFilter());
        return new YarnPackager(yarnTransformer);
    }

    private PackageJsonFiles packageJsonFiles() {
        return new PackageJsonFiles(packageJsonReader());
    }

    private PackageJsonReader packageJsonReader() {
        return new PackageJsonReader(gson);
    }

    private YarnLockExtractor yarnLockExtractor(YarnLockOptions yarnLockOptions) {
        return new YarnLockExtractor(yarnLockParser(), yarnPackager(yarnLockOptions), packageJsonFiles(), yarnLockOptions);
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
            toolVersionLogger
        );
    }

    private DockerExtractor dockerExtractor() {
        return new DockerExtractor(
            fileFinder,
            executableRunner,
            new BdioTransformer(),
            new ExternalIdFactory(),
            gson,
            new DockerInspectorResultsFileParser(gson),
            new ImageIdentifierGenerator()
        );
    }

    private GoGradleLockParser goGradleLockParser() {
        return new GoGradleLockParser(externalIdFactory);
    }

    private GoGradleExtractor goGradleExtractor() {
        return new GoGradleExtractor(goGradleLockParser());
    }

    private SAXParser saxParser() {
        try {
            return SAXParserFactory.newInstance().newSAXParser();
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException("Unable to create SAX Parser.", e);
        }
    }

    private SwiftCliParser swiftCliParser() {
        return new SwiftCliParser(gson);
    }

    private SwiftPackageTransformer swiftPackageTransformer() {
        GitUrlParser gitUrlParser = new GitUrlParser();
        return new SwiftPackageTransformer(gitUrlParser);
    }

    private SwiftExtractor swiftExtractor() {
        return new SwiftExtractor(executableRunner, swiftCliParser(), swiftPackageTransformer(), toolVersionLogger);
    }

    //#endregion Utility

}
