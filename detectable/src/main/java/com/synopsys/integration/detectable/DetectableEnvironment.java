/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable;

import java.io.File;

public class DetectableEnvironment {
    private final File directory;

    public DetectableEnvironment(final File directory) {
        this.directory = directory;
    }

    public File getDirectory() {
        return directory;
    }
}
