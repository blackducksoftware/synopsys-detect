package com.blackduck.integration.detectable.detectables.swift.lock.data.v1;

import com.blackduck.integration.detectable.detectables.swift.lock.data.PackageResolvedBase;
import com.google.gson.annotations.SerializedName;

public class PackageResolvedV1 extends PackageResolvedBase {
    @SerializedName("object")
    private final ResolvedObject resolvedObject;

    public PackageResolvedV1(String fileFormatVersion, ResolvedObject resolvedObject) {
        super(fileFormatVersion);
        this.resolvedObject = resolvedObject;
    }

    public ResolvedObject getResolvedObject() {
        return resolvedObject;
    }
}
