package com.synopsys.integration.detectable.detectables.lerna;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.lerna.lockfile.LernaLockFileResult;
import com.synopsys.integration.detectable.detectables.lerna.model.LernaPackage;
import com.synopsys.integration.detectable.detectables.lerna.model.LernaResult;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockfilePackager;
import com.synopsys.integration.detectable.detectables.npm.lockfile.result.NpmPackagerResult;
import com.synopsys.integration.detectable.detectables.yarn.YarnPackager;
import com.synopsys.integration.detectable.detectables.yarn.YarnResult;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.NullSafePackageJson;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonReader;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLock;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockParser;
import com.synopsys.integration.detectable.detectables.yarn.workspace.YarnWorkspaces;
import com.synopsys.integration.util.ExcludedIncludedWildcardFilter;
import com.synopsys.integration.util.NameVersion;

public class LernaPackager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FileFinder fileFinder;
    private final PackageJsonReader packageJsonReader;
    private final YarnLockParser yarnLockParser;
    private final NpmLockfilePackager npmLockfilePackager;
    private final YarnPackager yarnPackager;
    private final EnumListFilter<LernaPackageType> lernaPackageTypeFilter;

    public LernaPackager(
        FileFinder fileFinder,
        PackageJsonReader packageJsonReader,
        YarnLockParser yarnLockParser,
        NpmLockfilePackager npmLockfilePackager,
        YarnPackager yarnPackager,
        EnumListFilter<LernaPackageType> lernaPackageTypeFilter
    ) {
        this.fileFinder = fileFinder;
        this.packageJsonReader = packageJsonReader;
        this.yarnLockParser = yarnLockParser;
        this.npmLockfilePackager = npmLockfilePackager;
        this.yarnPackager = yarnPackager;
        this.lernaPackageTypeFilter = lernaPackageTypeFilter;
    }

    // TODO: Should be multiple transformer classes. Don't go from File to CodeLocation in one swoop.
    public LernaResult generateLernaResult(File sourceDirectory, File rootPackageJson, List<LernaPackage> lernaPackages) {
        LernaLockFileResult rootLockFile = findLockFiles(sourceDirectory);
        LernaResult rootLernaResult = parse(sourceDirectory, rootPackageJson, rootLockFile, new ArrayList<>());

        List<NameVersion> externalPackages = lernaPackages.stream()
            .map(lernaPackage -> new NameVersion(lernaPackage.getName(), lernaPackage.getVersion()))
            .collect(Collectors.toList());

        List<CodeLocation> codeLocations = new ArrayList<>(rootLernaResult.getCodeLocations());
        for (LernaPackage lernaPackage : lernaPackages) {
            LernaResult lernaResult = extractPackage(lernaPackage, externalPackages, rootLockFile);
            if (lernaResult != null) {
                if (lernaResult.isSuccess()) {
                    lernaResult.getCodeLocations().stream()
                        .map(codeLocation -> new CodeLocation(codeLocation.getDependencyGraph(), codeLocation.getExternalId().orElse(null), new File(lernaPackage.getLocation())))
                        .forEach(codeLocations::add);
                } else if (lernaResult.isFailure()) {
                    String extractionErrorMessage = lernaResult.getException().map(Throwable::getMessage).orElse("Error message not found.");
                    logger.warn("Failed to extract lerna package: {}", extractionErrorMessage);
                    lernaResult.getException().ifPresent(exception -> logger.debug("Lerna Extraction Failure", exception));
                }
            }
        }

        return LernaResult.success(rootLernaResult.getProjectName(), rootLernaResult.getProjectVersionName(), codeLocations);
    }

    private @Nullable
    LernaResult extractPackage(LernaPackage lernaPackage, List<NameVersion> externalPackages, LernaLockFileResult rootLockFile) {
        String lernaPackageDetails = String.format("%s:%s at %s", lernaPackage.getName(), lernaPackage.getVersion(), lernaPackage.getLocation());

        if (lernaPackage.isPrivate() && lernaPackageTypeFilter.shouldExclude(LernaPackageType.PRIVATE)) {
            logger.debug("Skipping extraction of private lerna package {}.", lernaPackageDetails);
            return null;
        }

        logger.debug("Now extracting Lerna package {}.", lernaPackageDetails);
        File lernaPackageDirectory = new File(lernaPackage.getLocation());
        LernaLockFileResult lockFile = findLockFiles(lernaPackageDirectory);
        File packagesPackageJson = fileFinder.findFile(lernaPackageDirectory, LernaDetectable.PACKAGE_JSON);

        if (packagesPackageJson == null) {
            return LernaResult.failure(new FileNotFoundException(String.format(
                "A %s file was not found in %s.",
                LernaDetectable.PACKAGE_JSON,
                lernaPackageDirectory.getAbsolutePath()
            )));
        }

        if (lockFile.hasLockFile()) {
            return parse(lernaPackageDirectory, packagesPackageJson, lockFile, externalPackages);
        } else {
            return parse(lernaPackageDirectory, packagesPackageJson, rootLockFile, externalPackages);
        }

    }

    private LernaLockFileResult findLockFiles(File searchDirectory) {
        try {
            File packageLockJsonFile = fileFinder.findFile(searchDirectory, LernaDetectable.PACKAGE_LOCK_JSON);
            if (packageLockJsonFile != null) {
                return LernaLockFileResult.foundNpm(FileUtils.readFileToString(packageLockJsonFile, StandardCharsets.UTF_8));
            }
            File shrinkwrapJsonFile = fileFinder.findFile(searchDirectory, LernaDetectable.SHRINKWRAP_JSON);
            if (shrinkwrapJsonFile != null) {
                return LernaLockFileResult.foundNpm(FileUtils.readFileToString(shrinkwrapJsonFile, StandardCharsets.UTF_8));
            }
            File yarnLockFile = fileFinder.findFile(searchDirectory, LernaDetectable.YARN_LOCK);
            if (yarnLockFile != null) {
                return LernaLockFileResult.foundYarn(FileUtils.readLines(yarnLockFile, StandardCharsets.UTF_8));
            }

            return LernaLockFileResult.foundNone();
        } catch (IOException exception) {
            return LernaLockFileResult.foundNone();
        }
    }

    private LernaResult parse(File directory, File packageJson, LernaLockFileResult lockFile, List<NameVersion> externalPackages) {
        String packageJsonContents;
        try {
            packageJsonContents = FileUtils.readFileToString(packageJson, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return LernaResult.failure(e);
        }

        if (lockFile.getNpmLockContents().isPresent()) {
            try {
                NpmPackagerResult npmPackagerResult = npmLockfilePackager.parseAndTransform(
                    packageJsonContents,
                    lockFile.getNpmLockContents().get(),
                    externalPackages
                );
                return LernaResult.success(
                    npmPackagerResult.getProjectName(),
                    npmPackagerResult.getProjectVersion(),
                    Collections.singletonList(npmPackagerResult.getCodeLocation())
                );
            } catch (Exception exception) {
                return LernaResult.failure(exception);
            }
        } else if (lockFile.getYarnLockContents().isPresent()) {
            YarnLock yarnLock = yarnLockParser.parseYarnLock(lockFile.getYarnLockContents().get());
            NullSafePackageJson rootPackageJson = packageJsonReader.read(packageJsonContents);
            YarnResult yarnResult = yarnPackager.generateCodeLocation(rootPackageJson, YarnWorkspaces.EMPTY, yarnLock, externalPackages, ExcludedIncludedWildcardFilter.EMPTY);

            if (yarnResult.getException().isPresent()) {
                return LernaResult.failure(yarnResult.getException().get());
            }

            return LernaResult.success(yarnResult.getProjectName(), yarnResult.getProjectVersionName(), yarnResult.getCodeLocations());
        } else {
            return LernaResult.failure(
                new FileNotFoundException(
                    String.format(
                        "Lerna extraction from %s requires one of the following files: %s, %s, %s",
                        directory.getAbsolutePath(),
                        LernaDetectable.PACKAGE_LOCK_JSON,
                        LernaDetectable.SHRINKWRAP_JSON,
                        LernaDetectable.YARN_LOCK
                    )
                )
            );
        }
    }
}
