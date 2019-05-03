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
    public void testUbuntu() {

        final ExternalIdFactory externalIdFactory = Mockito.mock(ExternalIdFactory.class);
        final ClangPackageDetailsTransformer transformer = new ClangPackageDetailsTransformer(externalIdFactory);

        final Forge codeLocationForge = null;
        final List<Forge> dependencyForges = new ArrayList<>();
        final Forge forge = Forge.UBUNTU;
        dependencyForges.add(forge);

        final File rootDir = null;
        final Set<PackageDetails> packages = new HashSet<>();

        final String packageName = "testPkgName";
        final String packageVersion = "1:testPkgVersion";
        final String packageArch = "testArch";
        final PackageDetails pkg = new PackageDetails(packageName, packageVersion, packageArch);
        packages.add(pkg);

        final ExternalId externalId = new ExternalId(forge);
        externalId.name = packageName;
        externalId.version = packageVersion;
        externalId.architecture = packageArch;

        // The real test is: Does this get called: (if not, test will fail)
        Mockito.when(externalIdFactory.createArchitectureExternalId(forge, packageName, packageVersion, packageArch)).thenReturn(externalId);
        final CodeLocation codeLocation = transformer.toCodeLocation(codeLocationForge, dependencyForges, rootDir, packages);

        assertEquals(1, codeLocation.getDependencyGraph().getRootDependencies().size());
        final Dependency generatedDependency = codeLocation.getDependencyGraph().getRootDependencies().iterator().next();
        assertEquals(packageName, generatedDependency.name);
        assertEquals(packageVersion, generatedDependency.version);
        final String expectedExternalId = String.format("%s/%s/%s", packageName, packageVersion, packageArch);
        assertEquals(expectedExternalId, generatedDependency.externalId.createExternalId());
    }
}
