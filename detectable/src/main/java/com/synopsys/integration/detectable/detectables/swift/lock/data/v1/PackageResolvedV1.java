package com.synopsys.integration.detectable.detectables.swift.lock.data.v1;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.detectable.detectables.swift.lock.data.PackageResolvedBase;

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
