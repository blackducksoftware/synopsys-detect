/**
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
