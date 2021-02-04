package com.synopsys.integration.detectable.detectable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.explanation.Explanation;
import com.synopsys.integration.detectable.detectable.explanation.FoundExecutable;
import com.synopsys.integration.detectable.detectable.explanation.FoundFile;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

public class Requirements {
    private DetectableResult failure;
    private List<Explanation> explanations = new ArrayList<>();
    private List<File> relevantFiles = new ArrayList<>();

    private final FileFinder fileFinder;
    private final DetectableEnvironment environment;

    public Requirements(final FileFinder fileFinder, final DetectableEnvironment environment) {
        this.fileFinder = fileFinder;
        this.environment = environment;
    }

    public File file(final String filename) {
        return file(environment.getDirectory(), filename);
    }

    public File optionalFile(final String filename, RequirementNotMetAction ifNotMet) {
        return optionalFile(environment.getDirectory(), filename, ifNotMet);
    }

    public File optionalFile(File directory, final String filename, RequirementNotMetAction ifNotMet) {
        if (isAlreadyFailed())
            return null;

        File file = fileFinder.findFile(directory, filename);
        if (file == null) {
            ifNotMet.requirementNotMet();
        } else {
            relevantFiles.add(file);
            explanations.add(new FoundFile(file));
        }
        return file;
    }

    public File file(File directory, final String filename) {
        //Only difference between Optional File and Required File is Required populate failure, so if optional 'is not met' we can capture that by setting failure.
        return optionalFile(directory, filename, () -> {
            failure = new FileNotFoundDetectableResult(filename);
        });
    }

    private boolean isAlreadyFailed() {
        return failure != null;
    }

    public void ifCurrentlyMet(Runnable runnable) {
        if (isCurrentlyMet()) {
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
}

