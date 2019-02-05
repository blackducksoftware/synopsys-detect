package com.synopsys.integration.detectable.detectables.clang.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommand;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.ClangPackageDetailsTransformer;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.DependencyFileDetailGenerator;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.DependencyFileDetails;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.FilePathGenerator;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.PackageDetails;

public class DependencyFileDetailGeneratorTest {
    @Test
    public void testFileThatDoesNotExistIsSkipped() throws ExecutableRunnerException {
        final File mockFile = Mockito.mock(File.class);
        Mockito.when(mockFile.toString()).thenReturn("Example");

        final FilePathGenerator filePathGenerator = Mockito.mock(FilePathGenerator.class);
        Mockito.when(filePathGenerator.fromCompileCommand(mockFile, null, true)).thenReturn(Arrays.asList("does_not_exist.h"));

        final DependencyFileDetailGenerator dependencyFileDetailGenerator = new DependencyFileDetailGenerator(filePathGenerator);

        final Set<DependencyFileDetails> fileDetailsSet = dependencyFileDetailGenerator.fromCompileCommands(Arrays.asList(new CompileCommand()), null, null, true);
        Assert.assertEquals(0, fileDetailsSet.size());
    }

    @Test
    public void testDependencyCreatedWithEachForge() throws ExecutableRunnerException {
        final File mockFile = Mockito.mock(File.class);
        Mockito.when(mockFile.toString()).thenReturn("Example");

        final Set<PackageDetails> packages = new HashSet<>();
        packages.add(new PackageDetails("testPackageName1", "testPackageVersion1", "testPackageArch1"));
        packages.add(new PackageDetails("testPackageName2", "testPackageVersion2", "testPackageArch2"));

        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final ClangPackageDetailsTransformer clangPackageDetailsTransformer = new ClangPackageDetailsTransformer(externalIdFactory);
        final CodeLocation codeLocation = clangPackageDetailsTransformer.toCodeLocation(Forge.CENTOS, Arrays.asList(Forge.CENTOS, Forge.FEDORA, Forge.REDHAT), mockFile, packages);

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
