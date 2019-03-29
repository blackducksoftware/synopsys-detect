/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.Forge;

public class ClangPackageManagerInfoFactory {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static ClangPackageManagerInfoFactory standardFactory() {
        return new ClangPackageManagerInfoFactory();
    }

    public List<ClangPackageManagerInfo> createPackageManagers() {
        List<ClangPackageManagerInfo> packageManagers = new ArrayList<>();

        packageManagers.add(rpm());
        packageManagers.add(apk());
        packageManagers.add(dpkg());

        return packageManagers;
    }

    public ClangPackageManagerInfo rpm(){
        ClangPackageManagerInfoBuilder rpm = new ClangPackageManagerInfoBuilder();
        rpm.setName("rpm");
        rpm.setCmd("rpm");
        rpm.setForge(Forge.CENTOS, Forge.FEDORA, Forge.REDHAT);
        rpm.setPresenceCheckArguments("--version");
        rpm.setPresenceCheckExpectedText("RPM version");
        rpm.setGetOwnerArguments("-qf");
        return rpm.build();
    }

    public ClangPackageManagerInfo dpkg(){
        ClangPackageManagerInfoBuilder dpkg = new ClangPackageManagerInfoBuilder();
        dpkg.setName("dpkg");
        dpkg.setCmd("dpkg");
        dpkg.setForge(Forge.UBUNTU, Forge.DEBIAN);
        dpkg.setPresenceCheckArguments("--version");
        dpkg.setPresenceCheckExpectedText("package management program version");
        dpkg.setGetOwnerArguments("-S");
        dpkg.setPackageInfoArguments("-s");
        return dpkg.build();
    }

    public ClangPackageManagerInfo apk(){
        ClangPackageManagerInfoBuilder apk = new ClangPackageManagerInfoBuilder();
        apk.setName("apk");
        apk.setCmd("apk");
        apk.setForge(Forge.ALPINE);
        apk.setPresenceCheckArguments("--version");
        apk.setPresenceCheckExpectedText("apk-tools ");
        apk.setGetOwnerArguments("info", "--who-owns");
        apk.setArchitectureArguments("info", "--print-arch");
        return apk.build();
    }
}