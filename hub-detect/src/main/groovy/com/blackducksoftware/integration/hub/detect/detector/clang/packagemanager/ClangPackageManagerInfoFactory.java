package com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.dependencyfinder.ApkArchitectureResolver;
import com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.dependencyfinder.ApkPackageManagerResolver;
import com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.dependencyfinder.DpkgPackageManagerResolver;
import com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.dependencyfinder.DpkgVersionResolver;
import com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.dependencyfinder.RpmPackageManagerResolver;
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