package com.synopsys.integration.detectable.detectables.clang.packagemanager;

import java.io.File;
import java.util.Set;

public class PackageDetailsResult {
    private final Set<PackageDetails> foundPackages;
    private final Set<File> unRecognizedDependencyFiles;

    public PackageDetailsResult(Set<PackageDetails> foundPackages, Set<File> unRecognizedDependencyFiles) {
        this.foundPackages = foundPackages;
        this.unRecognizedDependencyFiles = unRecognizedDependencyFiles;
    }

    public Set<File> getUnRecognizedDependencyFiles() {
        return unRecognizedDependencyFiles;
    }

    public Set<PackageDetails> getFoundPackages() {
        return foundPackages;
    }
}
