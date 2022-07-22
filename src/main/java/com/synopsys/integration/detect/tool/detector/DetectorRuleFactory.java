package com.synopsys.integration.detect.tool.detector;

import com.synopsys.integration.detect.tool.detector.factory.DetectDetectableFactory;
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
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.rule.DetectorRuleSet;
import com.synopsys.integration.detector.rule.builder.DetectorRuleSetBuilder;

public class DetectorRuleFactory {

    //TODO (8.0.0): Decide if things called lock should use the lock default or not.
    public DetectorRuleSet createRules(DetectDetectableFactory detectableFactory) {
        DetectorRuleSetBuilder rules = new DetectorRuleSetBuilder(detectableFactory);

        rules.addDetector(DetectorType.CARGO, detector -> {
            detector.entryPoint(CargoLockDetectable.class)
                .search().defaults();
        });

        rules.addDetector(DetectorType.CARTHAGE, detector -> {
            detector.entryPoint(CarthageLockDetectable.class)
                .search().defaults();
        });

        rules.addDetector(DetectorType.COCOAPODS, detector -> {
            detector.entryPoint(PodlockDetectable.class)
                .search().defaults();
        });

        rules.addDetector(DetectorType.BITBAKE, detector -> {
            detector.entryPoint(BitbakeDetectable.class)
                .search().defaults();
        });

        rules.addDetector(DetectorType.XCODE, detector -> {
            detector.entryPoint(XcodeWorkspaceDetectable.class)
                .search().defaults();
            detector.entryPoint(XcodeProjectDetectable.class)
                .search()
                .noMaxDepth()
                .nestable()
                .notNestableBeneath(XcodeWorkspaceDetectable.class)
                .notNestableBeneath(XcodeProjectDetectable.class);
        }).allEntryPointsFallbackToNext();

        rules.addDetector(DetectorType.SWIFT, detector -> {
            detector.entryPoint(SwiftPackageResolvedDetectable.class)
                .search().defaults().nestableExceptTo(DetectorType.XCODE);
            detector.entryPoint(SwiftCliDetectable.class)
                .search().defaults().nestableExceptTo(DetectorType.XCODE);
        }).allEntryPointsFallbackToNext();

        rules.addDetector(DetectorType.CONAN, detector -> {
            detector.entryPoint(ConanLockfileDetectable.class)
                .search().defaults();
            detector.entryPoint(ConanCliDetectable.class)
                .search().defaults();
        }).allEntryPointsFallbackToNext();

        rules.addDetector(DetectorType.CONDA, detector -> {
            detector.entryPoint(CondaCliDetectable.class)
                .search().defaults();
        });

        rules.addDetector(DetectorType.CPAN, detector -> {
            detector.entryPoint(CpanCliDetectable.class)
                .search().defaults();
        });

        rules.addDetector(DetectorType.CRAN, detector -> {
            detector.entryPoint(PackratLockDetectable.class)
                .search().defaults();
        });

        rules.addDetector(DetectorType.PACKAGIST, detector -> {
            detector.entryPoint(ComposerLockDetectable.class)
                .search().defaults();
        });

        rules.addDetector(DetectorType.DART, detector -> {
            detector.entryPoint(DartPubDepDetectable.class)
                .search().defaults();
            detector.entryPoint(DartPubSpecLockDetectable.class)
                .search().defaults();
        }).allEntryPointsFallbackToNext();

        rules.addDetector(DetectorType.GO_MOD, detector -> {
            detector.entryPoint(GoModCliDetectable.class)
                .search().defaults();
        });

        rules.addDetector(DetectorType.GO_DEP, detector -> {
            detector.entryPoint(GoDepLockDetectable.class)
                .search().defaults();
        });

        rules.addDetector(DetectorType.GO_VNDR, detector -> {
            detector.entryPoint(GoVndrDetectable.class)
                .search().defaults();
        });

        rules.addDetector(DetectorType.GO_VENDOR, detector -> {
            detector.entryPoint(GoVendorDetectable.class)
                .search().defaults();
        });

        rules.addDetector(DetectorType.GO_GRADLE, detector -> {
            detector.entryPoint(GoGradleDetectable.class)
                .search().defaults();
        });

        rules.addDetector(DetectorType.GRADLE, detector -> {
            detector.entryPoint(GradleInspectorDetectable.class)
                .fallback(GradleProjectInspectorDetectable.class)
                .search().defaults();
        });

        rules.addDetector(DetectorType.IVY, detector -> {
            detector.entryPoint(IvyParseDetectable.class)
                .search().defaultLock();
        });

        rules.addDetector(DetectorType.HEX, detector -> {
            detector.entryPoint(RebarDetectable.class)
                .search().defaults();
        });

        rules.addDetector(DetectorType.MAVEN, detector -> {
            detector.entryPoint(MavenPomDetectable.class)
                .fallback(MavenProjectInspectorDetectable.class)
                .search().defaults();

            detector.entryPoint(MavenPomWrapperDetectable.class)
                .fallback(MavenProjectInspectorDetectable.class)
                .search().defaults();
        });

        rules.addDetector(DetectorType.LERNA, detector -> {
            detector.entryPoint(LernaDetectable.class)
                .search().defaults();
        });

        rules.addDetector(DetectorType.YARN, detector -> {
            detector.entryPoint(YarnLockDetectable.class)
                .search().defaultLock();
        }).yieldsTo(DetectorType.LERNA);

        rules.addDetector(DetectorType.NPM, detector -> {
                detector.entryPoint(NpmShrinkwrapDetectable.class)
                    .search().defaultLock();
                detector.entryPoint(NpmPackageLockDetectable.class)
                    .search().defaultLock();
                detector.entryPoint(NpmCliDetectable.class)
                    .search().defaults();
                detector.entryPoint(NpmPackageJsonParseDetectable.class)
                    .search().defaults(); //maybe this one should be defaultLock?
            }).allEntryPointsFallbackToNext()
            .yieldsTo(DetectorType.LERNA, DetectorType.YARN, DetectorType.PNPM);

        rules.addDetector(DetectorType.PNPM, detector -> {
            detector.entryPoint(PnpmLockDetectable.class)
                .search().defaultLock();
        }).yieldsTo(DetectorType.LERNA);

        rules.addDetector(DetectorType.NUGET, detector -> {
            //four different detectables, last one will be the project inspector
            detector.entryPoint(NugetSolutionDetectable.class)
                .fallback(NugetProjectInspectorDetectable.class)
                .search().defaults();

            detector.entryPoint(NugetProjectDetectable.class)
                .fallback(NugetProjectInspectorDetectable.class)
                .search().defaults();
        });

        rules.addDetector(DetectorType.POETRY, detector -> {
            detector.entryPoint(PoetryDetectable.class)
                .search().defaults();
        });

        rules.addDetector(DetectorType.PIP, detector -> {
                detector.entryPoint(PipenvDetectable.class)
                    .search().defaults();
                detector.entryPoint(PipInspectorDetectable.class)
                    .search().defaults();
                detector.entryPoint(PipfileLockDetectable.class)
                    .search().defaults();
            })
            .allEntryPointsFallbackToNext()
            .yieldsTo(DetectorType.POETRY);

        rules.addDetector(DetectorType.RUBYGEMS, detector -> {
            detector.entryPoint(GemlockDetectable.class)
                .search().defaults();//should this be default lock?
            detector.entryPoint(GemspecParseDetectable.class)
                .search().defaults();
        }).allEntryPointsFallbackToNext();

        rules.addDetector(DetectorType.GIT, detector -> {
            detector.entryPoint(GitCliDetectable.class)
                .search().defaults();
            detector.entryPoint(GitParseDetectable.class)
                .search().defaults();
        }).allEntryPointsFallbackToNext();

        rules.addDetector(DetectorType.SBT, detector -> {
            detector.entryPoint(SbtDetectable.class)
                .search().defaults();
        });

        rules.addDetector(DetectorType.PEAR, detector -> {
            detector.entryPoint(PearCliDetectable.class)
                .search().defaults();
        });

        rules.addDetector(DetectorType.CLANG, detector -> {
            detector.entryPoint(ClangDetectable.class)
                .search().defaults();
        });

        return rules.build();
    }
}