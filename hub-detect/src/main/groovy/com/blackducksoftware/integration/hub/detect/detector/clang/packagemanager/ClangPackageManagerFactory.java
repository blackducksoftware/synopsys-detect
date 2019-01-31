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

public class ClangPackageManagerFactory {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ClangPackageManagerInfoFactory packageManagerInfoFactory;
    public ClangPackageManagerFactory(final ClangPackageManagerInfoFactory packageManagerInfoFactory){
        this.packageManagerInfoFactory = packageManagerInfoFactory;
    }

    public static ClangPackageManagerFactory standardFactory() {
        return new ClangPackageManagerFactory(ClangPackageManagerInfoFactory.standardFactory());
    }

    public List<ClangPackageManager> createPackageManagers() {
        List<ClangPackageManager> packageManagers = new ArrayList<>();

        packageManagers.add(new ClangPackageManager(packageManagerInfoFactory.apk(), new ApkPackageManagerResolver(new ApkArchitectureResolver())));
        packageManagers.add(new ClangPackageManager(packageManagerInfoFactory.dpkg(), new DpkgPackageManagerResolver(new DpkgVersionResolver())));
        packageManagers.add(new ClangPackageManager(packageManagerInfoFactory.rpm(), new RpmPackageManagerResolver()));

        return packageManagers;
    }


}