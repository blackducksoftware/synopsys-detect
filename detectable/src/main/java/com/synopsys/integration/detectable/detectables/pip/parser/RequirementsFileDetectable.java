package com.synopsys.integration.detectable.detectables.pip.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExceptionDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FilesNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "PIP Requirements Parser", language = "Python", forge = "PyPi", accuracy = DetectableAccuracyType.LOW, requirementsMarkdown = "File: requirements.txt")
public class RequirementsFileDetectable extends Detectable {
    public static final String REQUIREMENTS_DEFAULT_FILE_NAME = "requirements.txt";

    private final FileFinder fileFinder;
    private final RequirementsFileExtractor requirementsFileExtractor;
    private final RequirementsFileDetectableOptions requirementsFileDetectableOptions;

    private Set<File> requirementsFiles;
    private List<Path> requirementsFilePathsOverride;

    public RequirementsFileDetectable(
        DetectableEnvironment environment,
        FileFinder fileFinder,
        RequirementsFileExtractor requirementsFileExtractor,
        RequirementsFileDetectableOptions requirementsFileDetectableOptions
    ) {
        super(environment);
        this.fileFinder = fileFinder;
        this.requirementsFileExtractor = requirementsFileExtractor;
        this.requirementsFileDetectableOptions = requirementsFileDetectableOptions;
    }

    @Override
    public DetectableResult applicable() {
        // Applicable only if overrides are provided by --detect.pip.requirements.path or if at least one "requirements.txt" file is present in source directory.
        try {
            requirementsFilePathsOverride = requirementsFileDetectableOptions.getRequirementsFilePaths();
            // If no overrides provided by --detect.pip.requirements.path, search for the default "requirements.txt" file(s).
            // If overrides are provided, they have precedence over all files. So we do not include child requirements.txt files in the default parent.
            if (CollectionUtils.isEmpty(requirementsFilePathsOverride)) {
                processDefaultParentRequirementsFiles();
            } else {
                processRequirementsFileOverrides();
            }
            boolean requirementFilesPresent = CollectionUtils.isNotEmpty(requirementsFiles);
            if (requirementFilesPresent) {
                return new PassedDetectableResult();
            } else {
                return new FilesNotFoundDetectableResult(REQUIREMENTS_DEFAULT_FILE_NAME);
            }}
        catch (IOException e){
            return new ExceptionDetectableResult(e);
        }
    }

    @Override
    public DetectableResult extractable() {
        // Parser detectors do not require any executables check
        // Hence, they would always pass the extractable check if they pass the applicable check
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        try {
            return requirementsFileExtractor.extract(requirementsFiles);
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).failure(String.format("Failed to parse %s", REQUIREMENTS_DEFAULT_FILE_NAME)).build();
        }
    }

    private void processRequirementsFileOverrides() throws IOException {
        Set<File> requirementsFilesOverrides = new HashSet<>();
        File currentRequirementsFileOverride;
        for (Path requirementsFilePath : requirementsFilePathsOverride) {
            currentRequirementsFileOverride = requirementsFilePath.toFile();
            if (currentRequirementsFileOverride.exists()) {
                requirementsFilesOverrides.add(currentRequirementsFileOverride);
            }
        }
        requirementsFiles = requirementsFilesOverrides;
    }

    private void processDefaultParentRequirementsFiles() throws IOException {
        List<File> requirementsFilesList = fileFinder.findFiles(environment.getDirectory(), REQUIREMENTS_DEFAULT_FILE_NAME);
        requirementsFiles = new HashSet<>(requirementsFilesList);
        Set<File> childRequirementsFiles = new HashSet<>();
        // If there are more than one parent requirements.txt files present, check for child references in each parent file
        for (File parentRequirementsFile : requirementsFiles) {
            childRequirementsFiles.addAll(requirementsFileExtractor.findChildFileReferencesInParent(parentRequirementsFile));
        }
        requirementsFiles.addAll(childRequirementsFiles);
    }
}
