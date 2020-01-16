package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;
import com.synopsys.integration.detect.configuration.DetectProperties;

@Tag("battery")
public class CondaBattery {
    @Test
    void lock() {
        final BatteryTest test = new BatteryTest("conda-list");
        test.sourceDirectoryNamed("linux-conda");
        test.sourceFileNamed("environment.yml");
        test.sourceFileNamed("setup.py");
        test.executableFromResourceFiles(DetectProperties.Companion.getDETECT_CONDA_PATH(), "conda-list.xout", "conda-info.xout");
        test.executableFromResourceFiles(DetectProperties.Companion.getDETECT_PYTHON_PATH(), "python-setup.xout", "python-inspector.xout");
        test.expectBdioResources();
        test.run();
    }
}

