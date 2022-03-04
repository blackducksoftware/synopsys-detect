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

    public ClangPackageManagerInfoBuilder setName(String name) {
        this.pkgMgrName = name;
        return this;
    }

    public ClangPackageManagerInfoBuilder setCmd(String pkgMgrCmdString) {
        this.pkgMgrCmdString = pkgMgrCmdString;
        return this;
    }

    public ClangPackageManagerInfoBuilder setForge(Forge defaultForge, Forge... additionalForges) {
        List<Forge> newForges = new ArrayList<>(Arrays.asList(additionalForges));
        newForges.add(defaultForge);
        return setDefaultForge(defaultForge).setForges(newForges);
    }

    public ClangPackageManagerInfoBuilder setForges(List<Forge> forges) {
        this.forges = forges;
        return this;
    }

    public ClangPackageManagerInfoBuilder setDefaultForge(Forge defaultForge) {
        this.defaultForge = defaultForge;
        return this;
    }

    public ClangPackageManagerInfoBuilder setPresenceCheckArguments(List<String> checkPresenceCommandArgs) {
        this.checkPresenceCommandArgs = checkPresenceCommandArgs;
        return this;
    }

    public ClangPackageManagerInfoBuilder setPresenceCheckArguments(String... checkPresenceCommandArgs) {
        return setPresenceCheckArguments(Arrays.asList(checkPresenceCommandArgs));
    }

    public ClangPackageManagerInfoBuilder setPresenceCheckExpectedText(String checkPresenceCommandOutputExpectedText) {
        this.checkPresenceCommandOutputExpectedText = checkPresenceCommandOutputExpectedText;
        return this;
    }

    public ClangPackageManagerInfoBuilder setGetOwnerArguments(List<String> pkgMgrGetOwnerCmdArgs) {
        this.pkgMgrGetOwnerCmdArgs = pkgMgrGetOwnerCmdArgs;
        return this;
    }

    public ClangPackageManagerInfoBuilder setGetOwnerArguments(String... pkgMgrGetOwnerCmdArgs) {
        return setGetOwnerArguments(Arrays.asList(pkgMgrGetOwnerCmdArgs));
    }

    public ClangPackageManagerInfoBuilder setArchitectureArguments(List<String> architectureArguments) {
        this.architectureArguments = architectureArguments;
        return this;
    }

    public ClangPackageManagerInfoBuilder setArchitectureArguments(String... architectureArguments) {
        return setArchitectureArguments(Arrays.asList(architectureArguments));
    }

    public ClangPackageManagerInfoBuilder setPackageInfoArguments(List<String> pkgInfoArgs) {
        this.pkgInfoArgs = pkgInfoArgs;
        return this;
    }

    public ClangPackageManagerInfoBuilder setPackageInfoArguments(String... pkgInfoArgs) {
        return setPackageInfoArguments(Arrays.asList(pkgInfoArgs));
    }

    public ClangPackageManagerInfo build() {
        return new ClangPackageManagerInfo(
            pkgMgrName,
            pkgMgrCmdString,
            forges,
            defaultForge,
            checkPresenceCommandArgs,
            checkPresenceCommandOutputExpectedText,
            pkgMgrGetOwnerCmdArgs,
            architectureArguments,
            pkgInfoArgs
        );
    }
}
