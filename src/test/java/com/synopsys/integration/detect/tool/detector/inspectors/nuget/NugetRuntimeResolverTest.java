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

public class NugetRuntimeResolverTest {
    @Test
    public void isRuntimeAvailableTest() throws DetectableException {
        List<String> dotnetRuntimes = Arrays.asList(
            "Microsoft.AspNetCore.All 2.1.18 [/usr/local/share/dotnet/shared/Microsoft.AspNetCore.All]",
            "Microsoft.AspNetCore.App 2.1.18 [/usr/local/share/dotnet/shared/Microsoft.AspNetCore.App]",
            "Microsoft.NETCore.App 2.1.18 [/usr/local/share/dotnet/shared/Microsoft.NETCore.App]",
            "Microsoft.NETCore.App 3.1.4 [/usr/local/share/dotnet/shared/Microsoft.NETCore.App]"
        );

        NugetRuntimeResolver nugetRuntimeResolver = Mockito.mock(NugetRuntimeResolver.class);
        Mockito.when(nugetRuntimeResolver.listAvailableDotNetRuntimes()).thenReturn(dotnetRuntimes);
        Mockito.when(nugetRuntimeResolver.isRuntimeAvailable(Mockito.anyString())).thenCallRealMethod();
        Mockito.when(nugetRuntimeResolver.isRuntimeAvailable(Mockito.anyInt(), Mockito.anyInt())).thenCallRealMethod();

        assertAvailability(Assert::assertTrue, nugetRuntimeResolver, 2, 1);
        assertTrue("Expected 2.1 runtime to be available when passed as a semanticVersion string", nugetRuntimeResolver.isRuntimeAvailable("2.1"));
        assertAvailability(Assert::assertTrue, nugetRuntimeResolver, 3, 1);

        assertAvailability(Assert::assertFalse, nugetRuntimeResolver, 4, 0);
        assertFalse("Expected 4.0 runtime not to be available when passed as a semanticVersion string", nugetRuntimeResolver.isRuntimeAvailable("4.0"));
        assertAvailability(Assert::assertFalse, nugetRuntimeResolver, 2, 2);
    }

    private void assertAvailability(BiConsumer<String, Boolean> assertion, NugetRuntimeResolver nugetRuntimeResolver, Integer majorVersion, Integer minorVersion) throws DetectableException {
        boolean isVersionAvailable = nugetRuntimeResolver.isRuntimeAvailable(majorVersion, minorVersion);
        assertion.accept(String.format("Different runtime availability expected for %d.%d runtime", majorVersion, minorVersion), isVersionAvailable);
    }
}
