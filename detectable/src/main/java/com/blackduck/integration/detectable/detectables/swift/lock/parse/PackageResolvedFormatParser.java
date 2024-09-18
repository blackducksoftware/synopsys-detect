package com.blackduck.integration.detectable.detectables.swift.lock.parse;

import com.blackduck.integration.detectable.detectables.swift.lock.data.PackageResolvedBase;
import com.blackduck.integration.detectable.detectables.swift.lock.data.PackageResolvedFormat;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;

public class PackageResolvedFormatParser {
    private final Gson gson;

    public PackageResolvedFormatParser(Gson gson) {
        this.gson = gson;
    }

    public PackageResolvedFormat parseFormatFromJson(String packageResolvedContents) {
        PackageResolvedBase packageResolvedBase = gson.fromJson(packageResolvedContents, PackageResolvedBase.class);
        String version = packageResolvedBase.getFileFormatVersion();
        return parseFormatFromVersion(version);
    }

    public PackageResolvedFormat parseFormatFromVersion(@Nullable String version) {
        if (versionMatches(PackageResolvedFormat.V_1, version)) {
            return PackageResolvedFormat.V_1;
        } else if (versionMatches(PackageResolvedFormat.V_2, version)) {
            return PackageResolvedFormat.V_2;
        } else {
            return PackageResolvedFormat.UNKNOWN(version);
        }
    }

    private boolean versionMatches(PackageResolvedFormat checkedVersion, @Nullable String version) {
        return checkedVersion.getVersionString().equals(version);
    }
}
