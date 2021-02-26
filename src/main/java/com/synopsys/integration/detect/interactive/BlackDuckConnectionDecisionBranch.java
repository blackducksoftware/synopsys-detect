/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.connection.BlackDuckConfigFactory;
import com.synopsys.integration.detect.lifecycle.boot.product.BlackDuckConnectivityChecker;
import com.synopsys.integration.detect.lifecycle.boot.product.BlackDuckConnectivityResult;
import com.synopsys.integration.log.SilentIntLogger;

public class BlackDuckConnectionDecisionBranch implements DecisionTree {
    public static final String SHOULD_TEST_CONNECTION = "Would you like to test the Black Duck connection now?";
    public static final String SHOULD_RETRY_CONNECTION = "Would you like to retry entering Black Duck information?";
    private final DetectInfo detectInfo;
    private final List<PropertySource> existingPropertySources;
    private final BlackDuckConnectivityChecker blackDuckConnectivityChecker;

    public BlackDuckConnectionDecisionBranch(DetectInfo detectInfo, BlackDuckConnectivityChecker blackDuckConnectivityChecker, List<PropertySource> existingPropertySources) {
        this.detectInfo = detectInfo;
        this.existingPropertySources = existingPropertySources;
        this.blackDuckConnectivityChecker = blackDuckConnectivityChecker;
    }

    @Override
    public void traverse(InteractivePropertySourceBuilder propertySourceBuilder, InteractiveWriter writer) {
        boolean shouldReconfigureServer = true;
        BlackDuckConnectivityResult blackDuckConnectivityResult = BlackDuckConnectivityResult.failure("Connection has yet to be attempted.");
        BlackDuckServerDecisionBranch blackDuckServerDecisionBranch = new BlackDuckServerDecisionBranch();

        while (!blackDuckConnectivityResult.isSuccessfullyConnected() && shouldReconfigureServer) {
            blackDuckServerDecisionBranch.traverse(propertySourceBuilder, writer);

            Boolean testConnection = writer.askYesOrNo(SHOULD_TEST_CONNECTION);
            if (testConnection) {
                try {
                    MapPropertySource interactivePropertySource = propertySourceBuilder.build();
                    List<PropertySource> propertySources = new ArrayList<>(this.existingPropertySources);
                    propertySources.add(0, interactivePropertySource);

                    PropertyConfiguration propertyConfiguration = new PropertyConfiguration(propertySources);
                    DetectConfigurationFactory detectConfigurationFactory = new DetectConfigurationFactory(propertyConfiguration, new SimplePathResolver());
                    BlackDuckConfigFactory blackDuckConfigFactory = new BlackDuckConfigFactory(detectInfo, detectConfigurationFactory.createBlackDuckConnectionDetails());
                    BlackDuckServerConfig blackDuckServerConfig = blackDuckConfigFactory.createServerConfig(new SilentIntLogger());

                    blackDuckConnectivityResult = blackDuckConnectivityChecker.determineConnectivity(blackDuckServerConfig);
                } catch (Exception e) {
                    blackDuckConnectivityResult = BlackDuckConnectivityResult.failure("Failed to test connection. " + System.lineSeparator() + e.toString());
                }

                if (!blackDuckConnectivityResult.isSuccessfullyConnected()) {
                    writer.println(blackDuckConnectivityResult.getFailureReason());
                    shouldReconfigureServer = writer.askYesOrNo(SHOULD_RETRY_CONNECTION);
                }
            } else {
                shouldReconfigureServer = false;
            }
        }
    }

}
