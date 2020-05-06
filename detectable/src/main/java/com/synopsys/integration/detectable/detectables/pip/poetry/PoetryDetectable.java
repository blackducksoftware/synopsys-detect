package com.synopsys.integration.detectable.detectables.pip.poetry;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.PythonResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

public class PoetryDetectable extends Detectable {
    private static final String PYPROJECT_TOML_FILE_NAME = "pyproject.toml";

    private final FileFinder fileFinder;

    private final PythonResolver pythonResolver;

    public PoetryDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final PythonResolver pythonResolver) {
        super(environment);
        this.fileFinder = fileFinder;
        this.pythonResolver = pythonResolver;
    }

    @Override
    public DetectableResult applicable() {
        final File pyprojectToml = fileFinder.findFile(environment.getDirectory(), PYPROJECT_TOML_FILE_NAME);
        if (pyprojectToml == null) {
            return new FileNotFoundDetectableResult(PYPROJECT_TOML_FILE_NAME);
        }

        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {

        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return null;
    }
}
