package com.blackducksoftware.integration.hub.detect.factory;

import com.blackducksoftware.integration.hub.detect.bomtool.clang.ClangExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.CodeLocationAssembler;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.DependenciesListFileManager;
import com.blackducksoftware.integration.hub.detect.bomtool.cocoapods.PodlockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.cocoapods.PodlockParser;
import com.blackducksoftware.integration.hub.detect.bomtool.conda.CondaCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.conda.CondaListParser;
import com.blackducksoftware.integration.hub.detect.bomtool.cpan.CpanCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.cpan.CpanListParser;
import com.blackducksoftware.integration.hub.detect.bomtool.cran.PackratLockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.cran.PackratPackager;
import com.blackducksoftware.integration.hub.detect.bomtool.go.DepPackager;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoDepExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoVndrExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleReportParser;
import com.blackducksoftware.integration.hub.detect.bomtool.hex.Rebar3TreeParser;
import com.blackducksoftware.integration.hub.detect.bomtool.hex.RebarExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenCodeLocationPackager;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmCliParser;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmLockfileExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmLockfilePackager;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetInspectorPackager;
import com.blackducksoftware.integration.hub.detect.bomtool.packagist.ComposerLockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.packagist.PackagistParser;
import com.blackducksoftware.integration.hub.detect.bomtool.pear.PearCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.pear.PearParser;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipInspectorTreeParser;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipenvExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipenvGraphParser;
import com.blackducksoftware.integration.hub.detect.bomtool.rubygems.GemlockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.SbtResolutionCacheExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.yarn.YarnListParser;
import com.blackducksoftware.integration.hub.detect.bomtool.yarn.YarnLockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.yarn.YarnLockParser;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.google.gson.Gson;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class ExtractorFactory {
    //If you find your extractor needing more dependencies than this, I would reconsider the design of the extractor.
    private final Gson gson;
    private final ExternalIdFactory externalIdFactory;
    private final ExecutableRunner executableRunner;
    private final DetectFileManager detectFileManager;
    private final DetectFileFinder detectFileFinder;
    private final DetectConfiguration detectConfiguration;

    public ExtractorFactory(final Gson gson, final ExternalIdFactory externalIdFactory, final ExecutableRunner executableRunner, final DetectFileManager detectFileManager,
        final DetectFileFinder detectFileFinder, final DetectConfiguration detectConfiguration){
        this.gson = gson;
        this.externalIdFactory = externalIdFactory;
        this.executableRunner = executableRunner;
        this.detectFileManager = detectFileManager;
        this.detectFileFinder = detectFileFinder;
        this.detectConfiguration = detectConfiguration;
    }

    private CodeLocationAssembler codeLocationAssembler() {
        return new CodeLocationAssembler(externalIdFactory);
    }

    private DependenciesListFileManager clangDependenciesListFileParser() {
        return new DependenciesListFileManager(executableRunner);
    }

    public ClangExtractor clangExtractor() {
        return new ClangExtractor(executableRunner, gson, detectFileFinder, detectFileManager, clangDependenciesListFileParser(), codeLocationAssembler());
    }
    
    private PodlockParser podlockParser() {
        return new PodlockParser(externalIdFactory);
    }

    public PodlockExtractor podlockExtractor() {
        return new PodlockExtractor(podlockParser(), externalIdFactory);
    }

    private CondaListParser condaListParser() {
        return new CondaListParser(gson, externalIdFactory);
    }

    public CondaCliExtractor condaCliExtractor() {
        return new CondaCliExtractor(condaListParser(), externalIdFactory, executableRunner, detectConfiguration, detectFileManager);
    }

    private CpanListParser cpanListParser() {
        return new CpanListParser(externalIdFactory);
    }

    public CpanCliExtractor cpanCliExtractor() {
        return new CpanCliExtractor(cpanListParser(), externalIdFactory, executableRunner, detectFileManager);
    }

    private PackratPackager packratPackager() {
        return new PackratPackager(externalIdFactory);
    }

    public PackratLockExtractor packratLockExtractor() {
        return new PackratLockExtractor(packratPackager(), externalIdFactory, detectFileFinder);
    }

    public GoDepExtractor goDepExtractor() {
        return new GoDepExtractor(depPackager(), externalIdFactory);
    }
    
    public GoVndrExtractor goVndrExtractor() {
        return new GoVndrExtractor(externalIdFactory);
    }

    private DepPackager depPackager() {
        return new DepPackager(executableRunner, externalIdFactory, detectConfiguration);
    }

    private GradleReportParser gradleReportParser() {
        return new GradleReportParser(externalIdFactory);
    }
    
    public GradleInspectorExtractor gradleInspectorExtractor() {
        return new GradleInspectorExtractor(executableRunner, detectFileFinder, detectFileManager, gradleReportParser(), detectConfiguration);
    }

    public Rebar3TreeParser rebar3TreeParser() {
        return new Rebar3TreeParser(externalIdFactory);
    }

    public RebarExtractor rebarExtractor() {
        return new RebarExtractor(executableRunner, rebar3TreeParser());
    }

    private MavenCodeLocationPackager mavenCodeLocationPackager() {
        return new MavenCodeLocationPackager(externalIdFactory);
    }

    public MavenCliExtractor mavenCliExtractor() {
        return new MavenCliExtractor(executableRunner, mavenCodeLocationPackager(), detectConfiguration);
    }

    private NpmLockfilePackager npmLockfilePackager() {
        return new NpmLockfilePackager(gson, externalIdFactory);
    }

    public NpmCliParser npmCliParser() {
        return new NpmCliParser(externalIdFactory);
    }

    public NpmCliExtractor npmCliExtractor() {
        return new NpmCliExtractor(executableRunner, npmCliParser(), detectConfiguration);
    }

    public NpmLockfileExtractor npmLockfileExtractor() {
        return new NpmLockfileExtractor(npmLockfilePackager(), detectConfiguration);
    }

    private NugetInspectorPackager nugetInspectorPackager() {
        return new NugetInspectorPackager(gson, externalIdFactory);
    }

    public NugetInspectorExtractor nugetInspectorExtractor() {
        return new NugetInspectorExtractor(detectFileManager, nugetInspectorPackager(), executableRunner, detectFileFinder, detectConfiguration);
    }

    private PackagistParser packagistParser() {
        return new PackagistParser(externalIdFactory, detectConfiguration);
    }

    public ComposerLockExtractor composerLockExtractor() {
        return new ComposerLockExtractor(packagistParser());
    }

    public PearParser pearParser() {
        return new PearParser(externalIdFactory, detectConfiguration);
    }

    public PearCliExtractor pearCliExtractor() {
        return new PearCliExtractor(detectFileFinder, externalIdFactory, pearParser(), executableRunner, detectFileManager);
    }

    private PipenvGraphParser pipenvGraphParser() {
        return new PipenvGraphParser(externalIdFactory);
    }

    public PipenvExtractor pipenvExtractor() {
        return new PipenvExtractor(executableRunner, pipenvGraphParser(), detectConfiguration);
    }

    private PipInspectorTreeParser pipInspectorTreeParser() {
        return new PipInspectorTreeParser(externalIdFactory);
    }

    public PipInspectorExtractor pipInspectorExtractor() {
        return new PipInspectorExtractor(executableRunner, pipInspectorTreeParser(), detectConfiguration);
    }
    
    public GemlockExtractor gemlockExtractor() {
        return new GemlockExtractor(externalIdFactory);
    }

    public SbtResolutionCacheExtractor sbtResolutionCacheExtractor() {
        return new SbtResolutionCacheExtractor(detectFileFinder, externalIdFactory, detectConfiguration);
    }

    private YarnListParser yarnListParser() {
        return new YarnListParser(externalIdFactory, yarnLockParser());
    }

    private YarnLockParser yarnLockParser() {
        return new YarnLockParser();
    }

    public YarnLockExtractor yarnLockExtractor() {
        return new YarnLockExtractor(externalIdFactory, yarnListParser(), executableRunner, detectConfiguration);
    }

}
