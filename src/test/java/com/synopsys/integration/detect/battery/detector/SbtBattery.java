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
public class SbtBattery {
    @Test
    void resolutioncache() {
        final DetectorBatteryTest test = new DetectorBatteryTest("sbt-resolutioncache");
        test.sourceDirectoryNamed("linux-sbt");
        test.sourceFileNamed("build.sbt");
        test.sourceFolderFromExpandedResource("target");
        test.git("https://github.com/sbt/sbt-bintray.git", "master");
        test.executable(DetectProperties.DETECT_SBT_PATH.getProperty(), ""); //empty == no plugins installed
        test.expectBdioResources();
        test.run();
    }

    @Test
    void dotPlugin() {
        final DetectorBatteryTest test = new DetectorBatteryTest("sbt-dot");
        test.sourceDirectoryNamed("sbt-dot");
        test.sourceFileNamed("build.sbt");
        test.addDirectlyToSourceFolderFromExpandedResource("dots");
        test.executableFromResourceFiles(DetectProperties.DETECT_SBT_PATH.getProperty(), "sbt-plugins.xout", "sbt-dependencyDot.ftl");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void dotPluginMultipleProjectNode() {
        final DetectorBatteryTest test = new DetectorBatteryTest("sbt-dot-multipleprojectnode");
        test.sourceDirectoryNamed("sbt-dot");
        test.sourceFileNamed("build.sbt");
        test.addDirectlyToSourceFolderFromExpandedResource("dots");
        test.executableFromResourceFiles(DetectProperties.DETECT_SBT_PATH.getProperty(), "sbt-plugins.xout", "sbt-dependencyDot.ftl");
        test.expectBdioResources();
        test.run();
    }
}

