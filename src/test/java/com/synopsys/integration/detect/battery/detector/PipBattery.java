/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.battery.detector;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.util.DetectorBatteryTest;
import com.synopsys.integration.detect.configuration.DetectProperties;

@Tag("battery")
public class PipBattery {
    @Test
    void lock() {
        final DetectorBatteryTest test = new DetectorBatteryTest("pip-cli");
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
        final DetectorBatteryTest test = new DetectorBatteryTest("pipenv-cli");
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
        final DetectorBatteryTest test = new DetectorBatteryTest("pipenv-cli-projectonly");
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

