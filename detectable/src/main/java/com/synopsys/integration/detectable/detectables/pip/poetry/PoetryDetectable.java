package com.synopsys.integration.detectable.detectables.pip.poetry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PoetryRunPoetryInstallDetectResult;

public class PoetryDetectable extends Detectable {
    private static final String PYPROJECT_TOML_FILE_NAME = "pyproject.toml";
    private static final String POETRY_LOCK = "poetry.lock";

    private final FileFinder fileFinder;
    private final PoetryExtractor poetryExtractor;

    private File pyprojectToml;
    private File poetryLock;

    public PoetryDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final PoetryExtractor poetryExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.poetryExtractor = poetryExtractor;
    }

    @Override
    public DetectableResult applicable() {
        poetryLock = fileFinder.findFile(environment.getDirectory(), POETRY_LOCK);
        if (poetryLock == null) {
            pyprojectToml = fileFinder.findFile(environment.getDirectory(), PYPROJECT_TOML_FILE_NAME);
            if (pyprojectToml == null) {
                return new FileNotFoundDetectableResult(PYPROJECT_TOML_FILE_NAME);
            }
        }
        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() {
        if (poetryLock == null && pyprojectToml != null) {
            return new PoetryRunPoetryInstallDetectResult(environment.getDirectory().getAbsolutePath());
        }
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        try (final InputStream inputStream = new FileInputStream(poetryLock)) {
            return poetryExtractor.extract(inputStream);
        } catch (final IOException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
