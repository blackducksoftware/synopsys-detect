/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.explanation;

import java.io.File;

public class FoundFile extends Explanation {
    private final String file;

    public FoundFile(File file) {
        this.file = file.toString();
    }

    public FoundFile(String file) {
        this.file = file;
    }

    @Override
    public String describeSelf() {
        return "Found file: " + file;
    }
}
