/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.dart.pubspec;

import java.io.File;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.PassedResultBuilder;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
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

    private File pubspecYaml;
    private File pubspecLock;

    private PubSpecExtractor pubSpecExtractor;

    public DartPubSpecLockDetectable(DetectableEnvironment environment, FileFinder fileFinder, PubSpecExtractor pubSpecExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.pubSpecExtractor = pubSpecExtractor;
    }

    @Override
    public DetectableResult applicable() {
        PassedResultBuilder passedResultBuilder = new PassedResultBuilder();

        pubspecYaml = fileFinder.findFile(environment.getDirectory(), PUBSPEC_YAML_FILENAME);
        pubspecLock = fileFinder.findFile(environment.getDirectory(), PUBSPEC_LOCK_FILENAME);

        if (pubspecLock != null) {
            passedResultBuilder.foundFile(pubspecLock);
        } else if (pubspecYaml != null) {
            passedResultBuilder.foundFile(pubspecYaml);
        }

        if (pubspecYaml == null && pubspecLock == null) {
            return new FilesNotFoundDetectableResult(PUBSPEC_LOCK_FILENAME, PUBSPEC_YAML_FILENAME);
        } else {
            return passedResultBuilder.build();
        }
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        if (pubspecLock == null && pubspecYaml != null) {
            return new PubSpecLockNotFoundDetectableResult(environment.getDirectory().getAbsolutePath());
        }
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException {
        return pubSpecExtractor.extract(pubspecLock, pubspecYaml);
    }
}
