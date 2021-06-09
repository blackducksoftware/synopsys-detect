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

import com.synopsys.integration.detect.battery.util.DetectorBattery;

@Tag("battery")
public class RubygemsBattery {
    @Test
    void lock() {
        DetectorBattery test = new DetectorBattery("rubygems-lock");
        test.sourceDirectoryNamed("linux-rubygems");
        test.sourceFileFromResource("Gemfile.lock");
        test.git("https://github.com/BlackDuckCoPilot/example-rubygems-travis", "master");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void gemfileGeneratingCircularDependencies() {
        DetectorBattery test = new DetectorBattery("rubygems-circular-lock");
        test.sourceDirectoryNamed("jquery-multiselect-rails");
        test.sourceFileFromResource("Gemfile.lock");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void gemfileVersionLessDependencies() {
        DetectorBattery test = new DetectorBattery("rubygems-versionless-lock");
        test.sourceDirectoryNamed("rails");
        test.sourceFileFromResource("Gemfile.lock");
        test.expectBdioResources();
        test.run();
    }

}

