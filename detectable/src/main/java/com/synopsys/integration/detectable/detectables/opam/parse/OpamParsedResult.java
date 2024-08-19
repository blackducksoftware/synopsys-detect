package com.synopsys.integration.detectable.detectables.opam.parse;


import java.util.Set;
import java.util.Map;
import java.util.List;

public class OpamParsedResult {

    private final String projectName;

    private final String projectVersion;

    private final List<String> parsedDirectDependencies;

    private Map<String, String> lockFileDependencies;

    public OpamParsedResult(String projectName, String projectVersion, List<String> parsedDirectDependencies) {
        this.projectName = projectName;
        this.parsedDirectDependencies = parsedDirectDependencies;
        this.projectVersion = projectVersion;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public List<String> getParsedDirectDependencies() {
        return parsedDirectDependencies;
    }

    public String getProjectName() {
        return projectName;
    }

    public Map<String, String> getLockFileDependencies() {
        return lockFileDependencies;
    }

    public void setLockFileDependencies(Map<String, String> lockFileDependencies) {
        this.lockFileDependencies = lockFileDependencies;
    }
}
