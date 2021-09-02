/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.dart.pubdep;

import java.io.File;
import java.util.Arrays;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.PassedResultBuilder;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.executable.resolver.DartResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.FlutterResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutablesNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FilesNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PubSpecLockNotFoundDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Dart", forge = "Dart", requirementsMarkdown = "Files: pubspec.yaml, pubspec.lock.<br/><br/>Executable: dart, flutter")
public class DartPubDepDetectable extends Detectable {
    public static final String PUBSPEC_YAML_FILENAME = "pubspec.yaml";
    public static final String PUBSPEC_LOCK_FILENAME = "pubspec.lock";

    private final FileFinder fileFinder;
    private DartResolver dartResolver;
    private FlutterResolver flutterResolver;
    private PubDepsExtractor pubDepsExtractor;
    private DartPubDepsDetectableOptions dartPubDepsDetectableOptions;

    private File pubspecYaml;
    private File pubspecLock;

    private ExecutableTarget dartExe;
    private ExecutableTarget flutterExe;

    public DartPubDepDetectable(DetectableEnvironment environment, FileFinder fileFinder, PubDepsExtractor pubDepsExtractor, DartPubDepsDetectableOptions dartPubDepsDetectableOptions, DartResolver dartResolver,
        FlutterResolver flutterResolver) {
        super(environment);
        this.fileFinder = fileFinder;
        this.pubDepsExtractor = pubDepsExtractor;
        this.dartPubDepsDetectableOptions = dartPubDepsDetectableOptions;
        this.dartResolver = dartResolver;
        this.flutterResolver = flutterResolver;

    }

    @Override
    public DetectableResult applicable() {
        PassedResultBuilder passedResultBuilder = new PassedResultBuilder();

        pubspecYaml = fileFinder.findFile(environment.getDirectory(), PUBSPEC_YAML_FILENAME);
        pubspecLock = fileFinder.findFile(environment.getDirectory(), PUBSPEC_LOCK_FILENAME);

        if (pubspecLock != null) {
            passedResultBuilder.foundFile(pubspecLock);
        }
        if (pubspecYaml != null) {
            passedResultBuilder.foundFile(pubspecYaml);
        }

        if (pubspecLock != null && pubspecYaml == null) {
            return new FileNotFoundDetectableResult(PUBSPEC_YAML_FILENAME);
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
        dartExe = dartResolver.resolveDart();
        flutterExe = flutterResolver.resolveFlutter();
        if (dartExe == null && flutterExe == null) {
            return new ExecutablesNotFoundDetectableResult(Arrays.asList("dart", "flutter"));
        }
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException {
        return pubDepsExtractor.extract(environment.getDirectory(), dartExe, flutterExe, dartPubDepsDetectableOptions);
    }
}
