package com.synopsys.integration.detectable.detectable;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.explanation.Explanation;
import com.synopsys.integration.detectable.detectable.explanation.FoundExecutable;
import com.synopsys.integration.detectable.detectable.explanation.FoundFile;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FilesNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

public class Requirements {
    private DetectableResult failure;
    private final List<Explanation> explanations = new ArrayList<>();
    private final List<File> relevantFiles = new ArrayList<>();

    private final FileFinder fileFinder;
    private final DetectableEnvironment environment;

    public Requirements(FileFinder fileFinder, DetectableEnvironment environment) {
        this.fileFinder = fileFinder;
        this.environment = environment;
    }

    public void anyFile(SearchPattern... searchPatterns) {
        List<SearchPattern> foundSearchPatterns = new LinkedList<>();
        for (SearchPattern searchPattern : searchPatterns) {
            File file = fileFinder.findFile(searchPattern.getSearchDirectory(), searchPattern.getFilePattern());
            if (file != null) {
                explainFile(file);
                searchPattern.getFileConsumer().accept(file);
                foundSearchPatterns.add(searchPattern);
            }
        }
        if (CollectionUtils.isEmpty(foundSearchPatterns)) {
            failure = new FilesNotFoundDetectableResult(Bds.of(searchPatterns).map(SearchPattern::getFilePattern).toList());
        }
    }

    public void eitherFile(String primaryPattern, String secondaryPattern) {
        eitherFile(primaryPattern, environment.getDirectory(), secondaryPattern, environment.getDirectory(), primary -> {}, secondary -> {});
    }

    public void eitherFile(String primaryPattern, String secondaryPattern, Consumer<File> primaryConsumer, Consumer<File> secondaryConsumer) {
        eitherFile(primaryPattern, environment.getDirectory(), secondaryPattern, environment.getDirectory(), primaryConsumer, secondaryConsumer);
    }

    public void eitherFile(
        String primaryPattern,
        File primaryDirectory,
        String secondaryPattern,
        File secondaryDirectory,
        Consumer<File> primaryConsumer,
        Consumer<File> secondaryConsumer
    ) {
        File primary = fileFinder.findFile(primaryDirectory, primaryPattern);
        File secondary = fileFinder.findFile(secondaryDirectory, secondaryPattern);
        if (primary == null && secondary == null) {
            failure = new FilesNotFoundDetectableResult(primaryPattern, secondaryPattern);
        }
        if (primary != null) {
            explainFile(primary);
            primaryConsumer.accept(primary);
        }
        if (secondary != null) {
            explainFile(secondary);
            secondaryConsumer.accept(secondary);
        }
    }

    public void explain(Explanation explanation) {
        explanations.add(explanation);
    }

    public void explainFile(@NotNull File file) {
        relevantFiles.add(file);
        explanations.add(new FoundFile(file));
    }

    public void explainDirectory(@NotNull File file) {
        explanations.add(new FoundFile(file));
    }

    public void explainNullableFile(@Nullable File file) {
        if (file == null)
            return;
        explainFile(file);
    }

    public File directory(String filename) { //We don't include directory in a relevant file.
        return file(environment.getDirectory(), filename, false, () -> new FileNotFoundDetectableResult(filename));
    }

    public Optional<File> optionalFile(String filename) {
        return Optional.ofNullable(optionalFile(environment.getDirectory(), filename));
    }

    @Nullable
    public File optionalFile(File directory, String filename) {
        return optionalFile(directory, filename, () -> {});
    }

    @Nullable
    public File optionalFile(String filename, RequirementNotMetAction ifNotMet) {
        return optionalFile(environment.getDirectory(), filename, ifNotMet);
    }

    @Nullable
    public File optionalFile(File directory, String filename, RequirementNotMetAction ifNotMet) {
        return optionalFile(directory, filename, ifNotMet, true);
    }

    @Nullable
    public File optionalFile(File directory, String filename, RequirementNotMetAction ifNotMet, boolean isRelevant) {
        if (isAlreadyFailed())
            return null;

        File file = fileFinder.findFile(directory, filename);
        if (file == null) {
            ifNotMet.requirementNotMet();
        } else {
            if (isRelevant) {
                relevantFiles.add(file);
            }
            explanations.add(new FoundFile(file));
        }
        return file;
    }

    public File file(String filename) {
        return file(environment.getDirectory(), filename);
    }

    public File file(String filename, FailedResultCreator createMissingResult) {
        return file(environment.getDirectory(), filename, createMissingResult);
    }

    public File file(File directory, String filename) {
        return file(directory, filename, true, () -> new FileNotFoundDetectableResult(filename));
    }

    public File file(File directory, String filename, FailedResultCreator createMissingResult) {
        return file(directory, filename, true, createMissingResult);
    }

    public File file(File directory, String filename, boolean isRelevant, FailedResultCreator createMissingResult) {
        // The only difference between Optional File and Required File is Required populate failure, so if optional 'is not met' we can capture that by setting failure.
        return optionalFile(directory, filename, () -> failure = createMissingResult.createFailedResult(), isRelevant);
    }

    public boolean isAlreadyFailed() {
        return failure != null;
    }

    public void ifCurrentlyMet(Runnable runnable) {
        if (isCurrentlyMet()) {
            runnable.run();
        }
    }

    public void ifNotCurrentlyMet(Runnable runnable) {
        if (!isCurrentlyMet()) {
            runnable.run();
        }
    }

    public boolean isCurrentlyMet() {
        return !isAlreadyFailed();
    }

    public File executable(Resolver resolver, String name) throws DetectableException {
        if (isAlreadyFailed())
            return null;

        File resolved = resolver.resolve();
        if (resolved == null) {
            failure = new ExecutableNotFoundDetectableResult(name);
        } else {
            explanations.add(new FoundExecutable(resolved));
        }
        return resolved;
    }

    public ExecutableTarget executable(ExecutableTargetResolver resolver, String name) throws DetectableException {
        if (isAlreadyFailed())
            return null;

        ExecutableTarget resolved = resolver.resolve();
        if (resolved == null) {
            failure = new ExecutableNotFoundDetectableResult(name);
        } else {
            explanations.add(new FoundExecutable(resolved));
        }
        return resolved;
    }

    public DetectableResult result() {
        if (failure != null)
            return failure;
        return new PassedDetectableResult(explanations, relevantFiles);
    }

    public void anyFileMatchesPatterns(List<String> patterns) {
        List<File> anyFiles = fileFinder.findFiles(environment.getDirectory(), patterns);

        if (CollectionUtils.isNotEmpty(anyFiles)) {
            anyFiles.forEach(foundFile -> explanations.add(new FoundFile(foundFile)));
        } else {
            failure = new FilesNotFoundDetectableResult(patterns);
        }
    }
}

