package com.synopsys.integration.detectable.detectables.clang.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommand;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.ClangPackageDetailsTransformer;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.DependencyFileDetailGenerator;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.FilePathGenerator;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.PackageDetails;

public class DependencyFileDetailGeneratorTest {
    @Test
    public void testFileThatDoesNotExistIsSkipped() {
        File mockFile = Mockito.mock(File.class);
        Mockito.when(mockFile.toString()).thenReturn("Example");

        FilePathGenerator filePathGenerator = Mockito.mock(FilePathGenerator.class);
        Mockito.when(filePathGenerator.fromCompileCommand(mockFile, null, true)).thenReturn(Collections.singletonList("does_not_exist.h"));

        DependencyFileDetailGenerator dependencyFileDetailGenerator = new DependencyFileDetailGenerator(filePathGenerator);

        Set<File> fileDetailsSet = dependencyFileDetailGenerator.fromCompileCommands(Collections.singletonList(new CompileCommand()), null, true);
        Assertions.assertEquals(0, fileDetailsSet.size());
    }

    @Test
    public void testDependencyCreatedWithEachForge() {
        File mockFile = Mockito.mock(File.class);
        Mockito.when(mockFile.toString()).thenReturn("Example");

        Set<PackageDetails> packages = new HashSet<>();
        packages.add(new PackageDetails("testPackageName1", "testPackageVersion1", "testPackageArch1"));
        packages.add(new PackageDetails("testPackageName2", "testPackageVersion2", "testPackageArch2"));

        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        ClangPackageDetailsTransformer clangPackageDetailsTransformer = new ClangPackageDetailsTransformer(externalIdFactory);
        CodeLocation codeLocation = clangPackageDetailsTransformer.toCodeLocation(Arrays.asList(Forge.CENTOS, Forge.FEDORA, Forge.REDHAT), packages);

        Set<Dependency> dependencies = codeLocation.getDependencyGraph().getRootDependencies();
        assertEquals(6, dependencies.size());
        for (Dependency dependency : dependencies) {
            System.out.printf("Checking dependency: %s:%s / %s\n", dependency.getName(), dependency.getVersion(), dependency.getExternalId().getForge().getName());
            char indexChar = dependency.getName().charAt(15);
            assertTrue(indexChar == '1' || indexChar == '2' || indexChar == '3');

            String forge = dependency.getExternalId().getForge().getName();
            assertTrue("centos".equals(forge) || "fedora".equals(forge) || "redhat".equals(forge));

            assertEquals(String.format("testPackageName%c", indexChar), dependency.getName());
            assertEquals(String.format("testPackageVersion%c", indexChar), dependency.getVersion());
            assertEquals(String.format("testPackageArch%c", indexChar), dependency.getExternalId().getArchitecture());

            assertEquals(forge, dependency.getExternalId().getForge().getName());
            assertEquals(String.format("testPackageName%c", indexChar), dependency.getExternalId().getName());
            assertEquals(String.format("testPackageVersion%c", indexChar), dependency.getExternalId().getVersion());
        }
    }

}
