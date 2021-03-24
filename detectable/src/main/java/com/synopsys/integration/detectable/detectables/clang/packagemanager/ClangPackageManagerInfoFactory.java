/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.clang.packagemanager;

import com.synopsys.integration.bdio.model.Forge;

public class ClangPackageManagerInfoFactory {

    private static final String VERSION_FLAG = "--version";

    public static ClangPackageManagerInfoFactory standardFactory() {
        return new ClangPackageManagerInfoFactory();
    }

    public ClangPackageManagerInfo rpm() {
        final ClangPackageManagerInfoBuilder rpm = new ClangPackageManagerInfoBuilder();
        rpm.setName("rpm");
        rpm.setCmd("rpm");
        rpm.setForge(Forge.CENTOS, Forge.FEDORA, Forge.REDHAT);
        rpm.setPresenceCheckArguments(VERSION_FLAG);
        rpm.setPresenceCheckExpectedText("RPM version");
        rpm.setGetOwnerArguments("-qf", "--queryformat=\\{ epoch: \\\"%{E}\\\", name: \\\"%{N}\\\", version: \\\"%{V}-%{R}\\\", arch: \\\"%{ARCH}\\\" \\}");
        return rpm.build();
    }

    public ClangPackageManagerInfo dpkg() {
        final ClangPackageManagerInfoBuilder dpkg = new ClangPackageManagerInfoBuilder();
        dpkg.setName("dpkg");
        dpkg.setCmd("dpkg");
        dpkg.setForge(Forge.UBUNTU, Forge.DEBIAN);
        dpkg.setPresenceCheckArguments(VERSION_FLAG);
        dpkg.setPresenceCheckExpectedText("package management program version");
        dpkg.setGetOwnerArguments("-S");
        dpkg.setPackageInfoArguments("-s");
        return dpkg.build();
    }

    public ClangPackageManagerInfo apk() {
        final ClangPackageManagerInfoBuilder apk = new ClangPackageManagerInfoBuilder();
        apk.setName("apk");
        apk.setCmd("apk");
        apk.setForge(Forge.ALPINE);
        apk.setPresenceCheckArguments(VERSION_FLAG);
        apk.setPresenceCheckExpectedText("apk-tools ");
        apk.setGetOwnerArguments("info", "--who-owns");
        apk.setArchitectureArguments("info", "--print-arch");
        return apk.build();
    }
}