package com.synopsys.integration.detectable.detectables.xcode.parse;

import java.util.Optional;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectables.xcode.data.PackageResolved;

public class PackageResolvedParser {
    private final Gson gson;

    public PackageResolvedParser(Gson gson) {
        this.gson = gson;
    }

    public Optional<PackageResolved> parsePackageResolved(String packageResolvedContents) {
        PackageResolved packageResolved = gson.fromJson(packageResolvedContents, PackageResolved.class);
        if (packageResolved == null) {
            return Optional.empty();
        }

        return Optional.of(packageResolved);
    }
}
