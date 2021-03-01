/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.project;

import java.io.File;
import java.util.List;

public class DetectProject {
    private final String projectName;
    private final String projectVersion;
    private final List<File> bdioFiles;

    public DetectProject(final String projectName, final String projectVersion, final List<File> bdioFiles) {
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.bdioFiles = bdioFiles;
    }

    public List<File> getBdioFiles() {
        return bdioFiles;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

}
