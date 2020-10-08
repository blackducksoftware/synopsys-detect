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

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.configuration.source.PropertySource;
import com.synopsys.integration.detect.configuration.DetectProperties;

public class InteractiveModeDecisionTree implements DecisionTree {
    private final List<PropertySource> existingPropertySources;

    public InteractiveModeDecisionTree(List<PropertySource> existingPropertySources) {
        this.existingPropertySources = new ArrayList<>(existingPropertySources);
    }

    public void traverse(Interactions interactions) {
        interactions.printWelcome();

        Boolean connectToHub = interactions.askYesOrNo("Would you like to connect to a Black Duck server?");
        if (connectToHub) {
            BlackDuckConnectionDecisionBranch blackDuckConnectionDecisionBranch = new BlackDuckConnectionDecisionBranch(existingPropertySources);
            blackDuckConnectionDecisionBranch.traverse(interactions);

            Boolean customDetails = interactions.askYesOrNo("Would you like to provide a project name and version to use?");
            if (customDetails) {
                interactions.setPropertyFromQuestion(DetectProperties.DETECT_PROJECT_NAME.getProperty(), "What is the project name?");
                interactions.setPropertyFromQuestion(DetectProperties.DETECT_PROJECT_VERSION_NAME.getProperty(), "What is the project version?");
            }
        } else {
            interactions.setProperty(DetectProperties.BLACKDUCK_OFFLINE_MODE.getProperty(), "true");
        }

        Boolean scan = interactions.askYesOrNo("Would you like run a CLI scan?");
        if (scan) {
            CliDecisionBranch cliDecisionBranch = new CliDecisionBranch(connectToHub);
            cliDecisionBranch.traverse(interactions);
        } else {
            interactions.setProperty(DetectProperties.DETECT_TOOLS_EXCLUDED.getProperty(), "SIGNATURE_SCAN");
        }

        interactions.saveAndEndInteractiveMode();
    }

}
