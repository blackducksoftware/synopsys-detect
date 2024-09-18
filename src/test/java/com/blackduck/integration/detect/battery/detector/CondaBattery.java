package com.blackduck.integration.detect.battery.detector;

import com.blackduck.integration.detect.battery.util.DetectorBatteryTestRunner;
import com.blackduck.integration.detect.configuration.DetectProperties;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("battery")
public class CondaBattery {
    @Test
    void lock() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("conda-list");
        test.sourceDirectoryNamed("linux-conda");
        test.sourceFileNamed("environment.yml");
        test.sourceFileNamed("setup.py");
        test.executableFromResourceFiles(DetectProperties.DETECT_CONDA_PATH, "conda-list.xout", "conda-info.xout");
        test.executableFromResourceFiles(DetectProperties.DETECT_PYTHON_PATH, "python-setup.xout", "python-inspector.xout");
        test.executableFromResourceFiles(DetectProperties.DETECT_PIP_PATH, "python-inspector.xout"); // Needs pip for version logging
        test.expectBdioResources();
        test.run();
    }
}

