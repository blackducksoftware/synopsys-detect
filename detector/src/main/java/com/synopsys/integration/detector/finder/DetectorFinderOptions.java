/*
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.finder;

import java.io.File;
import java.util.function.Predicate;

public class DetectorFinderOptions {
    private final Predicate<File> fileFilter;
    private final int maximumDepth;
    private final boolean followSymLinks;

    public DetectorFinderOptions(Predicate<File> fileFilter, int maximumDepth, boolean followSymLinks) {
        this.fileFilter = fileFilter;
        this.maximumDepth = maximumDepth;
        this.followSymLinks = followSymLinks;
    }

    public Predicate<File> getFileFilter() {
        return fileFilter;
    }

    public int getMaximumDepth() {
        return maximumDepth;
    }

    public boolean followSymLinks() {
        return followSymLinks;
    }
}
