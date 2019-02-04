package com.synopsys.integration.detectable.detectables.clang.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.ClangPackageDetailsTransformer;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.DependencyFileDetailGenerator;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.DependencyFileDetails;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.FilePathGenerator;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.PackageDetails;

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
    public void testFileNotExistSkipped() throws ExecutableRunnerException {
        File mockFile = Mockito.mock(File.class);
        Mockito.when(mockFile.toString()).thenReturn("Example");

        FilePathGenerator filePathGenerator = Mockito.mock(FilePathGenerator.class);
        Mockito.when(filePathGenerator.fromCompileCommand(mockFile, null, true)).thenReturn(Arrays.asList("does_not_exist.h"));

        DependencyFileDetailGenerator dependencyFileDetailGenerator = new DependencyFileDetailGenerator(filePathGenerator);

        Set<DependencyFileDetails> fileDetailsSet = dependencyFileDetailGenerator.fromCompileCommands(Arrays.asList(null), null, true);
        Assert.assertEquals(0, fileDetailsSet.size());
    }

    @Test
    public void testJsonWithArgumentsNotCommand() throws ExecutableRunnerException {
        final Set<PackageDetails> packages = new HashSet<>();
        packages.add(new PackageDetails("testPackageName1", "testPackageVersion1", "testPackageArch1"));
        packages.add(new PackageDetails("testPackageName2", "testPackageVersion2", "testPackageArch2"));

        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final ClangPackageDetailsTransformer clangPackageDetailsTransformer = new ClangPackageDetailsTransformer(externalIdFactory);
        final CodeLocation codeLocation = clangPackageDetailsTransformer.toCodeLocation(Forge.CENTOS, Arrays.asList(Forge.CENTOS, Forge.FEDORA, Forge.REDHAT), null, packages);

        final Set<Dependency> dependencies = codeLocation.getDependencyGraph().getRootDependencies();
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
