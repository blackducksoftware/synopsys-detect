package com.synopsys.integration.detectable.detectables.conan.lockfile.parser.model;

import java.util.List;
import java.util.Optional;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.util.Stringable;

public class ConanLockfileNode extends Stringable {
    private final String path;
    private final String ref;
    private final List<Integer> requires;

    @SerializedName("build_requires")
    private final List<Integer> buildRequires;

    @SerializedName("package_id")
    private final String packageId;

    @SerializedName("prev")
    private final String packageRevision;

    public ConanLockfileNode(String path, String ref, List<Integer> requires, List<Integer> buildRequires, String packageId, String packageRevision) {
        this.path = path;
        this.ref = ref;
        this.requires = requires;
        this.buildRequires = buildRequires;
        this.packageId = packageId;
        this.packageRevision = packageRevision;
    }

    public Optional<String> getPath() {
        return Optional.ofNullable(path);
    }

    public Optional<String> getRef() {
        return Optional.ofNullable(ref);
    }

    public Optional<List<Integer>> getRequires() {
        return Optional.ofNullable(requires);
    }

    public Optional<List<Integer>> getBuildRequires() {
        return Optional.ofNullable(buildRequires);
    }

    public Optional<String> getPackageId() {
        return Optional.ofNullable(packageId);
    }

    public Optional<String> getPackageRevision() {
        return Optional.ofNullable(packageRevision);
    }
}
