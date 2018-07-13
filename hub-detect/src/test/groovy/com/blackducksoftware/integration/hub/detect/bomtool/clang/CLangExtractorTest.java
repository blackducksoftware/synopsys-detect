package com.blackducksoftware.integration.hub.detect.bomtool.clang;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;

public class CLangExtractorTest {

    private static final String EXTRACTION_ID = "testExtractionId";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    // TODO Under development... Needs to test multiple compile commands, multiple dependency files, multiple packages
    @Test
    public void test() throws IOException, ExecutableRunnerException {
        final File outputDir = new File("src/test/resources/clang/output");

        final List<CompileCommand> compileCommands = new ArrayList<>();
        final CompileCommand compileCommand = new CompileCommand();
        compileCommand.directory = "src/test/resources/clang/source";
        compileCommand.file = "src/test/resources/clang/source/hello_world.cpp";
        compileCommand.command = "gcc hello_world.cpp";
        compileCommands.add(compileCommand);

        final File stdLibIncludeFile = new File("/usr/include/stdlib.h");
        final Set<String> dependencyFilePaths = new HashSet<>();
        dependencyFilePaths.add("src/test/resources/clang/source/myinclude.h");
        dependencyFilePaths.add(stdLibIncludeFile.getAbsolutePath());

        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        final DetectFileManager detectFileManager = Mockito.mock(DetectFileManager.class);
        final DependenciesListFileManager dependenciesListFileManager = Mockito.mock(DependenciesListFileManager.class);

        Mockito.when(dependenciesListFileManager.generateDependencyFilePaths(outputDir, compileCommand)).thenReturn(dependencyFilePaths);
        Mockito.when(executableRunner.executeFromDirQuietly(Mockito.any(File.class), Mockito.anyString(), Mockito.anyList())).thenReturn(new ExecutableOutput(0, "", ""));

        final CompileCommandsJsonFileParser compileCommandsJsonFileParser = Mockito.mock(CompileCommandsJsonFileParser.class);
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final CodeLocationAssembler codeLocationAssembler = new CodeLocationAssembler(externalIdFactory);
        final ClangExtractor extractor = new ClangExtractor(executableRunner,
                detectFileManager, dependenciesListFileManager,
                compileCommandsJsonFileParser, codeLocationAssembler);

        final LinuxPackageManager pkgMgr = Mockito.mock(LinuxPackageManager.class);
        final File givenDir = new File("src/test/resources/clang/source/build");
        final int depth = 1;
        final ExtractionId extractionId = new ExtractionId(BomToolGroupType.CLANG, EXTRACTION_ID);
        final File jsonCompilationDatabaseFile = new File("src/test/resources/clang/source/build/compile_commands.json");

        Mockito.when(detectFileManager.getOutputDirectory(Mockito.any(ExtractionId.class))).thenReturn(outputDir);
        Mockito.when(compileCommandsJsonFileParser.parse(Mockito.any(File.class))).thenReturn(compileCommands);

        final List<PackageDetails> packages = new ArrayList<>();
        packages.add(new PackageDetails("testPackageName", "testPackageVersion", "testPackageArch"));

        // TODO need to handle more cases here:
        Mockito.when(pkgMgr.getDefaultForge()).thenReturn(Forge.UBUNTU);
        Mockito.when(pkgMgr.getPackages(Mockito.any(ExecutableRunner.class), Mockito.any(Set.class), Mockito.any(DependencyFileDetails.class))).thenReturn(packages);
        Mockito.when(pkgMgr.getForges()).thenReturn(Arrays.asList(Forge.UBUNTU, Forge.DEBIAN));
        final Extraction extraction = extractor.extract(pkgMgr, givenDir, depth, extractionId, jsonCompilationDatabaseFile);

        final Dependency dependency = extraction.codeLocations.get(0).getDependencyGraph().getRootDependencies().iterator().next();
        assertEquals("testPackageName", extraction.codeLocations.get(0).getDependencyGraph().getRootDependencies().iterator().next().name);
        assertEquals("testPackageVersion", extraction.codeLocations.get(0).getDependencyGraph().getRootDependencies().iterator().next().version);
        assertEquals("testPackageArch", dependency.externalId.architecture);
        assertEquals("ubuntu", dependency.externalId.forge.getName());
        assertEquals(null, dependency.externalId.group);
        assertEquals("testPackageName", dependency.externalId.name);
        assertEquals(null, dependency.externalId.path);
        assertEquals("testPackageVersion", dependency.externalId.version);
    }

}
