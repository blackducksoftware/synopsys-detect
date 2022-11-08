package com.synopsys.integration.detectable.detectables.swift.lock.parse;

import java.util.Optional;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectables.swift.lock.data.PackageResolvedBase;

public class PackageResolvedParser {
    private final Gson gson;

    public PackageResolvedParser(Gson gson) {
        this.gson = gson;
    }

    public <T extends PackageResolvedBase> Optional<T> parsePackageResolved(String packageResolvedContents, Class<T> packageResolvedClass) {
        T packageResolved = gson.fromJson(packageResolvedContents, packageResolvedClass);
        if (packageResolved == null) {
            return Optional.empty();
        }

        return Optional.of(packageResolved);
    }
}
