package com.synopsys.integration.detect.battery.detector;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.util.DetectorBatteryTestRunner;
import com.synopsys.integration.detect.configuration.DetectProperties;

@Tag("battery")
public class PipBattery {
    @Test
    void lock() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("pip-cli");
        test.sourceDirectoryNamed("linux-pip");
        test.sourceFileNamed("setup.py");
        test.executableFromResourceFiles(DetectProperties.DETECT_PYTHON_PATH.getProperty(), "pip-name.xout", "pip-inspector.xout");
        test.git("https://github.com/nvbn/thefuck.git", "master");
        test.expectBdioResources();
        test.run();
        //detect.pip.requirements.path = requirements.txt
    }

    @Test
    void pipenv_cli() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("pipenv-cli");
        test.sourceDirectoryNamed("pipenv-cli-django");
        test.sourceFileNamed("Pipfile.lock");
        test.sourceFileNamed("Pipfile");
        test.executable(DetectProperties.DETECT_PYTHON_PATH.getProperty(), "jpadilla/django-project-template", "");
        test.executableFromResourceFiles(DetectProperties.DETECT_PIPENV_PATH.getProperty(), "pip-freeze.xout", "pipenv-graph.xout");
        test.git("https://github.com/jpadilla/django-project-template.git", "master");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void pipenv_cli_projectonly() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("pipenv-cli-projectonly");
        test.sourceDirectoryNamed("pipenv-cli-projectonly");
        test.sourceFileNamed("Pipfile.lock");
        test.sourceFileNamed("Pipfile");
        test.executable(DetectProperties.DETECT_PYTHON_PATH.getProperty(), "django-debug-toolbar", "2.0");
        test.executableFromResourceFiles(DetectProperties.DETECT_PIPENV_PATH.getProperty(), "pip-freeze.xout", "pipenv-graph.xout");
        test.property(DetectProperties.DETECT_PIP_ONLY_PROJECT_TREE.getProperty(), "true");
        test.property(DetectProperties.DETECT_PIP_PROJECT_NAME.getProperty(), "django-debug-toolbar");
        test.property(DetectProperties.DETECT_PIP_PROJECT_VERSION_NAME.getProperty(), "2.0");
        test.git("https://github.com/jpadilla/django-project-template.git", "master");
        test.expectBdioResources();
        test.run();
    }
}

