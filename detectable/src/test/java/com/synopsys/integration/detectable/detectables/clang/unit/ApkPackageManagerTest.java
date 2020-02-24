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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfo;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfoFactory;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.PackageDetails;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.ApkArchitectureResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.ApkPackageManagerResolver;

public class ApkPackageManagerTest {
    @Test
    public void canParsePackages() throws ExecutableRunnerException {
        final StringBuilder sb = new StringBuilder();
        sb.append("garbage\n");
        sb.append("nonsense\n");
        sb.append("this line has the is owned by substring\n");
        sb.append(" is owned by \n");
        sb.append("/usr/include/stdlib.h is owned by musl-dev-1.1.18-r3\n"); // This is the one valid line; rest should be discarded
        sb.append("/usr/include/stdlib.h is owned by .musl-dev-1.1.18-r99\n");
        final String pkgMgrOwnedByOutput = sb.toString();

        final ApkArchitectureResolver architectureResolver = Mockito.mock(ApkArchitectureResolver.class);
        Mockito.when(architectureResolver.resolveArchitecture(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Optional.of("x86_64"));

        final ApkPackageManagerResolver apkPackageManagerResolver = new ApkPackageManagerResolver(architectureResolver);

        final ClangPackageManagerInfo apk = new ClangPackageManagerInfoFactory().apk();
        final List<PackageDetails> pkgs = apkPackageManagerResolver.resolvePackages(apk, null, null, pkgMgrOwnedByOutput);

        Assertions.assertEquals(1, pkgs.size());
        Assertions.assertEquals("musl-dev", pkgs.get(0).getPackageName());
        Assertions.assertEquals("1.1.18-r3", pkgs.get(0).getPackageVersion());
        Assertions.assertEquals("x86_64", pkgs.get(0).getPackageArch());
    }

    @Test
    public void canParseArchitecture() throws ExecutableRunnerException {
        final String exampleOutput = "x86_64\n";

        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        Mockito.when(executableRunner.execute(null, "apk", Arrays.asList("info", "--print-arch"))).thenReturn(new ExecutableOutput("", 0, exampleOutput, ""));

        final ApkArchitectureResolver architectureResolver = new ApkArchitectureResolver();
        final Optional<String> architecture = architectureResolver.resolveArchitecture(new ClangPackageManagerInfoFactory().apk(), null, executableRunner);

        assertTrue(architecture.isPresent());
        Assertions.assertEquals("x86_64", architecture.get());
    }
}