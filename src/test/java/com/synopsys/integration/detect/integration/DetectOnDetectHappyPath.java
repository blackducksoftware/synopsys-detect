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
package com.synopsys.integration.detect.integration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.detect.Application;
import com.synopsys.integration.detect.battery.docker.integration.BlackDuckAssertions;
import com.synopsys.integration.detect.battery.docker.integration.BlackDuckTestConnection;
import com.synopsys.integration.detect.battery.docker.util.DetectCommandBuilder;

@Tag("integration")
public class DetectOnDetectHappyPath {
    @Test
    public void testBomCreatedInBlackDuck() throws Exception {
        BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
        BlackDuckAssertions projectUtil = blackDuckTestConnection.projectVersionAssertions("synopsys-detect-junit", "happy-path");

        DetectCommandBuilder detectCommandBuilder = new DetectCommandBuilder();
        detectCommandBuilder.projectNameVersion(projectUtil.getProjectNameVersion());
        detectCommandBuilder.connectToBlackDuck(blackDuckTestConnection);
        detectCommandBuilder.waitForResults();

        projectUtil.emptyOnBlackDuck();

        Application.setShouldExit(false);
        Application.main(detectCommandBuilder.buildArguments());

        projectUtil.hasCodeLocations("synopsys-detect/synopsys-detect-junit/happy-path scan",
            "synopsys-detect-junit/happy-path/detectable/com.synopsys.integration/detectable/7.2.0-SNAPSHOT gradle/bom",
            "synopsys-detect-junit/happy-path/com.synopsys.integration/synopsys-detect/7.2.0-SNAPSHOT gradle/bom",
            "synopsys-detect-junit/happy-path/common/com.synopsys.integration/common/7.2.0-SNAPSHOT gradle/bom",
            "synopsys-detect-junit/happy-path/common-test/com.synopsys.integration/common-test/7.2.0-SNAPSHOT gradle/bom",
            "synopsys-detect-junit/happy-path/configuration/com.synopsys.integration/configuration/7.2.0-SNAPSHOT gradle/bom",
            "synopsys-detect-junit/happy-path/detector/com.synopsys.integration/detector/7.2.0-SNAPSHOT gradle/bom");

        projectUtil.hasComponents(Bds.of("jackson-core").toSet());
    }

}
