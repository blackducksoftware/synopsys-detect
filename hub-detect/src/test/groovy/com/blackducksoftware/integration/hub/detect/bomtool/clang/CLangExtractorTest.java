package com.blackducksoftware.integration.hub.detect.bomtool.clang;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

public class CLangExtractorTest {

    private static final String BOMTOOL_NAME = "CLang";
    private static final String EXTRACTION_ID = "testExtractionId";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    // TODO Under development...
    @Ignore
    @Test
    public void test() throws IOException {
        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        final DetectFileManager detectFileManager = Mockito.mock(DetectFileManager.class);
        final DependenciesListFileManager dependenciesListFileManager = Mockito.mock(DependenciesListFileManager.class);
        final CompileCommandsJsonFileParser compileCommandsJsonFileParser = Mockito.mock(CompileCommandsJsonFileParser.class);
        final CodeLocationAssembler codeLocationAssembler = Mockito.mock(CodeLocationAssembler.class);
        final CLangExtractor extractor = new CLangExtractor(executableRunner,
                detectFileManager, dependenciesListFileManager,
                compileCommandsJsonFileParser, codeLocationAssembler);

        final LinuxPackageManager pkgMgr = Mockito.mock(LinuxPackageManager.class);
        final File givenDir = new File("src/test/resources/clang/source/build");
        final int depth = 1;
        final ExtractionId extractionId = new ExtractionId(EXTRACTION_ID);
        final File jsonCompilationDatabaseFile = new File("src/test/resources/clang/source/build/compile_commands.json");

        Mockito.when(detectFileManager.getOutputDirectory(Mockito.anyString(), Mockito.any(ExtractionId.class))).thenReturn(new File("src/test/resources/clang/output"));
        // final List<CLangCompileCommand> compileCommands = compileCommandsJsonFileParser.parse(jsonCompilationDatabaseFile);

        final List<CompileCommand> compileCommands = new ArrayList<>();
        final CompileCommand compileCommand = new CompileCommand();
        compileCommand.directory = "src/test/resources/clang/source";
        compileCommand.file = "src/test/resources/clang/source/hello_world.cpp";
        compileCommand.command = "gcc hello_world.cpp";
        compileCommands.add(compileCommand);
        Mockito.when(compileCommandsJsonFileParser.parse(Mockito.any(File.class))).thenReturn(compileCommands);

        // final Optional<File> depsMkFile = dependenciesListFileManager.generate(workingDir, compileCommand);
        Mockito.when(dependenciesListFileManager.generate(new File(""), compileCommand)).thenReturn(Optional.of(new File("src/test/resources/clang/deps.mk")));

        extractor.extract(pkgMgr, givenDir, depth, extractionId, jsonCompilationDatabaseFile);
    }

}
