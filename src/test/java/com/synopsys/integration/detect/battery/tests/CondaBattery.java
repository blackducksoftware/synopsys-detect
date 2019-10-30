package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;
import com.synopsys.integration.detect.configuration.DetectProperty;

@Tag("battery")
public class CondaBattery {
    @Test
    void lock() {
        final BatteryTest test = new BatteryTest("conda-list");
        test.sourceDirectoryNamed("linux-conda");
        test.sourceFileNamed("environment.yml");
        test.sourceFileNamed("setup.py");
        test.executableFromResourceFiles(DetectProperty.DETECT_CONDA_PATH, "conda-list.xout", "conda-info.xout");
        test.executableFromResourceFiles(DetectProperty.DETECT_PYTHON_PATH, "python-setup.xout", "python-inspector.xout");
        test.expectBdioResources();
        test.run();
    }
}

