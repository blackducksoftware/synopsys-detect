package com.synopsys.integration.detectable.detectables.swift.lock.data;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.google.gson.annotations.SerializedName;

public class ResolvedPackage {
    // 'identity' replaces 'package' in file format "2"
    @SerializedName(value = "identity", alternate = { "package" })
    private final String identity;

    // 'location' replaces 'repositoryURL' in file format "2"
    @SerializedName(value = "location", alternate = { "repositoryURL" })
    private final String location;

    @SerializedName("kind")
    @Nullable
    private final String kind;

    @SerializedName("state")
    private final PackageState packageState;

    public static ResolvedPackage version1(String packageName, String repositoryURL, PackageState packageState) {
        return new ResolvedPackage(packageName, repositoryURL, null, packageState);
    }

    public static ResolvedPackage version2(String identity, String location, String kind, PackageState packageState) {
        return new ResolvedPackage(identity, location, kind, packageState);
    }

    private ResolvedPackage(String identity, String location, @Nullable String kind, PackageState packageState) {
        this.identity = identity;
        this.location = location;
        this.kind = kind;
        this.packageState = packageState;
    }

    public Optional<String> getKind() {
        return Optional.ofNullable(kind);
    }

    public String getIdentity() {
        return identity;
    }

    public String getLocation() {
        return location;
    }

    public PackageState getPackageState() {
        return packageState;
    }
}
