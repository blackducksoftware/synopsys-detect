/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public File file(String filename) {
        return file(environment.getDirectory(), filename);
    }

    public File directory(String filename) { //We don't include directory in a relevant file.
        return file(environment.getDirectory(), filename, false);
    }

    public Optional<File> optionalFile(String filename) {
        return Optional.ofNullable(optionalFile(environment.getDirectory(), filename, () -> {}));
    }

    public File optionalFile(String filename, RequirementNotMetAction ifNotMet) {
        return optionalFile(environment.getDirectory(), filename, ifNotMet);
    }

    public File optionalFile(File directory, String filename, RequirementNotMetAction ifNotMet) {
        return optionalFile(directory, filename, ifNotMet, true);
    }

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

    public File file(File directory, String filename) {
        return file(directory, filename, true);
    }

    public File file(File directory, String filename, boolean isRelevant) {
        //Only difference between Optional File and Required File is Required populate failure, so if optional 'is not met' we can capture that by setting failure.
        return optionalFile(directory, filename, () -> {
            failure = new FileNotFoundDetectableResult(filename);
        }, isRelevant);
    }

    private boolean isAlreadyFailed() {
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

        if (anyFiles.size() > 0) {
            anyFiles.forEach(foundFile -> explanations.add(new FoundFile(foundFile)));
        } else {
            failure = new FilesNotFoundDetectableResult(patterns);
        }
    }
}

