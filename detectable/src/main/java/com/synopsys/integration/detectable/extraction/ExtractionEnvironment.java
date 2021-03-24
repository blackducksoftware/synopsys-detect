/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.extraction;

import java.io.File;

public class ExtractionEnvironment {
    private File outputDirectory;

    public ExtractionEnvironment(final File outputDirectory) {this.outputDirectory = outputDirectory;}

    public File getOutputDirectory() {
        return outputDirectory;
    }
}
