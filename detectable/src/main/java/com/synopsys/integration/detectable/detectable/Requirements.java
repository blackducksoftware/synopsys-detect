/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detectable.detectable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private final List<Explanation> explanations = new ArrayList<>();
    private final List<File> relevantFiles = new ArrayList<>();

    private final FileFinder fileFinder;
    private final DetectableEnvironment environment;

    public Requirements(final FileFinder fileFinder, final DetectableEnvironment environment) {
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

    public File file(final String filename) {
        return file(environment.getDirectory(), filename);
    }

    public File directory(final String filename) { //We don't include directory in a relevant file.
        return file(environment.getDirectory(), filename, false);
    }

    public File optionalFile(final String filename, RequirementNotMetAction ifNotMet) {
        return optionalFile(environment.getDirectory(), filename, ifNotMet);
    }

    public File optionalFile(File directory, final String filename, RequirementNotMetAction ifNotMet) {
        return optionalFile(directory, filename, ifNotMet, true);
    }

    public File optionalFile(File directory, final String filename, RequirementNotMetAction ifNotMet, boolean isRelevant) {
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

    public File file(File directory, final String filename) {
        return file(directory, filename, true);
    }

    public File file(File directory, final String filename, boolean isRelevant) {
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
}

