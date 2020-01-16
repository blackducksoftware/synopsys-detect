package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;
import com.synopsys.integration.detect.configuration.DetectProperties;

@Tag("battery")
public class PipBattery {
    @Test
    void lock() {
        final BatteryTest test = new BatteryTest("pip-cli");
        test.sourceDirectoryNamed("linux-pip");
        test.sourceFileNamed("setup.py");
        test.executableFromResourceFiles(DetectProperties.Companion.getDETECT_PYTHON_PATH(), "pip-name.xout", "pip-inspector.xout");
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
        test.executable(DetectProperties.Companion.getDETECT_PYTHON_PATH(), "battery-pipenv-project-name", "battery-pipenv-project-version");
        test.executableFromResourceFiles(DetectProperties.Companion.getDETECT_PIPENV_PATH(), "pip-freeze.xout", "pipenv-graph.xout");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void pipenv_cli_projectonly() {
        final BatteryTest test = new BatteryTest("pipenv-cli-projectonly");
        test.sourceDirectoryNamed("pipenv-cli-projectonly");
        test.sourceFileNamed("Pipfile.lock");
        test.sourceFileNamed("Pipfile");
        test.executable(DetectProperties.Companion.getDETECT_PYTHON_PATH(), "battery-pipenv-project-name", "battery-pipenv-project-version");
        test.executableFromResourceFiles(DetectProperties.Companion.getDETECT_PIPENV_PATH(), "pip-freeze.xout", "pipenv-graph.xout");
        test.property(DetectProperties.Companion.getDETECT_PIP_ONLY_PROJECT_TREE(), "true");
        test.property(DetectProperties.Companion.getDETECT_PIP_PROJECT_NAME(), "lime");
        test.property(DetectProperties.Companion.getDETECT_PIP_PROJECT_VERSION_NAME(), "0.1.1.33");
        test.expectBdioResources();
        test.run();
    }
}

