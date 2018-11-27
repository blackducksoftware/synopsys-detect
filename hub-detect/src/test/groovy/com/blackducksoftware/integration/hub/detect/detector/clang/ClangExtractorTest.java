package com.blackducksoftware.integration.hub.detect.detector.clang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
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

import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.detector.ExtractionId;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.google.gson.Gson;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class ClangExtractorTest {

    private static final String EXTRACTION_ID = "testExtractionId";
    private final Gson gson = new Gson();
    private final File outputDir = new File("src/test/resources/clang/output");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testSimple() throws ExecutableRunnerException {
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);

        final CompileCommand compileCommandWrapper = createCompileCommand("src/test/resources/clang/source/hello_world.cpp", "gcc hello_world.cpp", null);
        final Set<String> dependencyFilePaths = createDependencyFilePaths(new File("/usr/include/stdlib.h"), new File("src/test/resources/clang/source/myinclude.h"));

        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        final DirectoryManager directoryManager = Mockito.mock(DirectoryManager.class);
        final DependenciesListFileManager dependenciesListFileManager = Mockito.mock(DependenciesListFileManager.class);

        Mockito.when(dependenciesListFileManager.generateDependencyFilePaths(outputDir, compileCommandWrapper)).thenReturn(dependencyFilePaths);
        Mockito.when(executableRunner.executeFromDirQuietly(Mockito.any(File.class), Mockito.anyString(), Mockito.anyList())).thenReturn(new ExecutableOutput(0, "", ""));

        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final CodeLocationAssembler codeLocationAssembler = new CodeLocationAssembler(externalIdFactory);
        final ClangExtractor extractor = new ClangExtractor(executableRunner, gson, new DetectFileFinder(),
            directoryManager, dependenciesListFileManager,
            codeLocationAssembler);

        final ClangLinuxPackageManager pkgMgr = Mockito.mock(ClangLinuxPackageManager.class);
        final File givenDir = new File("src/test/resources/clang/source/build");
        final int depth = 1;
        final ExtractionId extractionId = new ExtractionId(DetectorType.CLANG, EXTRACTION_ID);
        final File jsonCompilationDatabaseFile = new File("src/test/resources/clang/source/build/compile_commands.json");

        Mockito.when(directoryManager.getExtractionOutputDirectory(Mockito.any(ExtractionId.class))).thenReturn(outputDir);

        final List<PackageDetails> packages = new ArrayList<>();
        packages.add(new PackageDetails("testPackageName", "testPackageVersion", "testPackageArch"));

        Mockito.when(pkgMgr.getDefaultForge()).thenReturn(Forge.UBUNTU);
        Mockito.when(pkgMgr.getPackages(Mockito.any(File.class), Mockito.any(ExecutableRunner.class), Mockito.any(Set.class), Mockito.any(DependencyFileDetails.class))).thenReturn(packages);
        Mockito.when(pkgMgr.getForges()).thenReturn(Arrays.asList(Forge.UBUNTU, Forge.DEBIAN));
        final Extraction extraction = extractor.extract(pkgMgr, givenDir, depth, extractionId, jsonCompilationDatabaseFile);

        checkGeneratedDependenciesSimple(extraction);
    }

    @Test
    public void testMultipleCommandsDependenciesPackages() throws ExecutableRunnerException {
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);

        final CompileCommand compileCommandWrapperHelloWorld = createCompileCommand("src/test/resources/clang/source/hello_world.cpp", "gcc hello_world.cpp", null);
        final CompileCommand compileCommandWrapperGoodbyeWorld = createCompileCommand("src/test/resources/clang/source/goodbye_world.cpp", "gcc goodbye_world.cpp", null);

        final Set<String> dependencyFilePathsHelloWorld = createDependencyFilePaths(new File("src/test/resources/clang/source/myinclude.h"), new File("/usr/include/stdlib.h"), new File("/usr/include/math.h"));
        final Set<String> dependencyFilePathsGoodbyeWorld = createDependencyFilePaths(new File("/usr/include/pwd.h"), new File("/usr/include/printf.h"));

        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        final DirectoryManager directoryManager = Mockito.mock(DirectoryManager.class);
        final DependenciesListFileManager dependenciesListFileManager = Mockito.mock(DependenciesListFileManager.class);

        Mockito.when(dependenciesListFileManager.generateDependencyFilePaths(outputDir, compileCommandWrapperHelloWorld)).thenReturn(dependencyFilePathsHelloWorld);
        Mockito.when(dependenciesListFileManager.generateDependencyFilePaths(outputDir, compileCommandWrapperGoodbyeWorld)).thenReturn(dependencyFilePathsGoodbyeWorld);

        Mockito.when(executableRunner.executeFromDirQuietly(Mockito.any(File.class), Mockito.anyString(), Mockito.anyList())).thenReturn(new ExecutableOutput(0, "", ""));

        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final CodeLocationAssembler codeLocationAssembler = new CodeLocationAssembler(externalIdFactory);
        final ClangExtractor extractor = new ClangExtractor(executableRunner, gson, new DetectFileFinder(),
            directoryManager, dependenciesListFileManager,
            codeLocationAssembler);

        final ClangLinuxPackageManager pkgMgr = Mockito.mock(ClangLinuxPackageManager.class);
        final File givenDir = new File("src/test/resources/clang/source/build");
        final int depth = 1;
        final ExtractionId extractionId = new ExtractionId(DetectorType.CLANG, EXTRACTION_ID);
        final File jsonCompilationDatabaseFile = new File("src/test/resources/clang/source/build/compile_commands.json");

        Mockito.when(directoryManager.getExtractionOutputDirectory(Mockito.any(ExtractionId.class))).thenReturn(outputDir);

        final List<PackageDetails> packages = new ArrayList<>();
        packages.add(new PackageDetails("testPackageName1", "testPackageVersion1", "testPackageArch1"));
        packages.add(new PackageDetails("testPackageName2", "testPackageVersion2", "testPackageArch2"));

        Mockito.when(pkgMgr.getDefaultForge()).thenReturn(Forge.CENTOS);
        Mockito.when(pkgMgr.getPackages(Mockito.any(File.class), Mockito.any(ExecutableRunner.class), Mockito.any(Set.class), Mockito.any(DependencyFileDetails.class))).thenReturn(packages);
        Mockito.when(pkgMgr.getForges()).thenReturn(Arrays.asList(Forge.CENTOS, Forge.FEDORA, Forge.REDHAT));
        final Extraction extraction = extractor.extract(pkgMgr, givenDir, depth, extractionId, jsonCompilationDatabaseFile);

        checkGeneratedDependenciesComplex(extraction);
    }

    @Test
    public void testJsonWithArgumentsNotCommand() throws ExecutableRunnerException {
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);

        final String[] argsHello = { "gcc", "hello_world.cpp" };
        final CompileCommand compileCommandWrapperHelloWorld = createCompileCommand("src/test/resources/clang/source/hello_world.cpp", null, argsHello);
        final String[] argsGoodbye = { "gcc", "goodbye_world.cpp" };
        final CompileCommand compileCommandWrapperGoodbyeWorld = createCompileCommand("src/test/resources/clang/source/goodbye_world.cpp", null, argsGoodbye);

        final Set<String> dependencyFilePathsHelloWorld = createDependencyFilePaths(new File("src/test/resources/clang/source/myinclude.h"), new File("/usr/include/stdlib.h"), new File("/usr/include/math.h"));
        final Set<String> dependencyFilePathsGoodbyeWorld = createDependencyFilePaths(new File("/usr/include/pwd.h"), new File("/usr/include/printf.h"));
        ;

        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        final DirectoryManager directoryManager = Mockito.mock(DirectoryManager.class);
        final DependenciesListFileManager dependenciesListFileManager = Mockito.mock(DependenciesListFileManager.class);

        Mockito.when(dependenciesListFileManager.generateDependencyFilePaths(outputDir, compileCommandWrapperHelloWorld)).thenReturn(dependencyFilePathsHelloWorld);
        Mockito.when(dependenciesListFileManager.generateDependencyFilePaths(outputDir, compileCommandWrapperGoodbyeWorld)).thenReturn(dependencyFilePathsGoodbyeWorld);
        Mockito.when(executableRunner.executeFromDirQuietly(Mockito.any(File.class), Mockito.anyString(), Mockito.anyList())).thenReturn(new ExecutableOutput(0, "", ""));

        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final CodeLocationAssembler codeLocationAssembler = new CodeLocationAssembler(externalIdFactory);
        final ClangExtractor extractor = new ClangExtractor(executableRunner, gson, new DetectFileFinder(),
            directoryManager, dependenciesListFileManager,
            codeLocationAssembler);

        final ClangLinuxPackageManager pkgMgr = Mockito.mock(ClangLinuxPackageManager.class);
        final File givenDir = new File("src/test/resources/clang/source/build");
        final int depth = 1;
        final ExtractionId extractionId = new ExtractionId(DetectorType.CLANG, EXTRACTION_ID);
        final File jsonCompilationDatabaseFile = new File("src/test/resources/clang/source/build/compile_commands_usesArguments.json");

        Mockito.when(directoryManager.getExtractionOutputDirectory(Mockito.any(ExtractionId.class))).thenReturn(outputDir);

        final List<PackageDetails> packages = new ArrayList<>();
        packages.add(new PackageDetails("testPackageName1", "testPackageVersion1", "testPackageArch1"));
        packages.add(new PackageDetails("testPackageName2", "testPackageVersion2", "testPackageArch2"));

        Mockito.when(pkgMgr.getDefaultForge()).thenReturn(Forge.CENTOS);
        Mockito.when(pkgMgr.getPackages(Mockito.any(File.class), Mockito.any(ExecutableRunner.class), Mockito.any(Set.class), Mockito.any(DependencyFileDetails.class))).thenReturn(packages);
        Mockito.when(pkgMgr.getForges()).thenReturn(Arrays.asList(Forge.CENTOS, Forge.FEDORA, Forge.REDHAT));
        final Extraction extraction = extractor.extract(pkgMgr, givenDir, depth, extractionId, jsonCompilationDatabaseFile);

        checkGeneratedDependenciesComplex(extraction);
    }

    private void checkGeneratedDependenciesSimple(Extraction extraction) {
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

    private void checkGeneratedDependenciesComplex(Extraction extraction) {
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

    private Set<String> createDependencyFilePaths(File... dependencyFiles) {
        Set<String> dependencyFilePaths = new HashSet<>();
        for (File dependencyFile : dependencyFiles) {
            dependencyFilePaths.add(dependencyFile.getAbsolutePath());
        }
        return dependencyFilePaths;
    }

    private CompileCommand createCompileCommand(String file, String command, String[] arguments) {
        final CompileCommandJsonData compileCommandJsonData = new CompileCommandJsonData();
        compileCommandJsonData.directory = "src/test/resources/clang/source";
        compileCommandJsonData.file = file;
        compileCommandJsonData.command = command;
        compileCommandJsonData.arguments = arguments;
        return new CompileCommand(compileCommandJsonData);
    }

}
