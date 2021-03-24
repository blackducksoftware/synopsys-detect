/**
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.explanation.Explanation;
import com.synopsys.integration.detectable.detectable.explanation.FoundExecutable;
import com.synopsys.integration.detectable.detectable.explanation.FoundFile;
import com.synopsys.integration.detectable.detectable.explanation.FoundInspector;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

public class PassedResultBuilder {
    private final List<Explanation> explanations = new ArrayList<>();
    private final List<File> relevantFiles = new ArrayList<>();

    public void foundFile(@NotNull File file) {
        explanations.add(new FoundFile(file));
        relevantFiles.add(file);
    }

    public void foundNullableFile(@Nullable File file) {
        if (file == null)
            return;
        foundFile(file);
    }

    public PassedDetectableResult build() {
        return new PassedDetectableResult(explanations, relevantFiles);
    }

    public void foundExecutable(final File exe) {
        explanations.add(new FoundExecutable(exe));
    }

    public void foundExecutable(final ExecutableTarget exe) {
        explanations.add(new FoundExecutable(exe));
    }

    public void foundInspector(final File inspector) {
        explanations.add(new FoundInspector(inspector));
    }
}
