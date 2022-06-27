package com.synopsys.integration.detectable.detectables.swift.lock.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.swift.lock.data.PackageState;
import com.synopsys.integration.detectable.detectables.swift.lock.data.ResolvedPackage;
import com.synopsys.integration.detectable.detectables.swift.lock.data.v2.PackageResolvedV2;
import com.synopsys.integration.log.BufferedIntLogger;
import com.synopsys.integration.log.LogLevel;

class PackageResolvedDataCheckerTest {

    @Test
    void logUnknownPackageTypesV1() {
        BufferedIntLogger logger = new BufferedIntLogger();
        PackageResolvedDataChecker packageResolvedDataChecker = new PackageResolvedDataChecker(logger);

        PackageResolvedV2 packageResolved = new PackageResolvedV2(
            "2",
            Collections.singletonList(
                ResolvedPackage.version1(
                    "package-name",
                    "https://example.com",
                    new PackageState(null, "628cf20632d65d3a3e90ae8aaf52bce596d7ad8f", "1.2.3")
                )
            )
        );

        packageResolvedDataChecker.logUnknownPackageTypes(packageResolved);
        List<String> outputList = logger.getOutputList(LogLevel.WARN);
        assertEquals(0, outputList.size(), "Expected no issues in V1 data format");
    }

    @Test
    void logUnknownPackageTypesV2() {
        BufferedIntLogger logger = new BufferedIntLogger();
        PackageResolvedDataChecker packageResolvedDataChecker = new PackageResolvedDataChecker(logger);

        PackageResolvedV2 packageResolved = new PackageResolvedV2(
            "2",
            Arrays.asList(
                ResolvedPackage.version2(
                    "package-name",
                    "https://example.com",
                    "remoteSourceControl",
                    new PackageState(null, "628cf20632d65d3a3e90ae8aaf52bce596d7ad8f", "1.2.3")
                ),
                ResolvedPackage.version2(
                    "package-name",
                    "https://example.com",
                    "some-path-type",
                    new PackageState(null, "628cf20632d65d3a3e90ae8aaf52bce596d7ad8f", "1.2.3")
                )
            )
        );

        packageResolvedDataChecker.logUnknownPackageTypes(packageResolved);
        List<String> outputList = logger.getOutputList(LogLevel.WARN);
        assertEquals(1, outputList.size(), "Expected one issue from 'some-path-type' not being a known type.");
    }
}