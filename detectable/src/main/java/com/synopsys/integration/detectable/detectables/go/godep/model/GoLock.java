package com.synopsys.integration.detectable.detectables.go.godep.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GoLock {
    // see https://github.com/golang/dep/blob/master/lock.go for the source of the lock file
    @SerializedName("projects")
    public List<Project> projects;

    @SerializedName("solve-meta")
    public SolveMeta solveMeta;
}
