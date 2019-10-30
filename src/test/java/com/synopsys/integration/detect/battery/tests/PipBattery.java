package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;
import com.synopsys.integration.detect.configuration.DetectProperty;

@Tag("battery")
public class PipBattery {
    @Test
    void lock() {
        final BatteryTest test = new BatteryTest("pip-cli");
        test.sourceDirectoryNamed("linux-pip");
        test.sourceFileNamed("setup.py");
        test.executableFromResourceFiles(DetectProperty.DETECT_PYTHON_PATH, "pip-name.xout", "pip-inspector.xout");
        test.git("https://github.com/nvbn/thefuck.git", "HEAD");
        test.expectBdioResources();
        test.run();
        //detect.pip.requirements.path = requirements.txt
    }
}

