package com.blackduck.integration.detectable.detectables.dart.pubdep;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

import com.blackduck.integration.common.util.finder.FileFinder;
import com.blackduck.integration.detectable.Detectable;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.ExecutableTarget;
import com.blackduck.integration.detectable.detectable.DetectableAccuracyType;
import com.blackduck.integration.detectable.detectable.Requirements;
import com.blackduck.integration.detectable.detectable.annotation.DetectableInfo;
import com.blackduck.integration.detectable.detectable.exception.DetectableException;
import com.blackduck.integration.detectable.detectable.executable.ExecutableFailedException;
import com.blackduck.integration.detectable.detectable.executable.resolver.DartResolver;
import com.blackduck.integration.detectable.detectable.executable.resolver.FlutterResolver;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.blackduck.integration.detectable.extraction.ExtractionEnvironment;
import com.blackduck.integration.detectable.detectable.result.DetectableResult;
import com.blackduck.integration.detectable.detectable.result.ExecutablesNotFoundDetectableResult;
import com.blackduck.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.blackduck.integration.detectable.detectable.result.FilesNotFoundDetectableResult;
import com.blackduck.integration.detectable.detectable.result.PassedDetectableResult;
import com.blackduck.integration.detectable.detectable.result.PubSpecLockNotFoundDetectableResult;

@DetectableInfo(name = "Dart CLI", language = "Dart", forge = "Dart", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "Files: pubspec.yaml, pubspec.lock. Executable: dart, flutter")
public class DartPubDepDetectable extends Detectable {
    public static final String PUBSPEC_YAML_FILENAME = "pubspec.yaml";
    public static final String PUBSPEC_LOCK_FILENAME = "pubspec.lock";

    private final FileFinder fileFinder;
    private final DartResolver dartResolver;
    private final FlutterResolver flutterResolver;
    private final PubDepsExtractor pubDepsExtractor;
    private final DartPubDepsDetectableOptions dartPubDepsDetectableOptions;

    private Optional<File> pubspecYaml;
    private Optional<File> pubspecLock;

    private ExecutableTarget dartExe;
    private ExecutableTarget flutterExe;

    public DartPubDepDetectable(
        DetectableEnvironment environment,
        FileFinder fileFinder,
        PubDepsExtractor pubDepsExtractor,
        DartPubDepsDetectableOptions dartPubDepsDetectableOptions,
        DartResolver dartResolver,
        FlutterResolver flutterResolver
    ) {
        super(environment);
        this.fileFinder = fileFinder;
        this.pubDepsExtractor = pubDepsExtractor;
        this.dartPubDepsDetectableOptions = dartPubDepsDetectableOptions;
        this.dartResolver = dartResolver;
        this.flutterResolver = flutterResolver;

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
        if (!pubspecLock.isPresent() && pubspecYaml.isPresent()) {
            return new PubSpecLockNotFoundDetectableResult(environment.getDirectory().getAbsolutePath());
        } else if (pubspecLock.isPresent() && !pubspecYaml.isPresent()) {
            return new FileNotFoundDetectableResult(PUBSPEC_YAML_FILENAME);
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
        // pubspec.yaml cannot be null - ac 9/8/21
        return pubDepsExtractor.extract(environment.getDirectory(), dartExe, flutterExe, dartPubDepsDetectableOptions, pubspecYaml.orElse(null));
    }
}
