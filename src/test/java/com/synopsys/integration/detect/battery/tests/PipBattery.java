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
        test.git("https://github.com/nvbn/thefuck.git", "master");
        test.expectBdioResources();
        test.run();
        //detect.pip.requirements.path = requirements.txt
    }

    @Test
    void pipenv_cli() {
        final BatteryTest test = new BatteryTest("pipenv-cli");
        test.sourceDirectoryNamed("battery-pipenv");
        test.sourceFileNamed("Pipfile.lock");
        test.sourceFileNamed("Pipfile");
        test.executable(DetectProperty.DETECT_PYTHON_PATH, "battery-pipenv-project-name", "battery-pipenv-project-version");
        test.executableFromResourceFiles(DetectProperty.DETECT_PIPENV_PATH, "pip-freeze.xout", "pipenv-graph.xout");
        test.expectBdioResources();
        test.run();
    }
}

