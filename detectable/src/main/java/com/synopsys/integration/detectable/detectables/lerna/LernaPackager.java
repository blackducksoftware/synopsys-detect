package com.synopsys.integration.detectable.detectables.lerna;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.lerna.model.LernaPackage;
import com.synopsys.integration.detectable.detectables.lerna.model.LernaResult;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmLockfileOptions;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmParseResult;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockfilePackager;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockOptions;
import com.synopsys.integration.detectable.detectables.yarn.YarnPackager;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockResult;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnTransformer;

public class LernaPackager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FileFinder fileFinder;
    private final NpmLockfilePackager npmLockfileParser;
    private final NpmLockfileOptions npmLockfileOptions;
    private final YarnPackager yarnPackager;
    private final YarnTransformer yarnTransformer;
    private final YarnLockOptions yarnLockOptions;

    public LernaPackager(FileFinder fileFinder, NpmLockfilePackager npmLockfileParser, NpmLockfileOptions npmLockfileOptions, YarnPackager yarnPackager,
        YarnTransformer yarnTransformer, YarnLockOptions yarnLockOptions) {
        this.fileFinder = fileFinder;
        this.npmLockfileParser = npmLockfileParser;
        this.npmLockfileOptions = npmLockfileOptions;
        this.yarnPackager = yarnPackager;
        this.yarnTransformer = yarnTransformer;
        this.yarnLockOptions = yarnLockOptions;
    }

    public LernaResult generateLernaResult(File sourceDirectory, List<LernaPackage> lernaPackages) {
        LernaResult rootExtraction = extractWithRootLockfile(sourceDirectory, sourceDirectory);
        if (rootExtraction.isFailure()) {
            return rootExtraction;
        }

        List<CodeLocation> codeLocations = new ArrayList<>();
        for (LernaPackage lernaPackage : lernaPackages) {
            logger.debug(String.format("Now extracting Lerna package %s:%s at %s.", lernaPackage.getName(), lernaPackage.getVersion(), lernaPackage.getLocation()));
            File lernaPackageDirectory = new File(sourceDirectory.getParent(), lernaPackage.getLocation());

            LernaResult lernaResult = extractLernaPackage(sourceDirectory, lernaPackageDirectory);
            if (lernaResult.isSuccess()) {
                logger.debug(String.format("Extraction completed successfully on %s:%s.", lernaPackage.getName(), lernaPackage.getLocation()));

                lernaResult.getCodeLocations().stream()
                    .map(codeLocation -> new CodeLocation(codeLocation.getDependencyGraph(), codeLocation.getExternalId().orElse(null), lernaPackageDirectory))
                    .forEach(codeLocations::add);
            } else {
                String extractionErrorMessage = lernaResult.getException().map(Throwable::getMessage).orElse("Error message not found.");
                logger.warn(String.format("Failed to extract lerna package: %s", extractionErrorMessage));
                lernaResult.getException().ifPresent(exception -> logger.debug("Lerna Extraction Failure", exception));
            }
        }

        return LernaResult.success(rootExtraction.getProjectName(), rootExtraction.getProjectVersionName(), codeLocations);
    }

    private LernaResult extractLernaPackage(File sourceDirectory, File lernaPackageDirectory) {
        LernaResult lernaResult = extractWithLocalLockfile(lernaPackageDirectory);
        if (lernaResult.getException().isPresent()) {
            lernaResult = extractWithRootLockfile(lernaPackageDirectory, sourceDirectory);
        }

        return lernaResult;
    }

    private LernaResult extractWithRootLockfile(File lernaPackageDirectory, File sourceDirectory) {
        File packageJsonFile = fileFinder.findFile(lernaPackageDirectory, LernaDetectable.PACKAGE_JSON);
        return extractWithAnyLockfile(sourceDirectory, packageJsonFile);
    }

    private LernaResult extractWithLocalLockfile(File lernaPackageDirectory) {
        File packageJsonFile = fileFinder.findFile(lernaPackageDirectory, LernaDetectable.PACKAGE_JSON);
        return extractWithAnyLockfile(lernaPackageDirectory, packageJsonFile);
    }

    private LernaResult extractWithAnyLockfile(File searchDirectory, File packageJsonFile) {
        File packageLockJsonFile = fileFinder.findFile(searchDirectory, LernaDetectable.PACKAGE_LOCK_JSON);
        File shrinkwrapJsonFile = fileFinder.findFile(searchDirectory, LernaDetectable.SHRINKWRAP_JSON);
        File yarnLockFile = fileFinder.findFile(searchDirectory, LernaDetectable.YARN_LOCK);

        if (packageLockJsonFile != null) {
            return extractFromNpmLockfile(packageJsonFile, packageLockJsonFile);
        } else if (shrinkwrapJsonFile != null) {
            return extractFromNpmLockfile(packageJsonFile, shrinkwrapJsonFile);
        } else if (yarnLockFile != null) {
            return extractFromYarnLock(packageJsonFile, yarnLockFile);
        } else {
            return LernaResult.failure(
                new FileNotFoundException(
                    String.format("Lerna extraction from %s requires one of the following files: %s, %s, %s",
                        searchDirectory.getAbsolutePath(),
                        LernaDetectable.PACKAGE_LOCK_JSON,
                        LernaDetectable.SHRINKWRAP_JSON,
                        LernaDetectable.YARN_LOCK
                    )
                )
            );
        }
    }

    private LernaResult extractFromNpmLockfile(File packageJsonFile, File npmLockfile) {
        try {
            String lockfileText = FileUtils.readFileToString(npmLockfile, StandardCharsets.UTF_8);
            String packageJsonText = FileUtils.readFileToString(packageJsonFile, StandardCharsets.UTF_8);

            NpmParseResult npmParseResult = npmLockfileParser.parse(packageJsonText, lockfileText, npmLockfileOptions.shouldIncludeDeveloperDependencies());

            return LernaResult.success(npmParseResult.getProjectName(), npmParseResult.getProjectVersion(), Collections.singletonList(npmParseResult.getCodeLocation()));
        } catch (IOException exception) {
            return LernaResult.failure(exception);
        }
    }

    private LernaResult extractFromYarnLock(File packageJsonFile, File yarnLockFile) {
        try {
            YarnLockResult yarnLockResult = yarnPackager.generateYarnResult(packageJsonFile, yarnLockFile);
            DependencyGraph dependencyGraph = yarnTransformer.transform(yarnLockResult, yarnLockOptions.useProductionOnly());
            CodeLocation codeLocation = new CodeLocation(dependencyGraph);

            return LernaResult.success(yarnLockResult.getPackageJson().name, yarnLockResult.getPackageJson().version, Collections.singletonList(codeLocation));
        } catch (IOException | MissingExternalIdException exception) {
            return LernaResult.failure(exception);
        }
    }
}
