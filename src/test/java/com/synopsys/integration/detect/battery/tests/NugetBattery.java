package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;
import com.synopsys.integration.detect.configuration.DetectProperty;

@Tag("battery")
public class NugetBattery {
    @Test
    void dotnetcore() {
        final BatteryTest test = new BatteryTest("nuget-dotnetcore");
        test.sourceDirectoryNamed("windows-nuget4");
        test.sourceFileNamed("example.sln");
        test.executableThatCopiesFiles(DetectProperty.DETECT_DOTNET_PATH, "NUGET-0")
            .onWindows(5, "")
            .onLinux(3, "--output_directory=");
        test.git("https://github.com/GaProgMan/dwCheckApi.git", "master");
        test.expectBdioResources();
        test.run();
    }
}
