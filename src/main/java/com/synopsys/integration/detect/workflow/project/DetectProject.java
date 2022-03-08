package com.synopsys.integration.detect.workflow.project;

import java.io.File;
import java.util.List;

public class DetectProject {
    private final String projectName;
    private final String projectVersion;
    private final List<File> bdioFiles;

    public DetectProject(String projectName, String projectVersion, List<File> bdioFiles) {
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
