package com.synopsys.integration.detect.tool.signaturescanner.operation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScanPath;
import com.synopsys.integration.detect.workflow.blackduck.ExclusionPatternCreator;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.util.NameVersion;

public class CalculateScanPathsOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final BlackDuckSignatureScannerOptions signatureScannerOptions;
    private final DirectoryManager directoryManager;
    private final FileFinder fileFinder;
    private final Predicate<File> fileFilter;

    public CalculateScanPathsOperation(
        BlackDuckSignatureScannerOptions signatureScannerOptions,
        DirectoryManager directoryManager,
        FileFinder fileFinder,
        Predicate<File> fileFilter
    ) {
        this.signatureScannerOptions = signatureScannerOptions;
        this.directoryManager = directoryManager;
        this.fileFinder = fileFinder;
        this.fileFilter = fileFilter;
    }

    public List<SignatureScanPath> determinePathsAndExclusions(NameVersion projectNameVersion, Integer maxDepth, @Nullable DockerTargetData dockerTargetData) throws IOException {
        List<Path> providedSignatureScanPaths = signatureScannerOptions.getSignatureScannerPaths();
        boolean userProvidedScanTargets = null != providedSignatureScanPaths && !providedSignatureScanPaths.isEmpty();
        List<String> exclusionPatterns = signatureScannerOptions.getExclusionPatterns();
        boolean followSymLinks = signatureScannerOptions.followSymLinks();

        List<SignatureScanPath> signatureScanPaths = new ArrayList<>();
        if (null != projectNameVersion.getName() && null != projectNameVersion.getVersion() && userProvidedScanTargets) { //TODO: Why are we doing this? -jp
            for (Path path : providedSignatureScanPaths) {
                logger.info(String.format("Registering explicit scan path %s", path));
                SignatureScanPath scanPath = createScanPath(path, maxDepth, exclusionPatterns, followSymLinks);
                signatureScanPaths.add(scanPath);
            }
        } else if (dockerTargetData != null && dockerTargetData.getSquashedImage().isPresent()) {
            SignatureScanPath scanPath = createScanPath(dockerTargetData.getSquashedImage().get().getCanonicalFile().toPath(), maxDepth, exclusionPatterns, followSymLinks);
            signatureScanPaths.add(scanPath);
        } else if (dockerTargetData != null && dockerTargetData.getProvidedImageTar().isPresent()) {
            SignatureScanPath scanPath = createScanPath(dockerTargetData.getProvidedImageTar().get().getCanonicalFile().toPath(), maxDepth, exclusionPatterns, followSymLinks);
            signatureScanPaths.add(scanPath);
        } else {
            Path sourcePath = directoryManager.getSourceDirectory().getAbsoluteFile().toPath();
            if (userProvidedScanTargets) {
                logger.warn(String.format("No Project name or version found. Skipping User provided scan targets - registering the source path %s to scan", sourcePath));
            } else {
                logger.info(String.format("No scan targets provided - registering the source path %s to scan", sourcePath));
            }
            SignatureScanPath scanPath = createScanPath(sourcePath, maxDepth, exclusionPatterns, followSymLinks);
            signatureScanPaths.add(scanPath);
        }
        return signatureScanPaths;
    }

    private SignatureScanPath createScanPath(Path path, Integer maxDepth, List<String> exclusionPatterns, boolean followSymLinks) {
        File target = path.toFile();
        ExclusionPatternCreator exclusionPatternCreator = new ExclusionPatternCreator(fileFinder, fileFilter, target);

        Set<String> scanExclusionPatterns = new HashSet<>();

        // First add explicit exclusions that are correctly formatted
        scanExclusionPatterns.addAll(exclusionPatterns.stream()
            .filter(this::isCorrectlyFormattedExclusion)
            .collect(Collectors.toSet()));

        scanExclusionPatterns.addAll(exclusionPatternCreator.determineExclusionPatterns(followSymLinks, maxDepth, exclusionPatterns));

        SignatureScanPath signatureScanPath = new SignatureScanPath();
        signatureScanPath.setTargetPath(target);
        signatureScanPath.getExclusions().addAll(scanExclusionPatterns);
        return signatureScanPath;
    }

    private boolean isCorrectlyFormattedExclusion(String exclusion) {
        return exclusion.startsWith("/") && exclusion.endsWith("/") && !exclusion.contains("**");
    }
}
