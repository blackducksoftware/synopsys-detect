package com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfo;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.PackageDetails;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class ApkPackageManagerResolver implements ClangPackageManagerResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ApkArchitectureResolver architectureResolver;

    public ApkPackageManagerResolver(ApkArchitectureResolver architectureResolver) {
        this.architectureResolver = architectureResolver;
    }

    @Override
    public List<PackageDetails> resolvePackages(
        ClangPackageManagerInfo currentPackageManager,
        DetectableExecutableRunner executableRunner,
        File workingDirectory,
        String queryPackageOutput
    ) throws ExecutableRunnerException, NotOwnedByAnyPkgException {
        isValid(queryPackageOutput);
        Optional<String> architecture = architectureResolver.resolveArchitecture(currentPackageManager, workingDirectory, executableRunner);
        List<PackageDetails> packageDetailsList = new ArrayList<>();
        String[] packageLines = queryPackageOutput.split("\n");
        for (String packageLine : packageLines) {
            Optional<List<String>> pkgNameVersionParts = parseIsOwnedByOutputLine(packageLine);
            if (pkgNameVersionParts.isPresent()) {
                String version = deriveVersion(pkgNameVersionParts.get());
                logger.trace(String.format("version: %s", version));
                Optional<String> component = deriveComponent(pkgNameVersionParts.get());
                logger.trace(String.format("component: %s", component));
                if (component.isPresent()) {
                    String externalId = String.format("%s/%s/%s", component, version, architecture.get());
                    logger.debug(String.format("Constructed externalId: %s", externalId));
                    PackageDetails dependencyDetails = new PackageDetails(component.get(), version, architecture.get());
                    packageDetailsList.add(dependencyDetails);
                }
            }
        }
        return packageDetailsList;
    }

    private void isValid(String queryPackageOutput) throws NotOwnedByAnyPkgException {
        if (queryPackageOutput.contains("ERROR") && queryPackageOutput.contains("Could not find owner package")) {
            throw new NotOwnedByAnyPkgException(queryPackageOutput);
        }
    }

    private String deriveVersion(List<String> pkgParts) {
        return String.format("%s-%s", pkgParts.get(pkgParts.size() - 2), pkgParts.get(pkgParts.size() - 1));
    }

    private Optional<String> deriveComponent(List<String> componentVersionParts) {
        // if a package starts with a period, we should ignore it because it is a virtual meta package and the version information is missing
        if (componentVersionParts == null || componentVersionParts.isEmpty() || componentVersionParts.get(0).startsWith(".")) {
            return Optional.empty();
        }
        StringBuilder component = new StringBuilder(componentVersionParts.get(0));
        for (int i = 1; i < componentVersionParts.size() - 2; i++) {
            component.append("-");
            component.append(componentVersionParts.get(i));
        }
        return Optional.of(component.toString());
    }

    // parse output of "apk info --who-owns pkg" --> package name+version details
    private Optional<List<String>> parseIsOwnedByOutputLine(String packageLine) {
        // expecting a line like: /usr/include/stdlib.h is owned by musl-dev-1.1.18-r3
        if (!packageLine.contains(" is owned by ")) {
            return Optional.empty();
        }
        String[] packageLineParts = packageLine.split("\\s+");
        if (packageLineParts.length < 5) {
            return Optional.empty();
        }
        String packageNameVersion = packageLineParts[4];
        logger.trace(String.format("packageNameAndVersion: %s", packageNameVersion));
        String[] packageNameVersionParts = packageNameVersion.split("-");
        if (packageNameVersionParts.length < 3) {
            logger.error(String.format("apk info output contains an invalid package: %s", packageNameVersion));
            return Optional.empty();
        }
        return Optional.of(Arrays.asList(packageNameVersionParts));
    }
}
