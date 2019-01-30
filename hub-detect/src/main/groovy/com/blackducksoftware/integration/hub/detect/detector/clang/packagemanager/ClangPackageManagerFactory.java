package com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.dependencyfinder.ApkPackageManagerResolver;
import com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.dependencyfinder.DpkgPackageManagerResolver;
import com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.dependencyfinder.RpmPackageManagerResolver;
import com.synopsys.integration.bdio.model.Forge;

public class ClangPackageManagerFactory {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ClangPackageManagerFactory() {
        ClangPackageManagerBuilder rpm = new ClangPackageManagerBuilder();
        rpm.setName("rpm");
        rpm.setCmd("rpm");
        rpm.setForge(Forge.CENTOS, Forge.FEDORA, Forge.REDHAT);
        rpm.setPresenceCheckArguments("--version");
        rpm.setPresenceCheckExpectedText("RPM version");
        rpm.setGetOwnerArguments("-qf");
        rpm.setPackageOutputParser(new RpmPackageManagerResolver());


        ClangPackageManagerBuilder dpkg = new ClangPackageManagerBuilder();
        dpkg.setName("dpkg");
        dpkg.setCmd("dpkg");
        dpkg.setForge(Forge.UBUNTU, Forge.DEBIAN);
        dpkg.setPresenceCheckArguments("--version");
        dpkg.setPresenceCheckExpectedText("package management program version");
        dpkg.setGetOwnerArguments("-S");
        dpkg.setPackageOutputParser(new DpkgPackageManagerResolver());

        ClangPackageManagerBuilder apk = new ClangPackageManagerBuilder();
        apk.setName("apk");
        apk.setCmd("apk");
        apk.setForge(Forge.ALPINE);
        apk.setPresenceCheckArguments("--version");
        apk.setPresenceCheckExpectedText("apk-tools ");
        apk.setGetOwnerArguments("info", "--who-owns");
        apk.setPackageOutputParser(new ApkPackageManagerResolver());
        apk.setArchitectureArguments("info", "--print-arch");


    }
}