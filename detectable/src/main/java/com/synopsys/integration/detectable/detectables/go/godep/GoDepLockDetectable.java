package com.synopsys.integration.detectable.detectables.go.godep;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.PassedResultBuilder;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FilesNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.GoPkgLockfileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "GoDep Lock", language = "Golang", forge = "GitHub", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "File: Gopkg.lock.")
public class GoDepLockDetectable extends Detectable {
    public static final String GOPKG_LOCK_FILENAME = "Gopkg.lock";
    public static final String GOFILE_FILENAME_PATTERN = "Gopkg.toml";

    private final FileFinder fileFinder;
    private final GoDepExtractor goDepExtractor;

    private File goLock;
    private File goToml;

    public GoDepLockDetectable(DetectableEnvironment environment, FileFinder fileFinder, GoDepExtractor goDepExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.goDepExtractor = goDepExtractor;
    }

    @Override
    public DetectableResult applicable() {
        PassedResultBuilder passedResultBuilder = new PassedResultBuilder();
        goLock = fileFinder.findFile(environment.getDirectory(), GOPKG_LOCK_FILENAME);
        if (goLock == null) {
            goToml = fileFinder.findFile(environment.getDirectory(), GOFILE_FILENAME_PATTERN);
            if (goToml == null) {
                return new FilesNotFoundDetectableResult(GOPKG_LOCK_FILENAME, GOFILE_FILENAME_PATTERN);
            } else {
                passedResultBuilder.foundFile(goToml);
            }
        } else {
            passedResultBuilder.foundFile(goLock);
        }

        return passedResultBuilder.build();
    }

    @Override
    public DetectableResult extractable() {
        if (goLock == null && goToml != null) {
            return new GoPkgLockfileNotFoundDetectableResult(environment.getDirectory().getAbsolutePath());
        }
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        try (InputStream inputStream = new FileInputStream(goLock)) {
            return goDepExtractor.extract(inputStream);
        } catch (IOException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
