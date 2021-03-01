/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck;

import java.util.ArrayList;
import java.util.List;

public class CustomFieldDocument {
    private List<CustomFieldElement> project = new ArrayList<>();
    private List<CustomFieldElement> version = new ArrayList<>();

    public List<CustomFieldElement> getProject() {
        return project;
    }

    public void setProject(final List<CustomFieldElement> project) {
        this.project = project;
    }

    public List<CustomFieldElement> getVersion() {
        return version;
    }

    public void setVersion(final List<CustomFieldElement> version) {
        this.version = version;
    }
}
