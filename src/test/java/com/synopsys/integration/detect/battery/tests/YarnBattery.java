package com.synopsys.integration.detect.battery.tests;

import com.synopsys.integration.detect.battery.BatteryTest;
import com.synopsys.integration.detect.configuration.DetectProperty;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("battery")
public class YarnBattery {
    @Test
    void lock() {
        final BatteryTest test = new BatteryTest("yarn-lock");
        test.sourceDirectoryNamed("linux-yarn");
        test.sourceFileFromResource("yarn.lock");
        test.executableFromResourceFiles(DetectProperty.DETECT_YARN_PATH, "yarn-list.xout");
        test.git("https://github.com/babel/babel", "HEAD");
        test.expectBdioResources();
        test.run();
    }
}

