package com.synopsys.integration.detectable.detectables.swift.lock.data;

import com.google.gson.annotations.SerializedName;

public class PackageResolvedBase {
    @SerializedName("version")
    private final String fileFormatVersion;

    protected PackageResolvedBase(String fileFormatVersion) {
        this.fileFormatVersion = fileFormatVersion;
    }

    public String getFileFormatVersion() {
        return fileFormatVersion;
    }
}
