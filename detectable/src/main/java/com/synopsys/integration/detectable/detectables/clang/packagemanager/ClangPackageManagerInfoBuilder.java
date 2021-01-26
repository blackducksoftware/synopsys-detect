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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.synopsys.integration.bdio.model.Forge;

public class ClangPackageManagerInfoBuilder {
    private String pkgMgrName;
    private String pkgMgrCmdString;
    private List<Forge> forges;
    private Forge defaultForge;
    private List<String> checkPresenceCommandArgs;
    private String checkPresenceCommandOutputExpectedText;
    private List<String> pkgMgrGetOwnerCmdArgs;
    private List<String> architectureArguments;
    private List<String> pkgInfoArgs;

    public ClangPackageManagerInfoBuilder setName(final String name) {
        this.pkgMgrName = name;
        return this;
    }

    public ClangPackageManagerInfoBuilder setCmd(final String pkgMgrCmdString) {
        this.pkgMgrCmdString = pkgMgrCmdString;
        return this;
    }

    public ClangPackageManagerInfoBuilder setForge(final Forge defaultForge, final Forge... additionalForges) {
        final List<Forge> newForges = new ArrayList<>(Arrays.asList(additionalForges));
        newForges.add(defaultForge);
        return setDefaultForge(defaultForge).setForges(newForges);
    }

    public ClangPackageManagerInfoBuilder setForges(final List<Forge> forges) {
        this.forges = forges;
        return this;
    }

    public ClangPackageManagerInfoBuilder setDefaultForge(final Forge defaultForge) {
        this.defaultForge = defaultForge;
        return this;
    }

    public ClangPackageManagerInfoBuilder setPresenceCheckArguments(final List<String> checkPresenceCommandArgs) {
        this.checkPresenceCommandArgs = checkPresenceCommandArgs;
        return this;
    }

    public ClangPackageManagerInfoBuilder setPresenceCheckArguments(final String... checkPresenceCommandArgs) {
        return setPresenceCheckArguments(Arrays.asList(checkPresenceCommandArgs));
    }

    public ClangPackageManagerInfoBuilder setPresenceCheckExpectedText(final String checkPresenceCommandOutputExpectedText) {
        this.checkPresenceCommandOutputExpectedText = checkPresenceCommandOutputExpectedText;
        return this;
    }

    public ClangPackageManagerInfoBuilder setGetOwnerArguments(final List<String> pkgMgrGetOwnerCmdArgs) {
        this.pkgMgrGetOwnerCmdArgs = pkgMgrGetOwnerCmdArgs;
        return this;
    }

    public ClangPackageManagerInfoBuilder setGetOwnerArguments(final String... pkgMgrGetOwnerCmdArgs) {
        return setGetOwnerArguments(Arrays.asList(pkgMgrGetOwnerCmdArgs));
    }

    public ClangPackageManagerInfoBuilder setArchitectureArguments(final List<String> architectureArguments) {
        this.architectureArguments = architectureArguments;
        return this;
    }

    public ClangPackageManagerInfoBuilder setArchitectureArguments(final String... architectureArguments) {
        return setArchitectureArguments(Arrays.asList(architectureArguments));
    }

    public ClangPackageManagerInfoBuilder setPackageInfoArguments(final List<String> pkgInfoArgs) {
        this.pkgInfoArgs = pkgInfoArgs;
        return this;
    }

    public ClangPackageManagerInfoBuilder setPackageInfoArguments(final String... pkgInfoArgs) {
        return setPackageInfoArguments(Arrays.asList(pkgInfoArgs));
    }

    public ClangPackageManagerInfo build() {
        return new ClangPackageManagerInfo(pkgMgrName, pkgMgrCmdString, forges, defaultForge, checkPresenceCommandArgs, checkPresenceCommandOutputExpectedText, pkgMgrGetOwnerCmdArgs, architectureArguments, pkgInfoArgs);
    }
}
