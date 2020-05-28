package com.synopsys.integration.detect.tool.detector.inspectors.nuget;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class DotNetRuntimeAvailabilityVerifierTest {
    private static final List<String> TEST_RUNTIME_STRINGS = Arrays.asList(
        "Microsoft.AspNetCore.All 2.1.18 [/usr/local/share/dotnet/shared/Microsoft.AspNetCore.All]",
        "Microsoft.AspNetCore.App 2.1.18 [/usr/local/share/dotnet/shared/Microsoft.AspNetCore.App]",
        "Microsoft.NETCore.App 2.1.18 [/usr/local/share/dotnet/shared/Microsoft.NETCore.App]",
        "Microsoft.NETCore.App 3.1.4 [/usr/local/share/dotnet/shared/Microsoft.NETCore.App]"
    );

    @Test
    public void isRuntimeAvailableTest() throws DetectableException {
        DotNetRuntimeFinder runtimeFinder = Mockito.mock(DotNetRuntimeFinder.class);
        Mockito.when(runtimeFinder.listAvailableRuntimes()).thenReturn(TEST_RUNTIME_STRINGS);

        DotNetRuntimeAvailabilityVerifier runtimeResolver = new DotNetRuntimeAvailabilityVerifier(runtimeFinder);
        assertAvailability(Assert::assertTrue, runtimeResolver, 2, 1);
        assertAvailability(Assert::assertTrue, runtimeResolver, 3, 1);
        assertTrue("Expected 2.1.18 runtime to be available when passed as an array of Integers", runtimeResolver.isRuntimeAvailable(2, 1, 18));

        assertAvailability(Assert::assertFalse, runtimeResolver, 4, 0);
        assertAvailability(Assert::assertFalse, runtimeResolver, 2, 2);
    }

    @Test
    public void isRuntimeAvailableSemanticVersionStringTest() throws DetectableException {
        DotNetRuntimeFinder runtimeFinder = Mockito.mock(DotNetRuntimeFinder.class);
        Mockito.when(runtimeFinder.listAvailableRuntimes()).thenReturn(TEST_RUNTIME_STRINGS);

        DotNetRuntimeAvailabilityVerifier runtimeResolver = new DotNetRuntimeAvailabilityVerifier(runtimeFinder);
        assertTrue("Expected 2.1.18 runtime to be available when passed as a semanticVersion string", runtimeResolver.isRuntimeAvailable("2.1.18"));
        assertFalse("Expected 4.0 runtime not to be available when passed as a semanticVersion string", runtimeResolver.isRuntimeAvailable("4.0"));
    }

    private void assertAvailability(BiConsumer<String, Boolean> assertion, DotNetRuntimeAvailabilityVerifier nugetRuntimeResolver, Integer majorVersion, Integer minorVersion) throws DetectableException {
        boolean isVersionAvailable = nugetRuntimeResolver.isRuntimeAvailable(majorVersion, minorVersion);
        assertion.accept(String.format("Different runtime availability expected for %d.%d runtime", majorVersion, minorVersion), isVersionAvailable);
    }
}
