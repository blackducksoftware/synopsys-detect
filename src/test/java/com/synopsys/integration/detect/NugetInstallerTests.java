package com.synopsys.integration.detect;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detect.tool.detector.inspectors.nuget.runtime.DotNetRuntimeFinder;
import com.synopsys.integration.detect.tool.detector.inspectors.nuget.runtime.DotNetRuntimeManager;
import com.synopsys.integration.detect.tool.detector.inspectors.nuget.runtime.DotNetRuntimeParser;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class NugetInstallerTests {
    @Test
    public void installsDotNet5Inspector() throws DetectableException {
        List<String> runtimes = Arrays.asList("Microsoft.AspNetCore.App 5.0.7", "Microsoft.NETCore.App 5.0.7");
        DotNetRuntimeFinder dotNetRuntimeFinder = Mockito.mock(DotNetRuntimeFinder.class);
        Mockito.when(dotNetRuntimeFinder.listAvailableRuntimes()).thenReturn(runtimes);
        DotNetRuntimeManager dotNetRuntimeManager = new DotNetRuntimeManager(dotNetRuntimeFinder, new DotNetRuntimeParser());

        Assertions.assertTrue(dotNetRuntimeManager.isRuntimeAvailable(5));
        Assertions.assertFalse(dotNetRuntimeManager.isRuntimeAvailable(3, 1));
    }
}
