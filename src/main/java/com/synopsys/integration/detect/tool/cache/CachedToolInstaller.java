package com.synopsys.integration.detect.tool.cache;

import java.io.File;
import java.util.Optional;

import com.google.gson.Gson;

public class CachedToolInstaller {
    private final String pathToCachedInspectorFile;
    private final Gson gson;

    public CachedToolInstaller(String pathToCachedInspectorFile, Gson gson) {
        this.pathToCachedInspectorFile = pathToCachedInspectorFile;
        this.gson = gson;
    }

    public Optional<File> installCachedDockerInspector() {
        return null;
    }

    public Optional<File> installCachedProjectInspector() {
        return null;
    }

    public Optional<File> installCachedNugetInspector() {
        return null;
    }
}
