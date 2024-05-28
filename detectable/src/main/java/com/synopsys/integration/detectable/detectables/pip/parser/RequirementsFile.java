package com.synopsys.integration.detectable.detectables.pip.parser;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class RequirementsFile {
    @SerializedName("default")
    public Map<String, RequirementsFileDependencyEntry> dependencies;
}
