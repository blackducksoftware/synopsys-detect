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
package com.synopsys.integration.detect.interactive;

import static com.synopsys.integration.detect.configuration.DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN;
import static com.synopsys.integration.detect.configuration.DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL;
import static com.synopsys.integration.detect.configuration.DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH;

public class CliDecisionBranch implements DecisionTree {
    private final boolean connectedToBlackDuck;

    public CliDecisionBranch(boolean connectedToBlackDuck) {
        this.connectedToBlackDuck = connectedToBlackDuck;
    }

    @Override
    public void traverse(InteractivePropertySourceBuilder propertySourceBuilder, InteractiveWriter writer) {
        if (connectedToBlackDuck) {
            Boolean upload = writer.askYesOrNo("Would you like to upload CLI scan results to the Black Duck server?");
            if (!upload) {
                propertySourceBuilder.setProperty(DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN.getProperty(), "true");
            }
        }

        Boolean customScanner = writer.askYesOrNo("Would you like to provide a custom scanner?");
        if (customScanner) {
            Boolean downloadCustomScanner = writer.askYesOrNo("Would you like to download the custom scanner?");
            if (downloadCustomScanner) {
                propertySourceBuilder.setPropertyFromQuestion(DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL.getProperty(), "What is the scanner host url?");
            } else {
                propertySourceBuilder.setPropertyFromQuestion(DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH.getProperty(), "What is the location of your offline scanner?");
            }
        }
    }
}
