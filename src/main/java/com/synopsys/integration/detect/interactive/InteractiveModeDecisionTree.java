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

    public void traverse(InteractivePropertySourceBuilder propertySourceBuilder, InteractiveWriter writer) {
        writer.println("***** Welcome to Detect Interactive Mode *****");
        writer.println();

        Boolean connectToHub = writer.askYesOrNo("Would you like to connect to a Black Duck server?");
        if (connectToHub) {
            BlackDuckConnectionDecisionBranch blackDuckConnectionDecisionBranch = new BlackDuckConnectionDecisionBranch(existingPropertySources);
            blackDuckConnectionDecisionBranch.traverse(propertySourceBuilder, writer);

            Boolean customDetails = writer.askYesOrNo("Would you like to provide a project name and version to use?");
            if (customDetails) {
                propertySourceBuilder.setPropertyFromQuestion(DetectProperties.DETECT_PROJECT_NAME.getProperty(), "What is the project name?");
                propertySourceBuilder.setPropertyFromQuestion(DetectProperties.DETECT_PROJECT_VERSION_NAME.getProperty(), "What is the project version?");
            }
        } else {
            propertySourceBuilder.setProperty(DetectProperties.BLACKDUCK_OFFLINE_MODE.getProperty(), "true");
        }

        Boolean scan = writer.askYesOrNo("Would you like run a CLI scan?");
        if (scan) {
            CliDecisionBranch cliDecisionBranch = new CliDecisionBranch(connectToHub);
            cliDecisionBranch.traverse(propertySourceBuilder, writer);
        } else {
            propertySourceBuilder.setProperty(DetectProperties.DETECT_TOOLS_EXCLUDED.getProperty(), "SIGNATURE_SCAN");
        }

        writer.println("Interactive Mode Successful!");
        writer.println();

        Boolean saveSettings = writer.askYesOrNo("Would you like to save these settings to an application.properties file?");
        if (saveSettings) {
            Boolean customName = writer.askYesOrNo("Would you like save these settings to a profile?");
            if (customName) {
                String profileName = writer.askQuestion("What is the profile name?");

                propertySourceBuilder.saveToApplicationProperties(profileName);

                writer.println();
                writer.println("In the future, to use this profile add the following option:");
                writer.println();
                writer.println("--spring.profiles.active=" + profileName);
            } else {
                propertySourceBuilder.saveToApplicationProperties();
            }
        }

        writer.promptToStartDetect();
    }

}
