/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.clang.packagemanager;

import java.util.List;
import java.util.Optional;

import com.synopsys.integration.bdio.model.Forge;

public class ClangPackageManagerInfo {
    private String pkgMgrName;
    private String pkgMgrCmdString;
    private List<Forge> forges;
    private Forge defaultForge;
    private List<String> checkPresenceCommandArgs;
    private String checkPresenceCommandOutputExpectedText;
    private List<String> pkgMgrGetOwnerCmdArgs;
    private List<String> pkgArchitectureArgs;
    private List<String> pkgInfoArgs;

    public ClangPackageManagerInfo(final String pkgMgrName, final String pkgMgrCmdString, final List<Forge> forges, final Forge defaultForge, final List<String> checkPresenceCommandArgs,
        final String checkPresenceCommandOutputExpectedText, final List<String> pkgMgrGetOwnerCmdArgs, final List<String> pkgArchitectureArgs, final List<String> pkgInfoArgs) {
        this.pkgMgrName = pkgMgrName;
        this.pkgMgrCmdString = pkgMgrCmdString;
        this.forges = forges;
        this.defaultForge = defaultForge;
        this.checkPresenceCommandArgs = checkPresenceCommandArgs;
        this.checkPresenceCommandOutputExpectedText = checkPresenceCommandOutputExpectedText;
        this.pkgMgrGetOwnerCmdArgs = pkgMgrGetOwnerCmdArgs;
        this.pkgArchitectureArgs = pkgArchitectureArgs;
        this.pkgInfoArgs = pkgInfoArgs;
    }

    public String getPkgMgrName() {
        return pkgMgrName;
    }

    public String getPkgMgrCmdString() {
        return pkgMgrCmdString;
    }

    public List<Forge> getForges() {
        return forges;
    }

    public Forge getDefaultForge() {
        return defaultForge;
    }

    public List<String> getCheckPresenceCommandArgs() {
        return checkPresenceCommandArgs;
    }

    public String getCheckPresenceCommandOutputExpectedText() {
        return checkPresenceCommandOutputExpectedText;
    }

    public List<String> getPkgMgrGetOwnerCmdArgs() {
        return pkgMgrGetOwnerCmdArgs;
    }

    public Optional<List<String>> getPkgArchitectureArgs() {
        return Optional.ofNullable(pkgArchitectureArgs);
    }

    public Optional<List<String>> getPkgInfoArgs() {
        return Optional.ofNullable(pkgInfoArgs);
    }
}
