package com.synopsys.integration.detect.interactive.mode;

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

public class BlackDuckConnectionInteractionTree implements InteractionTree {
    private final List<PropertySource> existingPropertySources;

    public BlackDuckConnectionInteractionTree(List<PropertySource> existingPropertySources) {
        this.existingPropertySources = existingPropertySources;
    }

    public void configure(InteractiveMode interactiveMode) {
        boolean connected = false;
        boolean skipConnectionTest = false;
        BlackDuckServerInteractionTree blackDuckServerInteractionTree = new BlackDuckServerInteractionTree();

        while (!connected && !skipConnectionTest) {
            blackDuckServerInteractionTree.configure(interactiveMode);

            Boolean testHub = interactiveMode.askYesOrNo("Would you like to test the Black Duck connection now?");
            if (testHub) {
                ConnectionResult connectionAttempt = null;
                try {
                    MapPropertySource interactivePropertySource = interactiveMode.toPropertySource();
                    List<PropertySource> propertySources = new ArrayList<>(this.existingPropertySources);
                    propertySources.add(interactivePropertySource);
                    PropertyConfiguration propertyConfiguration = new PropertyConfiguration(propertySources);
                    DetectConfigurationFactory detectConfigurationFactory = new DetectConfigurationFactory(propertyConfiguration, new SimplePathResolver());
                    BlackDuckConfigFactory blackDuckConfigFactory = new BlackDuckConfigFactory(detectConfigurationFactory.createBlackDuckConnectionDetails());
                    BlackDuckServerConfig blackDuckServerConfig = blackDuckConfigFactory.createServerConfig(new SilentIntLogger());
                    connectionAttempt = blackDuckServerConfig.attemptConnection(new SilentIntLogger());
                } catch (Exception e) {
                    interactiveMode.println("Failed to test connection.");
                    interactiveMode.println(e.toString());
                    interactiveMode.println("");
                }

                if (connectionAttempt != null && connectionAttempt.isSuccess()) {
                    connected = true;
                } else {
                    connected = false;
                    interactiveMode.println("Failed to connect.");
                    if (connectionAttempt != null) {
                        interactiveMode.println(connectionAttempt.getFailureMessage().orElse("Unknown reason."));
                    }
                    skipConnectionTest = !interactiveMode.askYesOrNo("Would you like to retry entering Black Duck information?");
                }
            } else {
                skipConnectionTest = true;
            }
        }
    }

}
