package com.blackducksoftware.integration.hub.detect.bomtool.go.parse;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GopkgLock {
    // see https://github.com/golang/dep/blob/master/lock.go for the source of the lock file
    private List<Project> projects;

    @SerializedName("solve-meta")
    private SolveMeta solveMeta;

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(final List<Project> projects) {
        this.projects = projects;
    }

    public SolveMeta getSolveMeta() {
        return solveMeta;
    }

    public void setSolveMeta(final SolveMeta solveMeta) {
        this.solveMeta = solveMeta;
    }
}
