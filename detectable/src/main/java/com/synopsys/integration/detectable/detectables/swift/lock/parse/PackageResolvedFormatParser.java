package com.synopsys.integration.detectable.detectables.swift.lock.parse;

import static com.synopsys.integration.detectable.detectables.swift.lock.data.PackageResolvedFormat.UNKNOWN;
import static com.synopsys.integration.detectable.detectables.swift.lock.data.PackageResolvedFormat.V_1;
import static com.synopsys.integration.detectable.detectables.swift.lock.data.PackageResolvedFormat.V_2;

import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectables.swift.lock.data.PackageResolvedBase;
import com.synopsys.integration.detectable.detectables.swift.lock.data.PackageResolvedFormat;

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
        if (versionMatches(V_1, version)) {
            return V_1;
        } else if (versionMatches(V_2, version)) {
            return V_2;
        } else {
            return UNKNOWN(version);
        }
    }

    private boolean versionMatches(PackageResolvedFormat checkedVersion, @Nullable String version) {
        return checkedVersion.getVersionString().equals(version);
    }
}
