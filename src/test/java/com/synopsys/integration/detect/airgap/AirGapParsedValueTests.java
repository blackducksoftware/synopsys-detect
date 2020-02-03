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
package com.synopsys.integration.detect.airgap;

import org.junit.Assert;
import org.junit.Test;

import com.synopsys.integration.detect.help.DetectArgumentState;
import com.synopsys.integration.detect.help.DetectArgumentStateParser;
import com.synopsys.integration.detect.util.filter.DetectOverrideableFilter;
import com.synopsys.integration.detect.workflow.airgap.AirGapInspectors;

public class AirGapParsedValueTests {

    DetectArgumentStateParser parser = new DetectArgumentStateParser();

    @Test
    public void allIncluded() {
        final String[] args = new String[] { "-z", "ALL" };
        final DetectArgumentState state = parser.parseArgs(args);
        DetectOverrideableFilter filter = new DetectOverrideableFilter("", state.getParsedValue());

        Assert.assertTrue(filter.shouldInclude(AirGapInspectors.DOCKER.name()));
        Assert.assertTrue(filter.shouldInclude(AirGapInspectors.GRADLE.name()));
        Assert.assertTrue(filter.shouldInclude(AirGapInspectors.NUGET.name()));
    }

    @Test
    public void dockerNotIncluded() {
        final String[] args = new String[] { "-z", "GRADLE,NUGET" };
        final DetectArgumentState state = parser.parseArgs(args);
        DetectOverrideableFilter filter = new DetectOverrideableFilter("", state.getParsedValue());

        Assert.assertFalse(filter.shouldInclude(AirGapInspectors.DOCKER.name()));

        Assert.assertTrue(filter.shouldInclude(AirGapInspectors.GRADLE.name()));
        Assert.assertTrue(filter.shouldInclude(AirGapInspectors.NUGET.name()));
    }

    @Test
    public void onlyNugetIncluded() {
        final String[] args = new String[] { "-z", "NUGET" };
        final DetectArgumentState state = parser.parseArgs(args);
        DetectOverrideableFilter filter = new DetectOverrideableFilter("", state.getParsedValue());

        Assert.assertFalse(filter.shouldInclude(AirGapInspectors.DOCKER.name()));
        Assert.assertFalse(filter.shouldInclude(AirGapInspectors.GRADLE.name()));

        Assert.assertTrue(filter.shouldInclude(AirGapInspectors.NUGET.name()));
    }
}
