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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfo;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfoFactory;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.PackageDetails;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.RpmPackageManagerResolver;

public class RpmPackageManagerTest {

    @Test
    public void testValidNoEpoch() throws ExecutableRunnerException {
        final StringBuilder sb = new StringBuilder();
        sb.append("{ epoch: \"(none)\", name: \"boost-devel\", version: \"1.53.0-27.el7\", arch: \"x86_64\" }\n");
        final String pkgMgrOwnedByOutput = sb.toString();

        final RpmPackageManagerResolver pkgMgr = new RpmPackageManagerResolver(new Gson());
        final List<PackageDetails> pkgs = pkgMgr.resolvePackages(new ClangPackageManagerInfoFactory().rpm(), null, null, pkgMgrOwnedByOutput);

        assertEquals(1, pkgs.size());
        assertEquals("boost-devel", pkgs.get(0).getPackageName());
        assertEquals("1.53.0-27.el7", pkgs.get(0).getPackageVersion());
        assertEquals("x86_64", pkgs.get(0).getPackageArch());
    }

    @Test
    public void testValidWithEpoch() throws ExecutableRunnerException {
        final StringBuilder sb = new StringBuilder();
        sb.append("{ epoch: \"9\", name: \"boost-devel\", version: \"1.53.0-27.el7\", arch: \"x86_64\" }\n");
        final String pkgMgrOwnedByOutput = sb.toString();

        final RpmPackageManagerResolver pkgMgr = new RpmPackageManagerResolver(new Gson());
        final List<PackageDetails> pkgs = pkgMgr.resolvePackages(new ClangPackageManagerInfoFactory().rpm(), null, null, pkgMgrOwnedByOutput);

        assertEquals(1, pkgs.size());
        assertEquals("boost-devel", pkgs.get(0).getPackageName());
        assertEquals("9:1.53.0-27.el7", pkgs.get(0).getPackageVersion());
        assertEquals("x86_64", pkgs.get(0).getPackageArch());
    }

    @Test
    public void testInValid() throws ExecutableRunnerException {
        final StringBuilder sb = new StringBuilder();
        sb.append("garbage\n");
        sb.append("nonsense\n");
        sb.append("file /opt/hub-detect/clang-repos/hello_world/hello_world.cpp is not owned by any package\n");
        final String pkgMgrOwnedByOutput = sb.toString();

        final RpmPackageManagerResolver pkgMgr = new RpmPackageManagerResolver(new Gson());
        final List<PackageDetails> pkgs = pkgMgr.resolvePackages(new ClangPackageManagerInfoFactory().rpm(), null, null, pkgMgrOwnedByOutput);

        assertEquals(0, pkgs.size());
    }

    @Test
    public void testResolve() throws ExecutableRunnerException {

        final RpmPackageManagerResolver resolver = new RpmPackageManagerResolver(new Gson());
        final ClangPackageManagerInfo currentPackageManager = null;
        final ExecutableRunner executableRunner = null;
        final File workingDirectory = null;
        final String queryPackageOutput = "{ epoch: \"(none)\", name: \"glibc-headers\", version: \"2.17-222.el7\", arch: \"x86_64\" }\n" +
                                              "{ epoch: \"3\", name: \"test-package\", version: \"test-version\", arch: \"test_arch\" }\n";

        final List<PackageDetails> pkgs = resolver.resolvePackages(currentPackageManager, executableRunner, workingDirectory, queryPackageOutput);
        assertEquals(2, pkgs.size());
        boolean foundGLibcHeaders = false;
        boolean foundTestPkg = false;
        for (final PackageDetails pkg : pkgs) {
            if (pkg.getPackageName().equals("glibc-headers")) {
                foundGLibcHeaders = true;
                assertEquals("2.17-222.el7", pkg.getPackageVersion());
                assertEquals("x86_64", pkg.getPackageArch());
            }
            if (pkg.getPackageName().equals("test-package")) {
                foundTestPkg = true;
                assertEquals("3:test-version", pkg.getPackageVersion());
                assertEquals("test_arch", pkg.getPackageArch());
            }
        }
        assertTrue(foundGLibcHeaders);
        assertTrue(foundTestPkg);
    }
}
