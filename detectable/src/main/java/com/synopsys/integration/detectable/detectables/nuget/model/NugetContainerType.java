package com.synopsys.integration.detectable.detectables.nuget.model;

import com.google.gson.annotations.SerializedName;

public enum NugetContainerType {
    @SerializedName("Solution")
    SOLUTION,
    @SerializedName("Project")
    PROJECT
}
