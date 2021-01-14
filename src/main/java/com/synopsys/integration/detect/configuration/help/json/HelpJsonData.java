/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detect.configuration.help.json;

import java.util.ArrayList;
import java.util.List;

public class HelpJsonData {
    private List<HelpJsonExitCode> exitCodes = new ArrayList<>();
    private List<HelpJsonDetector> buildDetectors = new ArrayList<>();
    private List<HelpJsonDetector> buildlessDetectors = new ArrayList<>();
    private List<HelpJsonOption> options = new ArrayList<>();
    private List<HelpJsonDetectorStatusCode> detectorStatusCodes = new ArrayList<>();

    public List<HelpJsonExitCode> getExitCodes() {
        return exitCodes;
    }

    public void setExitCodes(final List<HelpJsonExitCode> exitCodes) {
        this.exitCodes = exitCodes;
    }

    public List<HelpJsonDetector> getBuildDetectors() {
        return buildDetectors;
    }

    public void setBuildDetectors(final List<HelpJsonDetector> buildDetectors) {
        this.buildDetectors = buildDetectors;
    }

    public List<HelpJsonDetector> getBuildlessDetectors() {
        return buildlessDetectors;
    }

    public void setBuildlessDetectors(final List<HelpJsonDetector> buildlessDetectors) {
        this.buildlessDetectors = buildlessDetectors;
    }

    public List<HelpJsonOption> getOptions() {
        return options;
    }

    public void setOptions(final List<HelpJsonOption> options) {
        this.options = options;
    }

    public List<HelpJsonDetectorStatusCode> getDetectorStatusCodes() {
        return detectorStatusCodes;
    }

    public void setDetectorStatusCodes(final List<HelpJsonDetectorStatusCode> detectorStatusCodes) {
        this.detectorStatusCodes = detectorStatusCodes;
    }
}
