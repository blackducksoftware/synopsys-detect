/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.file;

import java.io.File;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class SourceDirectoryDecision {
    @Nullable
    private final File sourceDirectory;

    private SourceDirectoryDecision(@Nullable File sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public static SourceDirectoryDecision withSourceDirectory(File sourceDirectory) {
        return new SourceDirectoryDecision(sourceDirectory);
    }

    public static SourceDirectoryDecision none() {
        return new SourceDirectoryDecision(null);
    }

    public Optional<File> getSourceDirectory() {
        return Optional.ofNullable(sourceDirectory);
    }
}
