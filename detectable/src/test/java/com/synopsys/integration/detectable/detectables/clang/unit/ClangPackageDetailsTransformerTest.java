package com.synopsys.integration.detectable.detectables.clang.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.ClangPackageDetailsTransformer;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.PackageDetails;

public class ClangPackageDetailsTransformerTest {

    @Test
    public void testDpkg() {
        doTest(Forge.UBUNTU);
    }

    @Test
    public void testRpm() {
        doTest(Forge.CENTOS);
    }

    private void doTest(Forge forge) {
        ExternalIdFactory externalIdFactory = Mockito.mock(ExternalIdFactory.class);
        ClangPackageDetailsTransformer transformer = new ClangPackageDetailsTransformer(externalIdFactory);

        Forge codeLocationForge = null;
        List<Forge> dependencyForges = new ArrayList<>();
        dependencyForges.add(forge);

        File rootDir = null;
        Set<PackageDetails> packages = new HashSet<>();

        final String packageName = "testPkgName";
        final String packageVersion = "1:testPkgVersion";
        final String packageArch = "testArch";
        PackageDetails pkg = new PackageDetails(packageName, packageVersion, packageArch);
        packages.add(pkg);

        ExternalId externalId = new ExternalId(forge);
        externalId.setName(packageName);
        externalId.setVersion(packageVersion);
        externalId.setArchitecture(packageArch);

        // The real test is: Does this get called: (if not, test will fail)
        Mockito.when(externalIdFactory.createArchitectureExternalId(forge, packageName, packageVersion, packageArch)).thenReturn(externalId);
        CodeLocation codeLocation = transformer.toCodeLocation(dependencyForges, packages);

        assertEquals(1, codeLocation.getDependencyGraph().getRootDependencies().size());
        Dependency generatedDependency = codeLocation.getDependencyGraph().getRootDependencies().iterator().next();
        assertEquals(packageName, generatedDependency.getName());
        assertEquals(packageVersion, generatedDependency.getVersion());
        assertEquals(forge, generatedDependency.getExternalId().getForge());
        String expectedExternalId = String.format("%s/%s/%s", packageName, packageVersion, packageArch);
        assertEquals(expectedExternalId, generatedDependency.getExternalId().createExternalId());
    }

}
