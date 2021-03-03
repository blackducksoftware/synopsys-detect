/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.clang.packagemanager;

import java.util.List;
import java.util.Optional;

import com.synopsys.integration.bdio.model.Forge;

public class ClangPackageManagerInfo {
    private final String pkgMgrName;
    private final String pkgMgrCmdString;
    private final List<Forge> forges;
    private final Forge defaultForge;
    private final List<String> checkPresenceCommandArgs;
    private final String checkPresenceCommandOutputExpectedText;
    private final List<String> pkgMgrGetOwnerCmdArgs;
    private final List<String> pkgArchitectureArgs;
    private final List<String> pkgInfoArgs;

    public ClangPackageManagerInfo(String pkgMgrName, String pkgMgrCmdString, List<Forge> forges, Forge defaultForge, List<String> checkPresenceCommandArgs,
        String checkPresenceCommandOutputExpectedText, List<String> pkgMgrGetOwnerCmdArgs, List<String> pkgArchitectureArgs, List<String> pkgInfoArgs) {
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
