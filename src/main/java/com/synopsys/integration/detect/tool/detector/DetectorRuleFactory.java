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
import com.synopsys.integration.detectable.detectables.git.GitDetectable;
import com.synopsys.integration.detectable.detectables.git.GitParseDetectable;
import com.synopsys.integration.detectable.detectables.go.godep.GoDepLockDetectable;
import com.synopsys.integration.detectable.detectables.go.gogradle.GoGradleDetectable;
import com.synopsys.integration.detectable.detectables.go.gomod.GoModCliDetectable;
import com.synopsys.integration.detectable.detectables.go.vendor.GoVendorDetectable;
import com.synopsys.integration.detectable.detectables.go.vendr.GoVndrDetectable;
import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleInspectorDetectable;
import com.synopsys.integration.detectable.detectables.gradle.parsing.GradleProjectInspectorDetectable;
import com.synopsys.integration.detectable.detectables.ivy.parse.IvyParseDetectable;
import com.synopsys.integration.detectable.detectables.lerna.LernaDetectable;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenPomDetectable;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenPomWrapperDetectable;
import com.synopsys.integration.detectable.detectables.maven.parsing.MavenProjectInspectorDetectable;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliDetectable;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmPackageLockDetectable;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmShrinkwrapDetectable;
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
    public DetectorRuleSet createRules(DetectDetectableFactory detectableFactory) {
        DetectorRuleSetBuilder rules = new DetectorRuleSetBuilder(detectableFactory);

        rules.addDetector(DetectorType.CARGO, detector -> {
            detector.entryPoint(CargoLockDetectable.class);
        });

        rules.addDetector(DetectorType.CARGO, CargoLockDetectable.class);

        rules.addDetector(DetectorType.CARTHAGE, detector -> {
            detector.entryPoint(CarthageLockDetectable.class);
        });

        rules.addDetector(DetectorType.COCOAPODS, detector -> {
            detector.entryPoint(PodlockDetectable.class);
        });

        rules.addDetector(DetectorType.BITBAKE, detector -> {
            detector.entryPoint(BitbakeDetectable.class);
        });

        rules.addDetector(DetectorType.XCODE, detector -> {
                detector.entryPoint(XcodeProjectDetectable.class);
                detector.entryPoint(XcodeWorkspaceDetectable.class);
            }).allEntryPointsFallbackToNext()
            .notSelfNestable();

        rules.addDetector(DetectorType.SWIFT, detector -> {
                detector.entryPoint(SwiftPackageResolvedDetectable.class);
                detector.entryPoint(SwiftCliDetectable.class);
            }).allEntryPointsFallbackToNext()
            .nestableExceptTo(DetectorType.XCODE);

        rules.addDetector(DetectorType.CONAN, detector -> {
            detector.entryPoint(ConanLockfileDetectable.class);
            detector.entryPoint(ConanCliDetectable.class);
        }).allEntryPointsFallbackToNext();

        rules.addDetector(DetectorType.CONDA, detector -> {
            detector.entryPoint(CondaCliDetectable.class);
        });

        rules.addDetector(DetectorType.CPAN, detector -> {
            detector.entryPoint(CpanCliDetectable.class);
        });

        rules.addDetector(DetectorType.CRAN, detector -> {
            detector.entryPoint(PackratLockDetectable.class);
        });

        rules.addDetector(DetectorType.PACKAGIST, detector -> {
            detector.entryPoint(ComposerLockDetectable.class);
        });

        rules.addDetector(DetectorType.DART, detector -> {
            detector.entryPoint(DartPubDepDetectable.class);
        });

        rules.addDetector(DetectorType.GO_MOD, detector -> {
            detector.entryPoint(GoModCliDetectable.class);
        });

        rules.addDetector(DetectorType.GO_DEP, detector -> {
            detector.entryPoint(GoDepLockDetectable.class);
        });

        rules.addDetector(DetectorType.GO_VNDR, detector -> {
            detector.entryPoint(GoVndrDetectable.class);
        });

        rules.addDetector(DetectorType.GO_VENDOR, detector -> {
            detector.entryPoint(GoVendorDetectable.class);
        });

        rules.addDetector(DetectorType.GO_GRADLE, detector -> {
            detector.entryPoint(GoGradleDetectable.class);
        });

        rules.addDetector(DetectorType.GRADLE, detector -> {
            detector.entryPoint(GradleInspectorDetectable.class);
            detector.entryPoint(GradleProjectInspectorDetectable.class);
        }).allEntryPointsFallbackToNext();

        rules.addDetector(DetectorType.IVY, detector -> {
            detector.entryPoint(IvyParseDetectable.class);
        });

        rules.addDetector(DetectorType.HEX, detector -> {
            detector.entryPoint(RebarDetectable.class);
        });

        rules.addDetector(DetectorType.MAVEN, detector -> {
            detector.entryPoint(MavenPomDetectable.class)
                .fallback(MavenProjectInspectorDetectable.class);

            detector.entryPoint(MavenPomWrapperDetectable.class)
                .fallback(MavenProjectInspectorDetectable.class);
        });

        rules.addDetector(DetectorType.LERNA, detector -> {
            detector.entryPoint(LernaDetectable.class);
        });

        rules.addDetector(DetectorType.YARN, detector -> {
            detector.entryPoint(YarnLockDetectable.class);
        }).yieldsTo(DetectorType.LERNA);

        rules.addDetector(DetectorType.NPM, detector -> {
                detector.entryPoint(NpmPackageLockDetectable.class);
                detector.entryPoint(NpmShrinkwrapDetectable.class);
                detector.entryPoint(NpmCliDetectable.class);
            }).allEntryPointsFallbackToNext()
            .yieldsTo(DetectorType.LERNA, DetectorType.YARN, DetectorType.PNPM);

        rules.addDetector(DetectorType.PNPM, detector -> {
            detector.entryPoint(PnpmLockDetectable.class)
                .fallback(NpmCliDetectable.class);
        }).yieldsTo(DetectorType.LERNA);

        rules.addDetector(DetectorType.NUGET, detector -> {
            detector.entryPoint(NugetSolutionDetectable.class)
                .fallback(NugetProjectInspectorDetectable.class);

            detector.entryPoint(NugetProjectDetectable.class)
                .fallback(NugetProjectInspectorDetectable.class);
        });

        rules.addDetector(DetectorType.POETRY, detector -> {
            detector.entryPoint(PoetryDetectable.class);
        });

        rules.addDetector(DetectorType.PIP, detector -> {
            detector.entryPoint(PipenvDetectable.class);
            detector.entryPoint(PipInspectorDetectable.class);
            detector.entryPoint(PipfileLockDetectable.class);
        }).yieldsTo(DetectorType.POETRY);

        rules.addDetector(DetectorType.RUBYGEMS, detector -> {
            detector.entryPoint(GemlockDetectable.class);
            detector.entryPoint(GemspecParseDetectable.class);
        }).allEntryPointsFallbackToNext();

        rules.addDetector(DetectorType.GIT, detector -> {
            detector.entryPoint(GitDetectable.class);
            detector.entryPoint(GitParseDetectable.class); //TODO: Is this necessary?
        }).allEntryPointsFallbackToNext();

        rules.addDetector(DetectorType.SBT, detector -> {
            detector.entryPoint(SbtDetectable.class);
        });

        rules.addDetector(DetectorType.PEAR, detector -> {
            detector.entryPoint(PearCliDetectable.class);
        });

        rules.addDetector(DetectorType.CLANG, detector -> {
            detector.entryPoint(ClangDetectable.class);
        });

        return rules.build();
    }
}