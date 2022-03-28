package com.synopsys.integration.detectable.detectables.pipenv.parse.data;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class PipfileLock {
    @SerializedName("default")
    public Map<String, PipfileLockDependencyEntry> dependencies;

    @SerializedName("develop")
    public Map<String, PipfileLockDependencyEntry> devDependencies;
}
