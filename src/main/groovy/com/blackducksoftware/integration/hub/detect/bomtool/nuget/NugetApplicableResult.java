package com.blackducksoftware.integration.hub.detect.bomtool.nuget;

import java.io.File;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolApplicableResult;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class NugetApplicableResult extends BomToolApplicableResult {
    private final List<File> solutionFiles;
    private final List<File> projectFiles;
    private final String nugetExecutable;

    public NugetApplicableResult(final File directory, final List<File> solutionFiles, final List<File> projectFiles, final String nugetExecutable) {
        super(directory, BomToolType.MAVEN);
        this.solutionFiles = solutionFiles;
        this.projectFiles = projectFiles;
        this.nugetExecutable = nugetExecutable;
    }

    public List<File> getSolutionFiles() {
        return solutionFiles;
    }

    public List<File> getProjectFiles() {
        return projectFiles;
    }

    public String getNugetExecutable() {
        return nugetExecutable;
    }

}