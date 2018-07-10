/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.bomtool.clang;

import java.util.List;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

public class PackageManagerFinder {
    private final ExecutableRunner executableRunner;
    private final List<LinuxPackageManager> pkgMgrs;

    private LinuxPackageManager foundPkgMgr = null;
    private boolean hasLookedForPkgMgr = false;

    public PackageManagerFinder(final ExecutableRunner executableRunner,
            final List<LinuxPackageManager> pkgMgrs) {
        this.executableRunner = executableRunner;
        this.pkgMgrs = pkgMgrs;
    }

    public LinuxPackageManager findPkgMgr(final BomToolEnvironment environment) throws BomToolException {
        try {
            if (!hasLookedForPkgMgr) {
                foundPkgMgr = findPkgMgr();
                hasLookedForPkgMgr = true;
            }
            return foundPkgMgr;
        } catch (final Exception e) {
            throw new BomToolException(e);
        }
    }

    private LinuxPackageManager findPkgMgr() throws IntegrationException {
        LinuxPackageManager pkgMgr = null;
        for (final LinuxPackageManager pkgMgrCandidate : pkgMgrs) {
            if (pkgMgrCandidate.applies(executableRunner)) {
                pkgMgr = pkgMgrCandidate;
                break;
            }
        }
        if (pkgMgr == null) {
            throw new IntegrationException("Unable to execute any supported package manager; Please make sure that one of the supported package managers is on the PATH");
        }
        return pkgMgr;
    }
}
