package com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.dependencyfinder.ClangPackageManagerResolver;
import com.synopsys.integration.bdio.model.Forge;

public class ClangPackageManagerBuilder {
    private String pkgMgrName;
    private String pkgMgrCmdString;
    private List<Forge> forges;
    private Forge defaultForge;
    private List<String> checkPresenceCommandArgs;
    private String checkPresenceCommandOutputExpectedText;
    private List<String> pkgMgrGetOwnerCmdArgs;
    private ClangPackageManagerResolver packageOutputParser;
    private List<String> architectureArguments;

    public ClangPackageManagerBuilder setName(String name) {
        this.pkgMgrName = name;
        return this;
    }

    public ClangPackageManagerBuilder setCmd(String name) {
        this.pkgMgrCmdString = pkgMgrCmdString;
        return this;
    }

    public ClangPackageManagerBuilder setForge(Forge defaultForge, Forge... additionalForges) {
        List<Forge> forges = new ArrayList<>(Arrays.asList(additionalForges));
        forges.add(defaultForge);
        return setDefaultForge(defaultForge).setForges(forges);
    }


    public ClangPackageManagerBuilder setForges(List<Forge> forges) {
        this.forges = forges;
        return this;
    }

    public ClangPackageManagerBuilder setDefaultForge(Forge defaultForge) {
        this.defaultForge = defaultForge;
        return this;
    }


    public ClangPackageManagerBuilder setPresenceCheckArguments(List<String> checkPresenceCommandArgs) {
        this.checkPresenceCommandArgs = checkPresenceCommandArgs;
        return this;
    }

    public ClangPackageManagerBuilder setPresenceCheckArguments(String... checkPresenceCommandArgs) {
        return setPresenceCheckArguments(Arrays.asList(checkPresenceCommandArgs));
    }

    public ClangPackageManagerBuilder setPresenceCheckExpectedText(String checkPresenceCommandOutputExpectedText) {
        this.checkPresenceCommandOutputExpectedText = checkPresenceCommandOutputExpectedText;
        return this;
    }

    public ClangPackageManagerBuilder setGetOwnerArguments(List<String> pkgMgrGetOwnerCmdArgs) {
        this.pkgMgrGetOwnerCmdArgs = pkgMgrGetOwnerCmdArgs;
        return this;
    }

    public ClangPackageManagerBuilder setGetOwnerArguments(String... pkgMgrGetOwnerCmdArgs) {
        return setGetOwnerArguments(Arrays.asList(pkgMgrGetOwnerCmdArgs));
    }

    public ClangPackageManagerBuilder setPackageOutputParser(ClangPackageManagerResolver packageOutputParser) {
        this.packageOutputParser = packageOutputParser;
        return this;
    }

    public ClangPackageManagerBuilder setArchitectureArguments(List<String> architectureArguments) {
        this.architectureArguments = architectureArguments;
        return this;
    }

    public ClangPackageManagerBuilder setArchitectureArguments(String... architectureArguments) {
        return setArchitectureArguments(Arrays.asList(architectureArguments));
    }

    public ClangPackageManagerInfo build() {
        return new ClangPackageManagerInfo(pkgMgrName, pkgMgrCmdString, forges, defaultForge, checkPresenceCommandArgs, checkPresenceCommandOutputExpectedText, pkgMgrGetOwnerCmdArgs, architectureArguments, pkgInfoArgs, packageOutputParser);
    }
}
