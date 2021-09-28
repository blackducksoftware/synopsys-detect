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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.configuration.help.DetectArgumentState;
import com.synopsys.integration.detect.configuration.help.DetectArgumentStateParser;
import com.synopsys.integration.detect.workflow.airgap.AirGapType;
import com.synopsys.integration.detect.workflow.airgap.AirGapTypeDecider;

public class AirGapParsedValueTests {

    DetectArgumentStateParser parser = new DetectArgumentStateParser();

    @Test
    public void fullAirGap() {
        String[] args = new String[] { "-z", "FULL" };
        DetectArgumentState state = parser.parseArgs(args);
        AirGapType type = new AirGapTypeDecider().decide(state);
        Assertions.assertEquals(AirGapType.FULL, type);
    }

    @Test
    public void noDocker() {
        String[] args = new String[] { "-z", "NO_DOCKER" };
        DetectArgumentState state = parser.parseArgs(args);
        AirGapType type = new AirGapTypeDecider().decide(state);
        Assertions.assertEquals(AirGapType.NO_DOCKER, type);
    }

    @Test
    public void defaultIsFull() {
        String[] args = new String[] { "-z" };
        DetectArgumentState state = parser.parseArgs(args);
        AirGapType type = new AirGapTypeDecider().decide(state);
        Assertions.assertEquals(AirGapType.FULL, type);
    }
}
