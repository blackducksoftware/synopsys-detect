/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detectable.factory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.BdioTransformer;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.resolver.BashResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.BazelResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CondaResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CpanResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CpanmResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.DockerResolver;
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
import com.synopsys.integration.detectable.detectable.executable.resolver.SwiftResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.inspector.GradleInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.PipInspectorResolver;
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
import com.synopsys.integration.detectable.detectables.clang.ClangDetectable;
import com.synopsys.integration.detectable.detectables.clang.ClangDetectableOptions;
import com.synopsys.integration.detectable.detectables.clang.ClangExtractor;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommandDatabaseParser;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommandParser;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.ClangPackageDetailsTransformer;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.DependencyFileDetailGenerator;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.DependencyListFileParser;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.FilePathGenerator;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerFactory;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfoFactory;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerRunner;
import com.synopsys.integration.detectable.detectables.cocoapods.PodlockDetectable;
import com.synopsys.integration.detectable.detectables.cocoapods.PodlockExtractor;
import com.synopsys.integration.detectable.detectables.cocoapods.parser.PodlockParser;
import com.synopsys.integration.detectable.detectables.conan.ConanCodeLocationGenerator;
import com.synopsys.integration.detectable.detectables.conan.ConanExtractorOptions;
import com.synopsys.integration.detectable.detectables.conan.cli.ConanCliDetectable;
import com.synopsys.integration.detectable.detectables.conan.cli.ConanCliExtractor;
import com.synopsys.integration.detectable.detectables.conan.cli.ConanInfoNodeParser;
import com.synopsys.integration.detectable.detectables.conan.cli.ConanInfoParser;
import com.synopsys.integration.detectable.detectables.conan.cli.ConanResolver;
import com.synopsys.integration.detectable.detectables.conda.CondaCliDetectable;
import com.synopsys.integration.detectable.detectables.conda.CondaCliDetectableOptions;
import com.synopsys.integration.detectable.detectables.conda.CondaCliExtractor;
import com.synopsys.integration.detectable.detectables.conda.parser.CondaListParser;
import com.synopsys.integration.detectable.detectables.cpan.CpanCliDetectable;
import com.synopsys.integration.detectable.detectables.cpan.CpanCliExtractor;
import com.synopsys.integration.detectable.detectables.cpan.parse.CpanListParser;
import com.synopsys.integration.detectable.detectables.cran.PackratLockDetectable;
import com.synopsys.integration.detectable.detectables.cran.PackratLockExtractor;
import com.synopsys.integration.detectable.detectables.cran.parse.PackratDescriptionFileParser;
import com.synopsys.integration.detectable.detectables.cran.parse.PackratLockFileParser;
import com.synopsys.integration.detectable.detectables.docker.DockerDetectable;
import com.synopsys.integration.detectable.detectables.docker.DockerDetectableOptions;
import com.synopsys.integration.detectable.detectables.docker.DockerExtractor;
import com.synopsys.integration.detectable.detectables.docker.DockerInspectorResolver;
import com.synopsys.integration.detectable.detectables.git.cli.GitCliDetectable;
import com.synopsys.integration.detectable.detectables.git.cli.GitCliExtractor;
import com.synopsys.integration.detectable.detectables.git.cli.GitUrlParser;
import com.synopsys.integration.detectable.detectables.git.parsing.GitParseDetectable;
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
import com.synopsys.integration.detectable.detectables.go.gomod.GoModCliExtractor;
import com.synopsys.integration.detectable.detectables.go.gomod.GoModCommandExecutor;
import com.synopsys.integration.detectable.detectables.go.gomod.GoModGraphParser;
import com.synopsys.integration.detectable.detectables.go.gomod.GoModGraphTransformer;
import com.synopsys.integration.detectable.detectables.go.gomod.ReplacementDataExtractor;
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
import com.synopsys.integration.detectable.detectables.gradle.parsing.GradleParseDetectable;
import com.synopsys.integration.detectable.detectables.gradle.parsing.GradleParseExtractor;
import com.synopsys.integration.detectable.detectables.gradle.parsing.parse.BuildGradleParser;
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
import com.synopsys.integration.detectable.detectables.maven.parsing.MavenParseDetectable;
import com.synopsys.integration.detectable.detectables.maven.parsing.MavenParseExtractor;
import com.synopsys.integration.detectable.detectables.maven.parsing.MavenParseOptions;
import com.synopsys.integration.detectable.detectables.npm.NpmPackageJsonDiscoverer;
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
import com.synopsys.integration.detectable.detectables.pip.PipInspectorDetectable;
import com.synopsys.integration.detectable.detectables.pip.PipInspectorDetectableOptions;
import com.synopsys.integration.detectable.detectables.pip.PipInspectorExtractor;
import com.synopsys.integration.detectable.detectables.pip.PipenvDetectable;
import com.synopsys.integration.detectable.detectables.pip.PipenvDetectableOptions;
import com.synopsys.integration.detectable.detectables.pip.PipenvExtractor;
import com.synopsys.integration.detectable.detectables.pip.parser.PipEnvJsonGraphParser;
import com.synopsys.integration.detectable.detectables.pip.parser.PipInspectorTreeParser;
import com.synopsys.integration.detectable.detectables.pip.parser.PipenvFreezeParser;
import com.synopsys.integration.detectable.detectables.pip.parser.PipenvTransformer;
import com.synopsys.integration.detectable.detectables.pip.poetry.PoetryDetectable;
import com.synopsys.integration.detectable.detectables.pip.poetry.PoetryExtractor;
import com.synopsys.integration.detectable.detectables.pip.poetry.parser.PoetryLockParser;
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
import com.synopsys.integration.detectable.detectables.sbt.SbtResolutionCacheDetectable;
import com.synopsys.integration.detectable.detectables.sbt.SbtResolutionCacheDetectableOptions;
import com.synopsys.integration.detectable.detectables.sbt.SbtResolutionCacheExtractor;
import com.synopsys.integration.detectable.detectables.swift.SwiftCliDetectable;
import com.synopsys.integration.detectable.detectables.swift.SwiftCliParser;
import com.synopsys.integration.detectable.detectables.swift.SwiftExtractor;
import com.synopsys.integration.detectable.detectables.swift.SwiftPackageTransformer;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockDetectable;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockExtractor;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockOptions;
import com.synopsys.integration.detectable.detectables.yarn.YarnPackager;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnTransformer;

/*
 Entry point for creating detectables using most
 */
public class DetectableFactory {

    private final FileFinder fileFinder;
    private final DetectableExecutableRunner executableRunner;
    private final ExternalIdFactory externalIdFactory;
    private final Gson gson;

    public DetectableFactory(FileFinder fileFinder, DetectableExecutableRunner executableRunner, ExternalIdFactory externalIdFactory, Gson gson) {
        this.fileFinder = fileFinder;
        this.executableRunner = executableRunner;
        this.externalIdFactory = externalIdFactory;
        this.gson = gson;
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

    public GemlockDetectable createGemlockDetectable(DetectableEnvironment environment) {
        return new GemlockDetectable(environment, fileFinder, gemlockExtractor());
    }

    public GitParseDetectable createGitParseDetectable(DetectableEnvironment environment) {
        return new GitParseDetectable(environment, fileFinder, gitParseExtractor());
    }

    public GitCliDetectable createGitCliDetectable(DetectableEnvironment environment, GitResolver gitResolver) {
        return new GitCliDetectable(environment, fileFinder, gitCliExtractor(), gitResolver);
    }

    public GoModCliDetectable createGoModCliDetectable(DetectableEnvironment environment, GoResolver goResolver) {
        return new GoModCliDetectable(environment, fileFinder, goResolver, goModCliExtractor());
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
        return new GradleDetectable(environment, fileFinder, gradleResolver, gradleInspectorResolver, gradleInspectorExtractor(), gradleInspectorOptions);
    }

    public GradleParseDetectable createGradleParseDetectable(DetectableEnvironment environment) {
        return new GradleParseDetectable(environment, fileFinder, gradleParseExtractor());
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

    public ConanCliDetectable createConanCliDetectable(DetectableEnvironment environment, ConanResolver conanResolver, ConanExtractorOptions conanCliExtractorOptions) {
        return new ConanCliDetectable(environment, fileFinder, conanResolver, conanCliExtractor(), conanCliExtractorOptions);
    }

    public NpmCliDetectable createNpmCliDetectable(DetectableEnvironment environment, NpmResolver npmResolver, NpmCliExtractorOptions npmCliExtractorOptions) {
        return new NpmCliDetectable(environment, fileFinder, npmResolver, npmCliExtractor(), npmPackageJsonDiscoverer(), npmCliExtractorOptions);
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

    public PodlockDetectable createPodLockDetectable(DetectableEnvironment environment) {
        return new PodlockDetectable(environment, fileFinder, podlockExtractor());
    }

    public PoetryDetectable createPoetryDetectable(DetectableEnvironment environment) {
        return new PoetryDetectable(environment, fileFinder, poetryExtractor());
    }

    public RebarDetectable createRebarDetectable(DetectableEnvironment environment, Rebar3Resolver rebar3Resolver) {
        return new RebarDetectable(environment, fileFinder, rebar3Resolver, rebarExtractor());
    }

    public SbtResolutionCacheDetectable createSbtResolutionCacheDetectable(DetectableEnvironment environment, SbtResolutionCacheDetectableOptions sbtResolutionCacheDetectableOptions) {
        return new SbtResolutionCacheDetectable(environment, fileFinder, sbtResolutionCacheExtractor(), sbtResolutionCacheDetectableOptions);
    }

    public SwiftCliDetectable createSwiftCliDetectable(DetectableEnvironment environment, SwiftResolver swiftResolver) {
        return new SwiftCliDetectable(environment, fileFinder, swiftExtractor(), swiftResolver);
    }

    public YarnLockDetectable createYarnLockDetectable(DetectableEnvironment environment, YarnLockOptions yarnLockOptions) {
        return new YarnLockDetectable(environment, fileFinder, yarnLockExtractor(yarnLockOptions));
    }

    public LernaDetectable createLernaDetectable(DetectableEnvironment environment, LernaResolver lernaResolver, YarnLockOptions yarnLockOptions, NpmLockfileOptions npmLockfileOptions, LernaOptions lernaOptions) {
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

    private ClangPackageDetailsTransformer clangPackageDetailsTransformer() {
        return new ClangPackageDetailsTransformer(externalIdFactory);
    }

    private CompileCommandDatabaseParser compileCommandDatabaseParser() {
        return new CompileCommandDatabaseParser(gson);
    }

    private CompileCommandParser compileCommandParser() {
        return new CompileCommandParser();
    }

    private ClangExtractor clangExtractor() {
        return new ClangExtractor(executableRunner, dependencyFileDetailGenerator(), clangPackageDetailsTransformer(), compileCommandDatabaseParser());
    }

    private PodlockParser podlockParser() {
        return new PodlockParser(externalIdFactory);
    }

    private PodlockExtractor podlockExtractor() {
        return new PodlockExtractor(podlockParser());
    }

    private CondaListParser condaListParser() {
        return new CondaListParser(gson, externalIdFactory);
    }

    private CondaCliExtractor condaCliExtractor() {
        return new CondaCliExtractor(condaListParser(), executableRunner);
    }

    private CpanListParser cpanListParser() {
        return new CpanListParser(externalIdFactory);
    }

    private CpanCliExtractor cpanCliExtractor() {
        return new CpanCliExtractor(cpanListParser(), executableRunner);
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
        return new GitCliExtractor(executableRunner, gitUrlParser());
    }

    private GoLockParser goLockParser() {
        return new GoLockParser(externalIdFactory);
    }

    private GoDepExtractor goDepExtractor() {
        return new GoDepExtractor(goLockParser());
    }

    private GoModGraphParser goModGraphParser() {
        return new GoModGraphParser(externalIdFactory);
    }

    private GoModCommandExecutor goModCommandExecutor() {
        return new GoModCommandExecutor(executableRunner);
    }

    private GoModGraphTransformer goModGraphTransformer() {
        return new GoModGraphTransformer(replacementDataExtractor());
    }

    private ReplacementDataExtractor replacementDataExtractor() {
        return new ReplacementDataExtractor(gson);
    }

    private GoModCliExtractor goModCliExtractor() {
        return new GoModCliExtractor(goModCommandExecutor(), goModGraphParser(), goModGraphTransformer());
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

    private GradleReportTransformer gradleReportTransformer() {
        return new GradleReportTransformer(externalIdFactory);
    }

    private GradleRootMetadataParser gradleRootMetadataParser() {
        return new GradleRootMetadataParser();
    }

    private Rebar3TreeParser rebar3TreeParser() {
        return new Rebar3TreeParser(externalIdFactory);
    }

    private RebarExtractor rebarExtractor() {
        return new RebarExtractor(executableRunner, rebar3TreeParser());
    }

    private MavenCodeLocationPackager mavenCodeLocationPackager() {
        return new MavenCodeLocationPackager(externalIdFactory);
    }

    private MavenCliExtractor mavenCliExtractor() {
        return new MavenCliExtractor(executableRunner, mavenCodeLocationPackager());
    }

    private ConanCliExtractor conanCliExtractor() {
        return new ConanCliExtractor(executableRunner, new ConanInfoParser(new ConanInfoNodeParser(), new ConanCodeLocationGenerator()));
    }

    private NpmCliParser npmCliDependencyFinder() {
        return new NpmCliParser(externalIdFactory);
    }

    private NpmLockfilePackager npmLockfilePackager() {
        return new NpmLockfilePackager(gson, externalIdFactory);
    }

    private NpmCliExtractor npmCliExtractor() {
        return new NpmCliExtractor(executableRunner, npmCliDependencyFinder());
    }

    private NpmPackageJsonDiscoverer npmPackageJsonDiscoverer() {
        return new NpmPackageJsonDiscoverer(gson);
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
        return new PipInspectorExtractor(executableRunner, pipInspectorTreeParser());
    }

    private PoetryExtractor poetryExtractor() {
        return new PoetryExtractor(new PoetryLockParser());
    }

    private GemlockExtractor gemlockExtractor() {
        return new GemlockExtractor(externalIdFactory);
    }

    private SbtResolutionCacheExtractor sbtResolutionCacheExtractor() {
        return new SbtResolutionCacheExtractor(fileFinder, externalIdFactory);
    }

    private YarnLockParser yarnLockParser() {
        return new YarnLockParser();
    }

    private YarnTransformer yarnTransformer() {
        return new YarnTransformer(externalIdFactory);
    }

    private YarnPackager yarnPackager(YarnLockOptions yarnLockOptions) {
        return new YarnPackager(gson, yarnLockParser(), yarnTransformer(), yarnLockOptions);
    }

    private YarnLockExtractor yarnLockExtractor(YarnLockOptions yarnLockOptions) {
        return new YarnLockExtractor(yarnPackager(yarnLockOptions));
    }

    private BitbakeRecipesParser bitbakeRecipesParser() {
        return new BitbakeRecipesParser();
    }

    private BitbakeRecipesToLayerMapConverter bitbakeRecipesToLayerMap() {
        return new BitbakeRecipesToLayerMapConverter();
    }

    private BitbakeExtractor bitbakeExtractor() {
        return new BitbakeExtractor(executableRunner, fileFinder, graphParserTransformer(), bitbakeGraphTransformer(), bitbakeRecipesParser(), bitbakeRecipesToLayerMap());
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

    private GradleInspectorExtractor gradleInspectorExtractor() {
        return new GradleInspectorExtractor(fileFinder, gradleRunner(), gradleReportParser(), gradleReportTransformer(), gradleRootMetadataParser());
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

    private BuildGradleParser buildGradleParser() {
        return new BuildGradleParser(externalIdFactory);
    }

    private GradleParseExtractor gradleParseExtractor() {
        return new GradleParseExtractor(buildGradleParser());
    }

    private SwiftCliParser swiftCliParser() {
        return new SwiftCliParser(gson);
    }

    private SwiftPackageTransformer swiftPackageTransformer() {
        return new SwiftPackageTransformer(externalIdFactory);
    }

    private SwiftExtractor swiftExtractor() {
        return new SwiftExtractor(executableRunner, swiftCliParser(), swiftPackageTransformer());
    }

    private LernaPackageDiscoverer lernaPackageDiscoverer() {
        return new LernaPackageDiscoverer(executableRunner, gson);
    }

    private LernaPackager lernaPackager(NpmLockfileOptions npmLockfileOptions, YarnLockOptions yarnLockOptions, LernaOptions lernaOptions) {
        return new LernaPackager(fileFinder, npmLockfilePackager(), npmLockfileOptions, yarnPackager(yarnLockOptions), lernaOptions);
    }

    private LernaExtractor lernaExtractor(NpmLockfileOptions npmLockfileOptions, YarnLockOptions yarnLockOptions, LernaOptions lernaOptions) {
        return new LernaExtractor(lernaPackageDiscoverer(), lernaPackager(npmLockfileOptions, yarnLockOptions, lernaOptions));
    }
    //#endregion Utility

}
