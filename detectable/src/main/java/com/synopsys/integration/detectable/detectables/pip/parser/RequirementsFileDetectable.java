package com.synopsys.integration.detectable.detectables.pip.parser;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FilesNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "PIP Requirements Parse", language = "Python", forge = "PyPi", accuracy = DetectableAccuracyType.LOW, requirementsMarkdown = "File: requirements.txt")
public class RequirementsFileDetectable extends Detectable {
    public static final String REQUIREMENTS_DEFAULT_FILE_NAME = "requirements.txt";

    // TODO if provided, override with requirements txt file name from Detect's property

    private final FileFinder fileFinder;
    private final RequirementsFileExtractor requirementsFileExtractor;
    private final RequirementsFileDetectableOptions requirementsFileDetectableOptions;

    private List<File> requirementsFiles;
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
        requirementsFilePathsOverride = requirementsFileDetectableOptions.getRequirementsFilePaths();

        // If no overrides provided by --detect.pip.requirements.path, search for the default "requirements.txt" file.
        if (CollectionUtils.isEmpty(requirementsFilePathsOverride)) {
            requirementsFiles = fileFinder.findFiles(environment.getDirectory(), REQUIREMENTS_DEFAULT_FILE_NAME);
        } else {
            List<File> requirementsFilesOverrides = new ArrayList<>();
            File currentRequirementsFileOverride;
            for (Path requirementsFilePath : requirementsFilePathsOverride) {
                currentRequirementsFileOverride = requirementsFilePath.toFile();
                if (currentRequirementsFileOverride.exists()) {
                    requirementsFilesOverrides.add(currentRequirementsFileOverride);
                }
            }
            requirementsFiles = requirementsFilesOverrides;
        }
        boolean requirementFilesPresent = CollectionUtils.isNotEmpty(requirementsFiles);
        if (requirementFilesPresent) {
            return new PassedDetectableResult();
        } else {
            return new FilesNotFoundDetectableResult(REQUIREMENTS_DEFAULT_FILE_NAME);
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
}
