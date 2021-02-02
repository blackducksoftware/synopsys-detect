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

    public File file(final FileFinder fileFinder, final DetectableEnvironment environment, final String filename) {
        return file(fileFinder, environment.getDirectory(), filename);
    }

    public File file(final FileFinder fileFinder, File directory, final String filename) {
        if (isAlreadyFailed())
            return null;

        File file = fileFinder.findFile(directory, filename);
        if (file == null) {
            failure = new FileNotFoundDetectableResult(filename);
        } else {
            explanations.add(new FoundFile(file));
        }
        return file;
    }

    private boolean isAlreadyFailed() {
        return failure != null;
    }

    public void ifNotFailed(Runnable runnable) {
        if (!isAlreadyFailed()) {
            runnable.run();
        }
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
        return new PassedDetectableResult(explanations);
    }
}

