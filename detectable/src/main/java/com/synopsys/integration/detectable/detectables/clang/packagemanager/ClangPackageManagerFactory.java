package com.synopsys.integration.detectable.detectables.clang.packagemanager;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.ApkArchitectureResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.ApkPackageManagerResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.DpkgPackageManagerResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.DpkgVersionResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.RpmPackageManagerResolver;

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