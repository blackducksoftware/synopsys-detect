/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfo;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.PackageDetails;

public class DpkgPackageManagerResolver implements ClangPackageManagerResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DpkgPkgDetailsResolver versionResolver;

    public DpkgPackageManagerResolver(DpkgPkgDetailsResolver versionResolver) {
        this.versionResolver = versionResolver;
    }

    @Override
    public List<PackageDetails> resolvePackages(ClangPackageManagerInfo currentPackageManager, DetectableExecutableRunner executableRunner, File workingDirectory, String ownershipQueryOutput)
        throws NotOwnedByAnyPkgException {
        List<PackageDetails> packageDetailsList = new ArrayList<>();
        String[] packageLines = ownershipQueryOutput.split("\n");
        for (String packageLine : packageLines) {
            if (!valid(packageLine)) {
                logger.trace(String.format("Skipping file ownership query output line: %s", packageLine));
                continue;
            }
            String[] queryPackageOutputParts = packageLine.split("\\s+");
            String[] packageNameArchParts = queryPackageOutputParts[0].split(":");
            String packageName = packageNameArchParts[0].trim();
            logger.debug(String.format("File ownership query results: package name: %s", packageName));
            Optional<PackageDetails> pkg = versionResolver.resolvePackageDetails(currentPackageManager, executableRunner, workingDirectory, packageName);
            if (pkg.isPresent()) {
                logger.debug(String.format("Adding package: %s", pkg.get()));
                packageDetailsList.add(pkg.get());
            }
        }
        return packageDetailsList;
    }

    private boolean valid(String packageLine) throws NotOwnedByAnyPkgException {
        if (packageLine.contains("no path found matching pattern")) {
            throw new NotOwnedByAnyPkgException(packageLine);
        }
        // arch included
        if (packageLine.matches(".+:.+: .+")) {
            return true;
        }
        // arch not included
        if (packageLine.matches(".+: .+")) {
            return true;
        }
        return false;
    }
}
