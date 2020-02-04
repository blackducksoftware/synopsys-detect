/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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

    private void doTest(final Forge forge) {
        final ExternalIdFactory externalIdFactory = Mockito.mock(ExternalIdFactory.class);
        final ClangPackageDetailsTransformer transformer = new ClangPackageDetailsTransformer(externalIdFactory);

        final Forge codeLocationForge = null;
        final List<Forge> dependencyForges = new ArrayList<>();
        dependencyForges.add(forge);

        final File rootDir = null;
        final Set<PackageDetails> packages = new HashSet<>();

        final String packageName = "testPkgName";
        final String packageVersion = "1:testPkgVersion";
        final String packageArch = "testArch";
        final PackageDetails pkg = new PackageDetails(packageName, packageVersion, packageArch);
        packages.add(pkg);

        final ExternalId externalId = new ExternalId(forge);
        externalId.setName(packageName);
        externalId.setVersion(packageVersion);
        externalId.setArchitecture(packageArch);

        // The real test is: Does this get called: (if not, test will fail)
        Mockito.when(externalIdFactory.createArchitectureExternalId(forge, packageName, packageVersion, packageArch)).thenReturn(externalId);
        final CodeLocation codeLocation = transformer.toCodeLocation(dependencyForges, packages);

        assertEquals(1, codeLocation.getDependencyGraph().getRootDependencies().size());
        final Dependency generatedDependency = codeLocation.getDependencyGraph().getRootDependencies().iterator().next();
        assertEquals(packageName, generatedDependency.getName());
        assertEquals(packageVersion, generatedDependency.getVersion());
        assertEquals(forge, generatedDependency.getExternalId().getForge());
        final String expectedExternalId = String.format("%s/%s/%s", packageName, packageVersion, packageArch);
        assertEquals(expectedExternalId, generatedDependency.getExternalId().createExternalId());
    }

}
