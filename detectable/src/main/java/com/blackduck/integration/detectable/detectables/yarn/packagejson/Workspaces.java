package com.blackduck.integration.detectable.detectables.yarn.packagejson;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Workspaces {

    @SerializedName("packages")
    public List<String> workspaceSubdirPatterns = new LinkedList<>();
}
