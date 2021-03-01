/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.explanation;

import java.io.File;

public class FoundInspector extends Explanation {
    private final String inspectorDescription;

    public FoundInspector(File file) {
        this.inspectorDescription = file.toString();
    }

    public FoundInspector(String description) {
        this.inspectorDescription = description;
    }

    @Override
    public String describeSelf() {
        return "Found inspector: " + inspectorDescription;
    }
}
