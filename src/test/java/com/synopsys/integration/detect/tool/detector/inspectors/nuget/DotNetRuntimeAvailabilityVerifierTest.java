package com.synopsys.integration.detect.tool.detector.inspectors.nuget;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class DotNetRuntimeAvailabilityVerifierTest {
    private static final List<String> VALID_RUNTIME_STRINGS = Arrays.asList(
        "Microsoft.AspNetCore.All 2.1.18 [/usr/local/share/dotnet/shared/Microsoft.AspNetCore.All]",
        "Microsoft.AspNetCore.All 2.1.18 [/usr/local/share/dotnet/2.1.18/shared/Microsoft.AspNetCore.All]",
        "Microsoft.AspNetCore.App 2.1.18 [/usr/local/share/dotnet/shared/Microsoft.AspNetCore.App]",
        "Microsoft.AspNetCore.App 2.1.18 [/usr/local/share/dotnet_1.0.0/shared/Microsoft.AspNetCore.App]",
        "Microsoft.NETCore.App 2.1.18 [/usr/local/share/dotnet/shared/Microsoft.NETCore.App]",
        "Microsoft.NETCore.App 3.1.4 [/usr/local/share/dotnet/shared/Microsoft.NETCore.App]"
    );

    @Test
    public void isRuntimeAvailableTest() throws DetectableException {
        DotNetRuntimeFinder runtimeFinder = Mockito.mock(DotNetRuntimeFinder.class);
        Mockito.when(runtimeFinder.listAvailableRuntimes()).thenReturn(VALID_RUNTIME_STRINGS);

        DotNetRuntimeAvailabilityVerifier runtimeResolver = new DotNetRuntimeAvailabilityVerifier(runtimeFinder);
        assertAvailability(Assertions::assertTrue, runtimeResolver, 2, 1);
        assertAvailability(Assertions::assertTrue, runtimeResolver, 3, 1);
        assertTrue(runtimeResolver.isRuntimeAvailable(2, 1, 18), "Expected 2.1.18 runtime to be available when passed as an array of Integers");

        assertAvailability(Assertions::assertFalse, runtimeResolver, 4, 0);
        assertAvailability(Assertions::assertFalse, runtimeResolver, 2, 2);
        assertAvailability(Assertions::assertFalse, runtimeResolver, 1, 0);
    }

    @Test
    public void isRuntimeAvailableSemanticVersionStringTest() throws DetectableException {
        DotNetRuntimeFinder runtimeFinder = Mockito.mock(DotNetRuntimeFinder.class);
        Mockito.when(runtimeFinder.listAvailableRuntimes()).thenReturn(VALID_RUNTIME_STRINGS);

        DotNetRuntimeAvailabilityVerifier runtimeResolver = new DotNetRuntimeAvailabilityVerifier(runtimeFinder);
        assertTrue(runtimeResolver.isRuntimeAvailable("2.1.18"), "Expected 2.1.18 runtime to be available when passed as a semanticVersion string");
        assertFalse(runtimeResolver.isRuntimeAvailable("4.0"), "Expected 4.0 runtime not to be available when passed as a semanticVersion string");
    }

    private void assertAvailability(BiConsumer<Boolean, String> assertion, DotNetRuntimeAvailabilityVerifier nugetRuntimeResolver, Integer majorVersion, Integer minorVersion) throws DetectableException {
        boolean isVersionAvailable = nugetRuntimeResolver.isRuntimeAvailable(majorVersion, minorVersion);
        assertion.accept(isVersionAvailable, String.format("Different runtime availability expected for %d.%d runtime", majorVersion, minorVersion));
    }
}
