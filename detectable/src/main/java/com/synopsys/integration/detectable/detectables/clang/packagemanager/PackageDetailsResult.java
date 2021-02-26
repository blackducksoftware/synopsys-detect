/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.clang.packagemanager;

import java.io.File;
import java.util.Set;

public class PackageDetailsResult {
    private final Set<PackageDetails> foundPackages;
    private final Set<File> unRecognizedDependencyFiles;

    public PackageDetailsResult(final Set<PackageDetails> foundPackages, final Set<File> unRecognizedDependencyFiles) {
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
