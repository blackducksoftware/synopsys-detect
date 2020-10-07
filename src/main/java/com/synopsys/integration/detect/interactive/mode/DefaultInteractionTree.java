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
package com.synopsys.integration.detect.interactive.mode;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.configuration.source.PropertySource;
import com.synopsys.integration.detect.configuration.DetectProperties;

public class DefaultInteractionTree implements InteractionTree {
    private final List<PropertySource> existingPropertySources;

    public DefaultInteractionTree(List<PropertySource> existingPropertySources) {
        this.existingPropertySources = new ArrayList<>(existingPropertySources);
    }

    public void configure(InteractiveMode interactiveMode) {
        interactiveMode.printWelcome();

        Boolean connectToHub = interactiveMode.askYesOrNo("Would you like to connect to a Black Duck server?");
        if (connectToHub) {
            BlackDuckConnectionInteractionTree blackDuckConnectionInteractionTree = new BlackDuckConnectionInteractionTree(existingPropertySources);
            blackDuckConnectionInteractionTree.configure(interactiveMode);

            Boolean customDetails = interactiveMode.askYesOrNo("Would you like to provide a project name and version to use?");
            if (customDetails) {
                interactiveMode.setPropertyFromQuestion(DetectProperties.DETECT_PROJECT_NAME.getProperty(), "What is the project name?");
                interactiveMode.setPropertyFromQuestion(DetectProperties.DETECT_PROJECT_VERSION_NAME.getProperty(), "What is the project version?");
            }
        } else {
            interactiveMode.setProperty(DetectProperties.BLACKDUCK_OFFLINE_MODE.getProperty(), "true");
        }

        Boolean scan = interactiveMode.askYesOrNo("Would you like run a CLI scan?");
        if (scan) {
            CliInteractionTree cliInteractionTree = new CliInteractionTree(connectToHub);
            cliInteractionTree.configure(interactiveMode);
        } else {
            interactiveMode.setProperty(DetectProperties.DETECT_TOOLS_EXCLUDED.getProperty(), "SIGNATURE_SCAN");
        }

        interactiveMode.saveAndEndInteractiveMode();
    }

}
