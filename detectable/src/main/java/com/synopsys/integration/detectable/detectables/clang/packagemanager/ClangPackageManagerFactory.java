package com.synopsys.integration.detectable.detectables.clang.packagemanager;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.ApkArchitectureResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.ApkPackageManagerResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.DpkgPackageManagerResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.DpkgPkgDetailsResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.RpmPackageManagerResolver;

public class ClangPackageManagerFactory {
    private final ClangPackageManagerInfoFactory packageManagerInfoFactory;

    public ClangPackageManagerFactory(ClangPackageManagerInfoFactory packageManagerInfoFactory) {
        this.packageManagerInfoFactory = packageManagerInfoFactory;
    }

    public static ClangPackageManagerFactory standardFactory() {
        return new ClangPackageManagerFactory(ClangPackageManagerInfoFactory.standardFactory());
    }

    public List<ClangPackageManager> createPackageManagers() {
        List<ClangPackageManager> packageManagers = new ArrayList<>();

        packageManagers.add(new ClangPackageManager(packageManagerInfoFactory.apk(), new ApkPackageManagerResolver(new ApkArchitectureResolver())));
        packageManagers.add(new ClangPackageManager(packageManagerInfoFactory.dpkg(), new DpkgPackageManagerResolver(new DpkgPkgDetailsResolver())));
        packageManagers.add(new ClangPackageManager(packageManagerInfoFactory.rpm(), new RpmPackageManagerResolver(new Gson())));

        return packageManagers;
    }

}