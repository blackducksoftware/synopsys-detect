package com.blackducksoftware.integration.hub.detect.bomtool.clang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.SystemUtils;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.google.gson.Gson;
import com.synopsys.integration.hub.bdio.model.Forge;
import com.synopsys.integration.hub.bdio.model.dependency.Dependency;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class ClangExtractorTest {

    private static final String EXTRACTION_ID = "testExtractionId";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testSimple() throws IOException, ExecutableRunnerException {
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);

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

        final Gson gson = new Gson();
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final CodeLocationAssembler codeLocationAssembler = new CodeLocationAssembler(externalIdFactory);
        final ClangExtractor extractor = new ClangExtractor(executableRunner, gson, new DetectFileFinder(),
            detectFileManager, dependenciesListFileManager,
            codeLocationAssembler);

        final ClangLinuxPackageManager pkgMgr = Mockito.mock(ClangLinuxPackageManager.class);
        final File givenDir = new File("src/test/resources/clang/source/build");
        final int depth = 1;
        final ExtractionId extractionId = new ExtractionId(BomToolGroupType.CLANG, EXTRACTION_ID);
        final File jsonCompilationDatabaseFile = new File("src/test/resources/clang/source/build/compile_commands.json");

        Mockito.when(detectFileManager.getOutputDirectory(Mockito.any(ExtractionId.class))).thenReturn(outputDir);

        final List<PackageDetails> packages = new ArrayList<>();
        packages.add(new PackageDetails("testPackageName", "testPackageVersion", "testPackageArch"));

        Mockito.when(pkgMgr.getDefaultForge()).thenReturn(Forge.UBUNTU);
        Mockito.when(pkgMgr.getPackages(Mockito.any(ExecutableRunner.class), Mockito.any(Set.class), Mockito.any(DependencyFileDetails.class))).thenReturn(packages);
        Mockito.when(pkgMgr.getForges()).thenReturn(Arrays.asList(Forge.UBUNTU, Forge.DEBIAN));
        final Extraction extraction = extractor.extract(pkgMgr, givenDir, depth, extractionId, jsonCompilationDatabaseFile);

        final Dependency dependency = extraction.codeLocations.get(0).getDependencyGraph().getRootDependencies().iterator().next();
        assertEquals("testPackageName", dependency.name);
        assertEquals("testPackageVersion", dependency.version);
        assertEquals("testPackageArch", dependency.externalId.architecture);
        assertEquals("ubuntu", dependency.externalId.forge.getName());
        assertEquals(null, dependency.externalId.group);
        assertEquals("testPackageName", dependency.externalId.name);
        assertEquals(null, dependency.externalId.path);
        assertEquals("testPackageVersion", dependency.externalId.version);
    }

    @Test
    public void testMultipleCommandsDependenciesPackages() throws IOException, ExecutableRunnerException {
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);

        final File outputDir = new File("src/test/resources/clang/output");

        final List<CompileCommand> compileCommands = new ArrayList<>();
        final CompileCommand compileCommandHelloWorld = new CompileCommand();
        compileCommandHelloWorld.directory = "src/test/resources/clang/source";
        compileCommandHelloWorld.file = "src/test/resources/clang/source/hello_world.cpp";
        compileCommandHelloWorld.command = "gcc hello_world.cpp";
        compileCommands.add(compileCommandHelloWorld);

        final CompileCommand compileCommandGoodbyeWorld = new CompileCommand();
        compileCommandGoodbyeWorld.directory = "src/test/resources/clang/source";
        compileCommandGoodbyeWorld.file = "src/test/resources/clang/source/goodbye_world.cpp";
        compileCommandGoodbyeWorld.command = "gcc goodbye_world.cpp";
        compileCommands.add(compileCommandGoodbyeWorld);

        final File stdLibIncludeFile = new File("/usr/include/stdlib.h");
        final File mathIncludeFile = new File("/usr/include/math.h");
        final Set<String> dependencyFilePathsHelloWorld = new HashSet<>();
        dependencyFilePathsHelloWorld.add("src/test/resources/clang/source/myinclude.h");
        dependencyFilePathsHelloWorld.add(stdLibIncludeFile.getAbsolutePath());
        dependencyFilePathsHelloWorld.add(mathIncludeFile.getAbsolutePath());

        final File pwdIncludeFile = new File("/usr/include/pwd.h");
        final File printfIncludeFile = new File("/usr/include/printf.h");
        final Set<String> dependencyFilePathsGoodbyeWorld = new HashSet<>();
        dependencyFilePathsGoodbyeWorld.add(pwdIncludeFile.getAbsolutePath());
        dependencyFilePathsGoodbyeWorld.add(printfIncludeFile.getAbsolutePath());

        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        final DetectFileManager detectFileManager = Mockito.mock(DetectFileManager.class);
        final DependenciesListFileManager dependenciesListFileManager = Mockito.mock(DependenciesListFileManager.class);

        Mockito.when(dependenciesListFileManager.generateDependencyFilePaths(outputDir, compileCommandHelloWorld)).thenReturn(dependencyFilePathsHelloWorld);
        Mockito.when(dependenciesListFileManager.generateDependencyFilePaths(outputDir, compileCommandGoodbyeWorld)).thenReturn(dependencyFilePathsGoodbyeWorld);

        Mockito.when(executableRunner.executeFromDirQuietly(Mockito.any(File.class), Mockito.anyString(), Mockito.anyList())).thenReturn(new ExecutableOutput(0, "", ""));

        final Gson gson = new Gson();
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final CodeLocationAssembler codeLocationAssembler = new CodeLocationAssembler(externalIdFactory);
        final ClangExtractor extractor = new ClangExtractor(executableRunner, gson, new DetectFileFinder(),
            detectFileManager, dependenciesListFileManager,
            codeLocationAssembler);

        final ClangLinuxPackageManager pkgMgr = Mockito.mock(ClangLinuxPackageManager.class);
        final File givenDir = new File("src/test/resources/clang/source/build");
        final int depth = 1;
        final ExtractionId extractionId = new ExtractionId(BomToolGroupType.CLANG, EXTRACTION_ID);
        final File jsonCompilationDatabaseFile = new File("src/test/resources/clang/source/build/compile_commands.json");

        Mockito.when(detectFileManager.getOutputDirectory(Mockito.any(ExtractionId.class))).thenReturn(outputDir);

        final List<PackageDetails> packages = new ArrayList<>();
        packages.add(new PackageDetails("testPackageName1", "testPackageVersion1", "testPackageArch1"));
        packages.add(new PackageDetails("testPackageName2", "testPackageVersion2", "testPackageArch2"));

        Mockito.when(pkgMgr.getDefaultForge()).thenReturn(Forge.CENTOS);
        Mockito.when(pkgMgr.getPackages(Mockito.any(ExecutableRunner.class), Mockito.any(Set.class), Mockito.any(DependencyFileDetails.class))).thenReturn(packages);
        Mockito.when(pkgMgr.getForges()).thenReturn(Arrays.asList(Forge.CENTOS, Forge.FEDORA, Forge.REDHAT));
        final Extraction extraction = extractor.extract(pkgMgr, givenDir, depth, extractionId, jsonCompilationDatabaseFile);

        final Set<Dependency> dependencies = extraction.codeLocations.get(0).getDependencyGraph().getRootDependencies();
        assertEquals(6, dependencies.size());
        for (final Dependency dependency : dependencies) {
            System.out.printf("Checking dependency: %s:%s / %s\n", dependency.name, dependency.version, dependency.externalId.forge.getName());
            final char indexChar = dependency.name.charAt(15);
            assertTrue(indexChar == '1' || indexChar == '2' || indexChar == '3');

            final String forge = dependency.externalId.forge.getName();
            assertTrue("centos".equals(forge) || "fedora".equals(forge) || "redhat".equals(forge));

            assertEquals(String.format("testPackageName%c", indexChar), dependency.name);
            assertEquals(String.format("testPackageVersion%c", indexChar), dependency.version);
            assertEquals(String.format("testPackageArch%c", indexChar), dependency.externalId.architecture);

            assertEquals(forge, dependency.externalId.forge.getName());
            assertEquals(null, dependency.externalId.group);
            assertEquals(String.format("testPackageName%c", indexChar), dependency.externalId.name);
            assertEquals(null, dependency.externalId.path);
            assertEquals(String.format("testPackageVersion%c", indexChar), dependency.externalId.version);
        }
    }

}
