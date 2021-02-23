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
package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;
import com.synopsys.integration.detect.configuration.DetectProperties;

@Tag("battery")
public class SbtBattery {
    @Test
    void resolutioncache() {
        final BatteryTest test = new BatteryTest("sbt-resolutioncache");
        test.sourceDirectoryNamed("linux-sbt");
        test.sourceFileNamed("build.sbt");
        test.sourceFolderFromExpandedResource("target");
        test.git("https://github.com/sbt/sbt-bintray.git", "master");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void dependencyGraphPlugin() {
        final BatteryTest test = new BatteryTest("sbt-dependencygraph-plugin");
        test.sourceDirectoryNamed("sbt-dependencygraph-plugin");
        test.sourceFileNamed("build.sbt");
        test.executableFromResourceFiles(DetectProperties.DETECT_SBT_PATH.getProperty(), "sbt-plugins.xout", "sbt-dependencyTree.xout");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void coursierPlugin() {
        final BatteryTest test = new BatteryTest("sbt-coursier-plugin");
        test.sourceDirectoryNamed("sbt-coursier-plugin");
        test.sourceFileNamed("build.sbt");
        test.executableFromResourceFiles(DetectProperties.DETECT_SBT_PATH.getProperty(), "sbt-plugins.xout", "sbt-dependencyTree.xout");
        test.expectBdioResources();
        test.run();
    }
}

