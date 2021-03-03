package com.synopsys.integration.detectable.detectables.sbt.dot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SbtProjects {
    private final SbtProject rootProject;
    private final Map<String, SbtProject> projectsByTask;

    public SbtProjects(final SbtProject rootProject, final Map<String, SbtProject> projectsByTask) {
        this.rootProject = rootProject;
        this.projectsByTask = projectsByTask;
    }

    public SbtProject getRootProject() {
        return rootProject;
    }

    public Map<String, SbtProject> getProjectsByTask() {
        return projectsByTask;
    }

    public List<SbtProject> getAllProjects() {
        List<SbtProject> all = new ArrayList<>(projectsByTask.values());
        all.add(rootProject);
        return all;
    }
}
