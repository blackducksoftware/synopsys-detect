package com.blackducksoftware.integration.hub.detect.detector.clang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.mockito.Mockito;

import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.detector.ExtractionId;
import com.blackducksoftware.integration.hub.detect.detector.clang.compilecommand.CompileCommand;
import com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.ClangPackageManager;
import com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.ClangPackageManagerRunner;
import com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.ClangPackageManagerInfo;
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

    @Test
    public void testFilePath() throws ExecutableRunnerException {

    }

    //I'm not sure what this is testing... seems like only the codeLocationAssembler is not mocked, so if all it is testing is the assembler, why go through all this trouble?
    //I'm not sure we generally want to test extractors unless they are easy to test - and even then, what benefit do we gain? that we called an executable correctly?
    //I think we should focus on testing our parsers / conversion logic.
    // - jordan
    @Test
    public void testSimple() throws ExecutableRunnerException {
        final CompileCommand compileCommand = createCompileCommand("src/test/resources/clang/source/hello_world.cpp", "gcc hello_world.cpp", null);
        final List<String> dependencyFilePaths = createDependencyFilePaths(new File("/usr/include/nonexistentfile1.h"), new File("src/test/resources/clang/source/myinclude.h"));

        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        final DirectoryManager directoryManager = Mockito.mock(DirectoryManager.class);
        final FilePathGenerator filePathGenerator = Mockito.mock(FilePathGenerator.class);

        Mockito.when(filePathGenerator.fromCompileCommand(outputDir, compileCommand, true)).thenReturn(dependencyFilePaths);
        Mockito.when(executableRunner.executeFromDirQuietly(Mockito.any(File.class), Mockito.anyString(), Mockito.anyList())).thenReturn(new ExecutableOutput(0, "", ""));

        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final CodeLocationAssembler codeLocationAssembler = new CodeLocationAssembler(externalIdFactory);
        final ClangExtractor extractor = new ClangExtractor(executableRunner, gson, new DetectFileFinder(), directoryManager, filePathGenerator, codeLocationAssembler);


        final ClangPackageManager packageManager = Mockito.mock(ClangPackageManager.class);
        final ClangPackageManagerInfo packageManagerInfo = Mockito.mock(ClangPackageManagerInfo.class);

        Mockito.when(packageManager.getPackageManagerInfo()).thenReturn(packageManagerInfo);
        Mockito.when(packageManagerInfo.getDefaultForge()).thenReturn(Forge.UBUNTU);
        Mockito.when(packageManagerInfo.getForges()).thenReturn(Arrays.asList(Forge.UBUNTU, Forge.DEBIAN));

        final ClangPackageManagerRunner packageManagerRunner = Mockito.mock(ClangPackageManagerRunner.class);
        final List<PackageDetails> packages = new ArrayList<>();
        packages.add(new PackageDetails("testPackageName", "testPackageVersion", "testPackageArch"));
        Mockito.when(packageManagerRunner.getPackages(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(packages);


        final File givenDir = new File("src/test/resources/clang/source/build");
        final int depth = 1;
        final ExtractionId extractionId = new ExtractionId(DetectorType.CLANG, EXTRACTION_ID);
        final File jsonCompilationDatabaseFile = new File("src/test/resources/clang/source/build/compile_commands.json");

        Mockito.when(directoryManager.getExtractionOutputDirectory(Mockito.any(ExtractionId.class))).thenReturn(outputDir);


        final Extraction extraction = extractor.extract(packageManager, packageManagerRunner, givenDir, depth, extractionId, jsonCompilationDatabaseFile, true);

        checkGeneratedDependenciesSimple(extraction);
    }

    @Test
    public void testMultipleCommandsDependenciesPackages() throws ExecutableRunnerException {

        final CompileCommand compileCommandWrapperHelloWorld = createCompileCommand("src/test/resources/clang/source/hello_world.cpp", "gcc hello_world.cpp", null);
        final CompileCommand compileCommandWrapperGoodbyeWorld = createCompileCommand("src/test/resources/clang/source/goodbye_world.cpp", "gcc goodbye_world.cpp", null);

        final List<String> dependencyFilePathsHelloWorld = createDependencyFilePaths(new File("src/test/resources/clang/source/myinclude.h"), new File("/usr/include/nonexistentfile1.h"), new File("/usr/include/nonexistentfile2.h"));
        final List<String> dependencyFilePathsGoodbyeWorld = createDependencyFilePaths(new File("/usr/include/nonexistentfile4.h"), new File("/usr/include/nonexistentfile3.h"));

        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        final DirectoryManager directoryManager = Mockito.mock(DirectoryManager.class);
        final FilePathGenerator filePathGenerator = Mockito.mock(FilePathGenerator.class);

        Mockito.when(filePathGenerator.fromCompileCommand(outputDir, compileCommandWrapperHelloWorld, true)).thenReturn(dependencyFilePathsHelloWorld);
        Mockito.when(filePathGenerator.fromCompileCommand(outputDir, compileCommandWrapperGoodbyeWorld, true)).thenReturn(dependencyFilePathsGoodbyeWorld);

        Mockito.when(executableRunner.executeFromDirQuietly(Mockito.any(File.class), Mockito.anyString(), Mockito.anyList())).thenReturn(new ExecutableOutput(0, "", ""));

        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final CodeLocationAssembler codeLocationAssembler = new CodeLocationAssembler(externalIdFactory);
        final ClangExtractor extractor = new ClangExtractor(executableRunner, gson, new DetectFileFinder(), directoryManager, filePathGenerator, codeLocationAssembler);

        final ClangPackageManager packageManager = Mockito.mock(ClangPackageManager.class);
        final ClangPackageManagerInfo packageManagerInfo = Mockito.mock(ClangPackageManagerInfo.class);

        Mockito.when(packageManager.getPackageManagerInfo()).thenReturn(packageManagerInfo);
        Mockito.when(packageManagerInfo.getDefaultForge()).thenReturn(Forge.CENTOS);
        Mockito.when(packageManagerInfo.getForges()).thenReturn(Arrays.asList(Forge.CENTOS, Forge.FEDORA, Forge.REDHAT));

        final ClangPackageManagerRunner packageManagerRunner = Mockito.mock(ClangPackageManagerRunner.class);
        final List<PackageDetails> packages = new ArrayList<>();
        packages.add(new PackageDetails("testPackageName1", "testPackageVersion1", "testPackageArch1"));
        packages.add(new PackageDetails("testPackageName2", "testPackageVersion2", "testPackageArch2"));
        Mockito.when(packageManagerRunner.getPackages(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(packages);

        final File givenDir = new File("src/test/resources/clang/source/build");
        final int depth = 1;
        final ExtractionId extractionId = new ExtractionId(DetectorType.CLANG, EXTRACTION_ID);
        final File jsonCompilationDatabaseFile = new File("src/test/resources/clang/source/build/compile_commands.json");

        Mockito.when(directoryManager.getExtractionOutputDirectory(Mockito.any(ExtractionId.class))).thenReturn(outputDir);

        final Extraction extraction = extractor.extract(packageManager, packageManagerRunner, givenDir, depth, extractionId, jsonCompilationDatabaseFile, true);

        checkGeneratedDependenciesComplex(extraction);
    }

    @Test
    public void testJsonWithArgumentsNotCommand() throws ExecutableRunnerException {

        final String[] argsHello = { "gcc", "hello_world.cpp" };
        final CompileCommand compileCommandWrapperHelloWorld = createCompileCommand("src/test/resources/clang/source/hello_world.cpp", null, argsHello);
        final String[] argsGoodbye = { "gcc", "goodbye_world.cpp" };
        final CompileCommand compileCommandWrapperGoodbyeWorld = createCompileCommand("src/test/resources/clang/source/goodbye_world.cpp", null, argsGoodbye);

        final List<String> dependencyFilePathsHelloWorld = createDependencyFilePaths(new File("src/test/resources/clang/source/myinclude.h"), new File("/usr/include/nonexistentfile1.h"), new File("/usr/include/nonexistentfile2.h"));
        final List<String> dependencyFilePathsGoodbyeWorld = createDependencyFilePaths(new File("/usr/include/nonexistentfile4.h"), new File("/usr/include/nonexistentfile3.h"));

        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        final DirectoryManager directoryManager = Mockito.mock(DirectoryManager.class);
        final FilePathGenerator filePathGenerator = Mockito.mock(FilePathGenerator.class);

        Mockito.when(filePathGenerator.fromCompileCommand(outputDir, compileCommandWrapperHelloWorld, true)).thenReturn(dependencyFilePathsHelloWorld);
        Mockito.when(filePathGenerator.fromCompileCommand(outputDir, compileCommandWrapperGoodbyeWorld, true)).thenReturn(dependencyFilePathsGoodbyeWorld);
        Mockito.when(executableRunner.executeFromDirQuietly(Mockito.any(File.class), Mockito.anyString(), Mockito.anyList())).thenReturn(new ExecutableOutput(0, "", ""));

        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final CodeLocationAssembler codeLocationAssembler = new CodeLocationAssembler(externalIdFactory);
        final ClangExtractor extractor = new ClangExtractor(executableRunner, gson, new DetectFileFinder(),
            directoryManager, filePathGenerator,
            codeLocationAssembler);

        final ClangPackageManager packageManager = Mockito.mock(ClangPackageManager.class);
        final ClangPackageManagerInfo packageManagerInfo = Mockito.mock(ClangPackageManagerInfo.class);

        Mockito.when(packageManager.getPackageManagerInfo()).thenReturn(packageManagerInfo);
        Mockito.when(packageManagerInfo.getDefaultForge()).thenReturn(Forge.CENTOS);
        Mockito.when(packageManagerInfo.getForges()).thenReturn(Arrays.asList(Forge.CENTOS, Forge.FEDORA, Forge.REDHAT));

        final ClangPackageManagerRunner packageManagerRunner = Mockito.mock(ClangPackageManagerRunner.class);
        final List<PackageDetails> packages = new ArrayList<>();
        packages.add(new PackageDetails("testPackageName1", "testPackageVersion1", "testPackageArch1"));
        packages.add(new PackageDetails("testPackageName2", "testPackageVersion2", "testPackageArch2"));
        Mockito.when(packageManagerRunner.getPackages(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(packages);

        final File givenDir = new File("src/test/resources/clang/source/build");
        final int depth = 1;
        final ExtractionId extractionId = new ExtractionId(DetectorType.CLANG, EXTRACTION_ID);
        final File jsonCompilationDatabaseFile = new File("src/test/resources/clang/source/build/compile_commands_usesArguments.json");

        Mockito.when(directoryManager.getExtractionOutputDirectory(Mockito.any(ExtractionId.class))).thenReturn(outputDir);

        final Extraction extraction = extractor.extract(packageManager, packageManagerRunner, givenDir, depth, extractionId, jsonCompilationDatabaseFile, true);

        checkGeneratedDependenciesComplex(extraction);
    }

    private void checkGeneratedDependenciesSimple(Extraction extraction) {
        boolean ubuntuComponentVerified = false;
        Set<Dependency> dependencies = extraction.codeLocations.get(0).getDependencyGraph().getRootDependencies();
        Iterator<Dependency> iter = dependencies.iterator();
        while (iter.hasNext()) {
            Dependency dependency = iter.next();
            System.out.printf("Checking dependency %s\n", dependency.externalId);
            if ("ubuntu".equals(dependency.externalId.forge.getName())) {
                assertEquals("testPackageName", dependency.name);
                assertEquals("testPackageVersion", dependency.version);
                assertEquals("testPackageArch", dependency.externalId.architecture);
                assertEquals("ubuntu", dependency.externalId.forge.getName());
                assertEquals(null, dependency.externalId.group);
                assertEquals("testPackageName", dependency.externalId.name);
                assertEquals(null, dependency.externalId.path);
                assertEquals("testPackageVersion", dependency.externalId.version);
                ubuntuComponentVerified = true;
            }
        }
        assertTrue(ubuntuComponentVerified);
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

    private List<String> createDependencyFilePaths(File... dependencyFiles) {
        List<String> dependencyFilePaths = new ArrayList<>();
        for (File dependencyFile : dependencyFiles) {
            dependencyFilePaths.add(dependencyFile.getAbsolutePath());
        }
        return dependencyFilePaths;
    }

    private CompileCommand createCompileCommand(String file, String command, String[] arguments) {
        final CompileCommand compileCommand = new CompileCommand();
        compileCommand.directory = "src/test/resources/clang/source";
        compileCommand.file = file;
        compileCommand.command = command;
        compileCommand.arguments = arguments;
        return compileCommand;
    }

}
