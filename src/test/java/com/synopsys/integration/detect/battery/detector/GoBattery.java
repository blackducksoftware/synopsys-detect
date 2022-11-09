package com.synopsys.integration.detect.battery.detector;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.util.DetectorBatteryTestRunner;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detectable.detectables.go.gomod.GoModDependencyType;

@Tag("battery")
public class GoBattery {
    @Test
    void lock() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("dep-lock");
        test.sourceDirectoryNamed("rooms");
        test.sourceFileFromResource("Gopkg.lock");
        test.git("https://github.com/thenrich/rooms", "master");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void conf() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("go_vndr-lock");
        test.sourceDirectoryNamed("linux-vndr");
        test.sourceFileFromResource("vendor.conf");
        test.git("https://github.com/moby/moby.git", "master");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void mod() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("go-mod");
        test.executableFromResourceFiles(DetectProperties.DETECT_GO_PATH, "go-version.xout", "go-list.xout", "go-list-u-json.xout", "go-mod-graph.xout", "go-mod-get-main.xout", "go-mod-list-directs.xout", "go-mod-why.xout", "go-mod-why.xout");
        test.sourceDirectoryNamed("source");
        test.sourceFileFromResource("go.mod");
        test.property(DetectProperties.DETECT_GO_MOD_DEPENDENCY_TYPES_EXCLUDED, GoModDependencyType.UNUSED.name());
        test.expectBdioResources();
        test.run();
    }
}

