package com.synopsys.integration.detectable.detectables.xcode.parse;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectables.xcode.data.PackageResolved;
import com.synopsys.integration.detectable.detectables.xcode.process.PackageResolvedFormatChecker;

public class PackageResolvedParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Gson gson;
    private final PackageResolvedFormatChecker packageResolvedFormatChecker;

    public PackageResolvedParser(Gson gson, PackageResolvedFormatChecker packageResolvedFormatChecker) {
        this.gson = gson;
        this.packageResolvedFormatChecker = packageResolvedFormatChecker;
    }

    public Optional<PackageResolved> parsePackageResolved(String packageResolvedContents) {
        PackageResolved packageResolved = gson.fromJson(packageResolvedContents, PackageResolved.class);
        if (packageResolved == null) {
            return Optional.empty();
        }

        packageResolvedFormatChecker.checkForVersionCompatibility(
            packageResolved,
            (fileFormatVersion, knownVersions) -> logger.warn(String.format(
                "The format version of Package.resolved (%s) is unknown to Detect, but will attempt to parse anyway. Known format versions are (%s).",
                fileFormatVersion,
                StringUtils.join(knownVersions, ", ")
            ))
        );

        return Optional.of(packageResolved);
    }
}
