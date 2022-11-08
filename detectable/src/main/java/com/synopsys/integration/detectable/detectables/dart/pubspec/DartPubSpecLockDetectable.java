package com.synopsys.integration.detectable.detectables.dart.pubspec;

import java.io.File;
import java.io.IOException;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PubSpecLockNotFoundDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "Dart PubSpec Lock", language = "Dart", forge = "Dart", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "Files: pubspec.yaml, pubspec.lock.")
public class DartPubSpecLockDetectable extends Detectable {
    public static final String PUBSPEC_YAML_FILENAME = "pubspec.yaml";
    public static final String PUBSPEC_LOCK_FILENAME = "pubspec.lock";

    private final FileFinder fileFinder;

    @Nullable
    private File pubspecYaml;
    private File pubspecLock;

    private final PubSpecExtractor pubSpecExtractor;

    public DartPubSpecLockDetectable(DetectableEnvironment environment, FileFinder fileFinder, PubSpecExtractor pubSpecExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.pubSpecExtractor = pubSpecExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.eitherFile(PUBSPEC_YAML_FILENAME, PUBSPEC_LOCK_FILENAME, yaml -> pubspecYaml = yaml, lock -> pubspecLock = lock);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        if (pubspecLock == null) {
            return new PubSpecLockNotFoundDetectableResult(environment.getDirectory().getAbsolutePath());
        }
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException, IOException {
        return pubSpecExtractor.extract(pubspecLock, pubspecYaml);
    }
}
