/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pipenv;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.PassedResultBuilder;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.PipenvResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PythonResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FilesNotFoundDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

import java.io.File;

@DetectableInfo(language = "Python", forge = "PyPi", requirementsMarkdown = "Files: Pipfile or Pipfile.lock. Executables: python or python3, and pipenv.")
public class PipenvDetectable extends Detectable {
    public static final String SETUPTOOLS_DEFAULT_FILE_NAME = "setup.py";
    public static final String PIPFILE_FILE_NAME = "Pipfile";
    public static final String PIPFILE_DOT_LOCK_FILE_NAME = "Pipfile.lock";

    private final PipenvDetectableOptions pipenvDetectableOptions;
    private final FileFinder fileFinder;
    private final PythonResolver pythonResolver;
    private final PipenvResolver pipenvResolver;
    private final PipenvExtractor pipenvExtractor;

    private ExecutableTarget pythonExe;
    private ExecutableTarget pipenvExe;
    private File setupFile;

    public PipenvDetectable(DetectableEnvironment environment, PipenvDetectableOptions pipenvDetectableOptions, FileFinder fileFinder, PythonResolver pythonResolver, PipenvResolver pipenvResolver,
        PipenvExtractor pipenvExtractor) {
        super(environment);
        this.pipenvDetectableOptions = pipenvDetectableOptions;
        this.fileFinder = fileFinder;
        this.pipenvResolver = pipenvResolver;
        this.pipenvExtractor = pipenvExtractor;
        this.pythonResolver = pythonResolver;
    }

    @Override
    public DetectableResult applicable() {
        File pipfile = fileFinder.findFile(environment.getDirectory(), PIPFILE_FILE_NAME);
        File pipfileDotLock = fileFinder.findFile(environment.getDirectory(), PIPFILE_DOT_LOCK_FILE_NAME);

        if (pipfile != null || pipfileDotLock != null) {
            PassedResultBuilder passedResultBuilder = new PassedResultBuilder();
            passedResultBuilder.foundNullableFile(pipfile);
            passedResultBuilder.foundNullableFile(pipfileDotLock);
            return passedResultBuilder.build();
        } else {
            return new FilesNotFoundDetectableResult(PIPFILE_FILE_NAME, PIPFILE_DOT_LOCK_FILE_NAME);
        }

    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        pythonExe = requirements.executable(pythonResolver::resolvePython, "python");
        pipenvExe = requirements.executable(pipenvResolver::resolvePipenv, "pipenv");

        setupFile = fileFinder.findFile(environment.getDirectory(), SETUPTOOLS_DEFAULT_FILE_NAME);
        requirements.explainNullableFile(setupFile);

        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        //TODO: Handle null better.
        return pipenvExtractor.extract(environment.getDirectory(), pythonExe, pipenvExe, setupFile, pipenvDetectableOptions.getPipProjectName().orElse(""), pipenvDetectableOptions.getPipProjectVersionName().orElse(""),
            pipenvDetectableOptions.isPipProjectTreeOnly());
    }

}

