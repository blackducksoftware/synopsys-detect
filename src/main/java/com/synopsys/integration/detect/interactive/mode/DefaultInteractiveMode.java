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

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.types.path.SimplePathResolver;
import com.synopsys.integration.configuration.source.MapPropertySource;
import com.synopsys.integration.configuration.source.PropertySource;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.connection.BlackDuckConfigFactory;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.rest.client.ConnectionResult;

public class DefaultInteractiveMode extends InteractiveMode {

    private final List<PropertySource> existingPropertySources;

    public DefaultInteractiveMode(List<PropertySource> existingPropertySources) {
        this.existingPropertySources = new ArrayList<>(existingPropertySources);
    }

    @Override
    public void configure() {
        printWelcome();

        final Boolean connectToHub = askYesOrNo("Would you like to connect to a Black Duck server?");
        if (connectToHub) {
            boolean connected = false;
            boolean skipConnectionTest = false;
            while (!connected && !skipConnectionTest) {
                setPropertyFromQuestion(DetectProperties.BLACKDUCK_URL.getProperty(), "What is the Black Duck server url?");

                println("You can now configure Black Duck with either an API token -OR- a username and password. The API token must already exist on the Black Duck server, but it is the preferred approach to configure your connection.");
                final Boolean useApiToken = askYesOrNo("Would you like to use an existing API token?");
                if (useApiToken) {
                    setPropertyFromQuestion(DetectProperties.BLACKDUCK_API_TOKEN.getProperty(), "What is the API token?");
                } else {
                    setPropertyFromQuestion(DetectProperties.BLACKDUCK_USERNAME.getProperty(), "What is the username?");

                    final Boolean setHubPassword = askYesOrNoWithMessage("Would you like to set the password?",
                        "WARNING: If you choose to save the settings, this password will be stored in plain text. You can set this password as an environment variable BLACKDUCK_HUB_PASSWORD.");
                    if (setHubPassword) {
                        setPropertyFromSecretQuestion(DetectProperties.BLACKDUCK_PASSWORD.getProperty(), "What is the password?");
                    }
                }

                final Boolean useProxy = askYesOrNo("Would you like to configure a proxy?");
                if (useProxy) {
                    setPropertyFromQuestion(DetectProperties.BLACKDUCK_PROXY_HOST.getProperty(), "What is the proxy host?");
                    setPropertyFromQuestion(DetectProperties.BLACKDUCK_PROXY_PORT.getProperty(), "What is the proxy port?");
                    setPropertyFromQuestion(DetectProperties.BLACKDUCK_PROXY_USERNAME.getProperty(), "What is the Black Duck username?");
                    final Boolean setHubPassword = askYesOrNoWithMessage("Would you like to set the Black Duck password?",
                        "WARNING: If you choose to save the settings, this password will be stored in plain text. You can set this password as an environment variable BLACKDUCK_PROXY_PASSWORD.");
                    if (setHubPassword) {
                        setPropertyFromSecretQuestion(DetectProperties.BLACKDUCK_PROXY_PASSWORD.getProperty(), "What is the proxy password?");
                    }
                    final Boolean useNtlmProxy = askYesOrNo("Do you use a ntlm proxy?");
                    if (useNtlmProxy) {
                        setPropertyFromQuestion(DetectProperties.BLACKDUCK_PROXY_NTLM_DOMAIN.getProperty(), "What is the ntlm proxy domain?");
                        setPropertyFromQuestion(DetectProperties.BLACKDUCK_PROXY_NTLM_WORKSTATION.getProperty(), "What is the ntlm proxy workstation?");
                    }
                }

                final Boolean trustCert = askYesOrNo("Would you like to automatically trust certificates?");
                if (trustCert) {
                    setProperty(DetectProperties.BLACKDUCK_TRUST_CERT.getProperty(), "true");
                }

                final Boolean testHub = askYesOrNo("Would you like to test the Black Duck connection now?");
                if (testHub) {
                    ConnectionResult connectionAttempt = null;
                    try {
                        MapPropertySource interactivePropertySource = new MapPropertySource("interactive", toPropertyMap());
                        List<PropertySource> propertySources = new ArrayList<>(this.existingPropertySources);
                        propertySources.add(interactivePropertySource);
                        PropertyConfiguration propertyConfiguration = new PropertyConfiguration(propertySources);
                        DetectConfigurationFactory detectConfigurationFactory = new DetectConfigurationFactory(propertyConfiguration, new SimplePathResolver());
                        BlackDuckConfigFactory blackDuckConfigFactory = new BlackDuckConfigFactory(detectConfigurationFactory.createBlackDuckConnectionDetails());
                        BlackDuckServerConfig blackDuckServerConfig = blackDuckConfigFactory.createServerConfig(new SilentIntLogger());
                        connectionAttempt = blackDuckServerConfig.attemptConnection(new SilentIntLogger());
                    } catch (final Exception e) {
                        println("Failed to test connection.");
                        println(e.toString());
                        println("");
                    }

                    if (connectionAttempt != null && connectionAttempt.isSuccess()) {
                        connected = true;
                    } else {
                        connected = false;
                        println("Failed to connect.");
                        if (connectionAttempt != null) {
                            println(connectionAttempt.getFailureMessage().orElse("Unknown reason."));
                        }
                        skipConnectionTest = !askYesOrNo("Would you like to retry entering Black Duck information?");
                    }
                } else {
                    skipConnectionTest = true;
                }
            }

            final Boolean customDetails = askYesOrNo("Would you like to provide a project name and version to use?");
            if (customDetails) {
                setPropertyFromQuestion(DetectProperties.DETECT_PROJECT_NAME.getProperty(), "What is the project name?");
                setPropertyFromQuestion(DetectProperties.DETECT_PROJECT_VERSION_NAME.getProperty(), "What is the project version?");
            }
        } else {
            setProperty(DetectProperties.BLACKDUCK_OFFLINE_MODE.getProperty(), "true");
        }

        final Boolean scan = askYesOrNo("Would you like run a CLI scan?");
        if (!scan) {
            setProperty(DetectProperties.DETECT_TOOLS_EXCLUDED.getProperty(), "SIGNATURE_SCAN");
        } else if (connectToHub) {
            final Boolean upload = askYesOrNo("Would you like to upload CLI scan results to the Black Duck server?");
            if (!upload) {
                setProperty(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN.getProperty(), "true");
            }
        }

        if (scan) {
            final Boolean customScanner = askYesOrNo("Would you like to provide a custom scanner?");
            if (customScanner) {
                final Boolean downloadCustomScanner = askYesOrNo("Would you like to download the custom scanner?");
                if (downloadCustomScanner) {
                    setPropertyFromQuestion(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL.getProperty(), "What is the scanner host url?");
                } else {
                    setPropertyFromQuestion(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH.getProperty(), "What is the location of your offline scanner?");
                }
            }
        }

        performStandardOutflow();
    }

}
