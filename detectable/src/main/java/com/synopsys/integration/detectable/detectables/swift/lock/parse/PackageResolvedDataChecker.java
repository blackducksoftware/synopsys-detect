package com.synopsys.integration.detectable.detectables.swift.lock.parse;

import java.util.Arrays;

import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.swift.lock.data.v2.PackageResolvedV2;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;

// The concern is Path packages and anything new Apple introduces: https://developer.apple.com/documentation/swift_packages/package/dependency/3197884-package
public class PackageResolvedDataChecker {
    private final IntLogger logger;

    protected static final String[] KNOWN_KINDS = { "remoteSourceControl" };

    public PackageResolvedDataChecker() {
        this(new Slf4jIntLogger(LoggerFactory.getLogger(PackageResolvedDataChecker.class)));
    }

    public PackageResolvedDataChecker(IntLogger logger) {
        this.logger = logger;
    }

    public void logUnknownPackageTypes(PackageResolvedV2 packageResolved) {
        packageResolved.getPackages().stream()
            .filter(packageItem -> packageItem.getKind()
                .map(foundKind -> Arrays.stream(KNOWN_KINDS)
                    .noneMatch(foundKind::equals)
                )
                .orElse(false) // It is a V1 file format. Shouldn't ever get to this point really.
            )
            .forEach(packageItem -> logger.warn(
                String.format(
                    "A package type unknown to Detect was found (%s). Processing will continue, but errors may occur.",
                    packageItem.getKind().orElse("unknown")
                )
            ));
    }
}
