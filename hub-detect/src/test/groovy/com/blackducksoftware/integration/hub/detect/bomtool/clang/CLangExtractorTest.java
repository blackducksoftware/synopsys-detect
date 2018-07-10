package com.blackducksoftware.integration.hub.detect.bomtool.clang;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;

public class CLangExtractorTest {

    private static final String EXTRACTION_ID = "testExtractionId";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    // TODO Under development...
    @Test
    public void test() throws IOException {
        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        final DetectFileManager detectFileManager = Mockito.mock(DetectFileManager.class);
        final DependenciesListFileManager dependenciesListFileManager = Mockito.mock(DependenciesListFileManager.class);
        final CompileCommandsJsonFileParser compileCommandsJsonFileParser = Mockito.mock(CompileCommandsJsonFileParser.class);
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final CodeLocationAssembler codeLocationAssembler = new CodeLocationAssembler(externalIdFactory);
        final CLangExtractor extractor = new CLangExtractor(executableRunner,
                detectFileManager, dependenciesListFileManager,
                compileCommandsJsonFileParser, codeLocationAssembler);

        final LinuxPackageManager pkgMgr = Mockito.mock(LinuxPackageManager.class);
        final File givenDir = new File("src/test/resources/clang/source/build");
        final int depth = 1;
        final ExtractionId extractionId = new ExtractionId(EXTRACTION_ID);
        final File jsonCompilationDatabaseFile = new File("src/test/resources/clang/source/build/compile_commands.json");
        final File outputDir = new File("src/test/resources/clang/output");
        Mockito.when(detectFileManager.getOutputDirectory(Mockito.anyString(), Mockito.any(ExtractionId.class))).thenReturn(outputDir);
        // final List<CLangCompileCommand> compileCommands = compileCommandsJsonFileParser.parse(jsonCompilationDatabaseFile);

        final List<CompileCommand> compileCommands = new ArrayList<>();
        final CompileCommand compileCommand = new CompileCommand();
        compileCommand.directory = "src/test/resources/clang/source";
        compileCommand.file = "src/test/resources/clang/source/hello_world.cpp";
        compileCommand.command = "gcc hello_world.cpp";
        compileCommands.add(compileCommand);
        Mockito.when(compileCommandsJsonFileParser.parse(Mockito.any(File.class))).thenReturn(compileCommands);

        // final Optional<File> depsMkFile = dependenciesListFileManager.generate(workingDir, compileCommand);
        final File depsMkFile = new File("src/test/resources/clang/deps.mk");
        Mockito.when(dependenciesListFileManager.generate(outputDir, compileCommand)).thenReturn(Optional.of(depsMkFile));
        // dependenciesListFileManager.parse
        final File stdLibIncludeFile = new File("/usr/include/stdlib.h");
        final List<String> dependencyFilePaths = new ArrayList<>();
        dependencyFilePaths.add("src/test/resources/clang/source/myinclude.h");
        dependencyFilePaths.add(stdLibIncludeFile.getAbsolutePath());
        Mockito.when(dependenciesListFileManager.parse(depsMkFile)).thenReturn(dependencyFilePaths);
        // pkgMgr.getPackages(executableRunner, filesForIScan, dependencyFileWithMetaData)
        final DependencyFileDetails dependencyFile = new DependencyFileDetails(false, stdLibIncludeFile);

        final List<PackageDetails> packages = new ArrayList<>();
        packages.add(new PackageDetails("testPackageName", "testPackageVersion", "testPackageArch"));

        // TODO need to handle more cases here:
        Mockito.when(pkgMgr.getDefaultForge()).thenReturn(Forge.UBUNTU);
        Mockito.when(pkgMgr.getPackages(Mockito.any(ExecutableRunner.class), Mockito.any(Set.class), Mockito.any(DependencyFileDetails.class))).thenReturn(packages);
        Mockito.when(pkgMgr.getForges()).thenReturn(Arrays.asList(Forge.UBUNTU, Forge.DEBIAN));
        final Extraction extraction = extractor.extract(pkgMgr, givenDir, depth, extractionId, jsonCompilationDatabaseFile);
        System.out.printf("extraction dependency: %s", extraction.codeLocations.get(0).getDependencyGraph().getRootDependencies().iterator().next().name);
    }

}
