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

        final Set<File> fileDetailsSet = dependencyFileDetailGenerator.fromCompileCommands(Arrays.asList(new CompileCommand()), null, true);
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
        final CodeLocation codeLocation = clangPackageDetailsTransformer.toCodeLocation(Arrays.asList(Forge.CENTOS, Forge.FEDORA, Forge.REDHAT), packages);

        final Set<Dependency> dependencies = codeLocation.getDependencyGraph().getRootDependencies();
        assertEquals(6, dependencies.size());
        for (final Dependency dependency : dependencies) {
            System.out.printf("Checking dependency: %s:%s / %s\n", dependency.getName(), dependency.getVersion(), dependency.getExternalId().getForge().getName());
            final char indexChar = dependency.getName().charAt(15);
            assertTrue(indexChar == '1' || indexChar == '2' || indexChar == '3');

            final String forge = dependency.getExternalId().getForge().getName();
            assertTrue("centos".equals(forge) || "fedora".equals(forge) || "redhat".equals(forge));

            assertEquals(String.format("testPackageName%c", indexChar), dependency.getName());
            assertEquals(String.format("testPackageVersion%c", indexChar), dependency.getVersion());
            assertEquals(String.format("testPackageArch%c", indexChar), dependency.getExternalId().getArchitecture());

            assertEquals(forge, dependency.getExternalId().getForge().getName());
            assertEquals(null, dependency.getExternalId().getGroup());
            assertEquals(String.format("testPackageName%c", indexChar), dependency.getExternalId().getName());
            assertEquals(null, dependency.getExternalId().getPath());
            assertEquals(String.format("testPackageVersion%c", indexChar), dependency.getExternalId().getVersion());
        }
    }

}
