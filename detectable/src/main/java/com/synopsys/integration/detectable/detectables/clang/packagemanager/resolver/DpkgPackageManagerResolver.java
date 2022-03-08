package com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
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
    public List<PackageDetails> resolvePackages(
        ClangPackageManagerInfo currentPackageManager,
        DetectableExecutableRunner executableRunner,
        File workingDirectory,
        String ownershipQueryOutput
    )
        throws NotOwnedByAnyPkgException {
        List<PackageDetails> packageDetailsList = new ArrayList<>();
        String[] packageLines = ownershipQueryOutput.split("\n");
        for (String packageLine : packageLines) {
            if (!valid(packageLine)) {
                logger.trace("Skipping file ownership query output line: {}", packageLine);
                continue;
            }

            NameArchitecture packageNameArchitecture = parsePackageNameArchitecture(packageLine);
            logger.debug(
                "File ownership query results: package name: {}, arch: {}",
                packageNameArchitecture.getName(),
                packageNameArchitecture.getArchitecture().orElse("<absent>")
            );
            Optional<PackageDetails> pkg = versionResolver.resolvePackageDetails(currentPackageManager, executableRunner, workingDirectory, packageNameArchitecture);
            if (pkg.isPresent()) {
                logger.debug("Adding package: {}", pkg.get());
                packageDetailsList.add(pkg.get());
            }
        }
        return packageDetailsList;
    }

    @Nullable
    private NameArchitecture parsePackageNameArchitecture(String packageLine) {
        String[] queryPackageOutputParts = packageLine.split("\\s+");
        String[] packageNameArchParts = queryPackageOutputParts[0].split(":");
        String packageName = packageNameArchParts[0].trim();
        if (packageNameArchParts.length > 1) {
            String architectureToken = packageNameArchParts[1].trim();
            String packageArchitecture = StringUtils.substringBefore(architectureToken, ",");
            return new NameArchitecture(packageName, packageArchitecture);
        } else {
            return new NameArchitecture(packageName, null);
        }
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
        return packageLine.matches(".+: .+");
    }
}
