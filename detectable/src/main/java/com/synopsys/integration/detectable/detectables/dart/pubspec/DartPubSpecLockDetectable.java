/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.dart.pubspec;

import java.io.File;
import java.util.Optional;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FilesNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PubSpecLockNotFoundDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Dart", forge = "Dart", requirementsMarkdown = "Files: pubspec.yaml, pubspec.lock.")
public class DartPubSpecLockDetectable extends Detectable {
    public static final String PUBSPEC_YAML_FILENAME = "pubspec.yaml";
    public static final String PUBSPEC_LOCK_FILENAME = "pubspec.lock";

    private final FileFinder fileFinder;

    private Optional<File> pubspecYaml;
    private Optional<File> pubspecLock;

    private PubSpecExtractor pubSpecExtractor;

    public DartPubSpecLockDetectable(DetectableEnvironment environment, FileFinder fileFinder, PubSpecExtractor pubSpecExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.pubSpecExtractor = pubSpecExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);

        pubspecYaml = requirements.optionalFile(PUBSPEC_YAML_FILENAME);
        pubspecLock = requirements.optionalFile(PUBSPEC_LOCK_FILENAME);

        if (pubspecYaml.isPresent() || pubspecLock.isPresent()) {
            return requirements.result();
        } else {
            return new FilesNotFoundDetectableResult(PUBSPEC_LOCK_FILENAME, PUBSPEC_YAML_FILENAME);
        }
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        if (pubspecLock.isPresent() && pubspecYaml.isPresent()) {
            return new PassedDetectableResult();
        } else if (!pubspecLock.isPresent()) {
            return new PubSpecLockNotFoundDetectableResult(environment.getDirectory().getAbsolutePath());
        } else {
            return new FileNotFoundDetectableResult(PUBSPEC_YAML_FILENAME);
        }

    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException {
        // pubspec.yaml cannot be null - ac 9/8/21
        return pubSpecExtractor.extract(pubspecLock.orElse(null), pubspecYaml.orElse(null));
    }
}
