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

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.types.path.SimplePathResolver;
import com.synopsys.integration.configuration.source.MapPropertySource;
import com.synopsys.integration.configuration.source.PropertySource;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.connection.BlackDuckConfigFactory;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.rest.client.ConnectionResult;

public class BlackDuckConnectionDecisionBranch implements DecisionTree {
    private final List<PropertySource> existingPropertySources;

    public BlackDuckConnectionDecisionBranch(List<PropertySource> existingPropertySources) {
        this.existingPropertySources = existingPropertySources;
    }

    public void traverse(Interactions interactions) {
        boolean connected = false;
        boolean skipConnectionTest = false;
        BlackDuckServerDecisionBranch blackDuckServerDecisionBranch = new BlackDuckServerDecisionBranch();

        while (!connected && !skipConnectionTest) {
            blackDuckServerDecisionBranch.traverse(interactions);

            Boolean testHub = interactions.askYesOrNo("Would you like to test the Black Duck connection now?");
            if (testHub) {
                ConnectionResult connectionAttempt = null;
                try {
                    MapPropertySource interactivePropertySource = interactions.createPropertySource();
                    List<PropertySource> propertySources = new ArrayList<>(this.existingPropertySources);
                    propertySources.add(interactivePropertySource);
                    PropertyConfiguration propertyConfiguration = new PropertyConfiguration(propertySources);
                    DetectConfigurationFactory detectConfigurationFactory = new DetectConfigurationFactory(propertyConfiguration, new SimplePathResolver());
                    BlackDuckConfigFactory blackDuckConfigFactory = new BlackDuckConfigFactory(detectConfigurationFactory.createBlackDuckConnectionDetails());
                    BlackDuckServerConfig blackDuckServerConfig = blackDuckConfigFactory.createServerConfig(new SilentIntLogger());
                    connectionAttempt = blackDuckServerConfig.attemptConnection(new SilentIntLogger());
                } catch (Exception e) {
                    interactions.println("Failed to test connection.");
                    interactions.println(e.toString());
                    interactions.println("");
                }

                if (connectionAttempt != null && connectionAttempt.isSuccess()) {
                    connected = true;
                } else {
                    connected = false;
                    interactions.println("Failed to connect.");
                    if (connectionAttempt != null) {
                        interactions.println(connectionAttempt.getFailureMessage().orElse("Unknown reason."));
                    }
                    skipConnectionTest = !interactions.askYesOrNo("Would you like to retry entering Black Duck information?");
                }
            } else {
                skipConnectionTest = true;
            }
        }
    }

}
