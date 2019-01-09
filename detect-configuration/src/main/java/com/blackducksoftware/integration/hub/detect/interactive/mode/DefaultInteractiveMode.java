/**
 * detect-configuration
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.interactive.mode;

import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.help.DetectOptionManager;
import com.blackducksoftware.integration.hub.detect.hub.HubServiceManager;
import com.synopsys.integration.log.SilentLogger;

public class DefaultInteractiveMode extends InteractiveMode {
    private final HubServiceManager hubServiceManager;
    private final DetectOptionManager detectOptionManager;

    public DefaultInteractiveMode(final HubServiceManager hubServiceManager, final DetectOptionManager detectOptionManager) {
        this.hubServiceManager = hubServiceManager;
        this.detectOptionManager = detectOptionManager;
    }

    @Override
    public void configure() {
        printWelcome();

        final Boolean connectToHub = askYesOrNo("Would you like to connect to a Black Duck server?");
        if (connectToHub == true) {
            boolean connected = false;
            boolean skipConnectionTest = false;
            while (!connected && !skipConnectionTest) {
                setPropertyFromQuestion(DetectProperty.BLACKDUCK_URL, "What is the Black Duck server url?");

                println("You can now configure Black Duck with either an API token -OR- a username and password. The API token must already exist on the Black Duck server, but it is the preferred approach to configure your connection.");
                final Boolean useApiToken = askYesOrNo("Would you like to use an existing API token?");
                if (useApiToken) {
                    setPropertyFromQuestion(DetectProperty.BLACKDUCK_API_TOKEN, "What is the API token?");
                } else {
                    setPropertyFromQuestion(DetectProperty.BLACKDUCK_USERNAME, "What is the username?");

                    final Boolean setHubPassword = askYesOrNoWithMessage("Would you like to set the password?",
                            "WARNING: If you choose to save the settings, this password will be stored in plain text. You can set this password as an environment variable BLACKDUCK_HUB_PASSWORD.");
                    if (setHubPassword) {
                        setPropertyFromSecretQuestion(DetectProperty.BLACKDUCK_PASSWORD, "What is the password?");
                    }
                }

                final Boolean useProxy = askYesOrNo("Would you like to configure a proxy?");
                if (useProxy) {
                    setPropertyFromQuestion(DetectProperty.BLACKDUCK_PROXY_HOST, "What is the proxy host?");
                    setPropertyFromQuestion(DetectProperty.BLACKDUCK_PROXY_PORT, "What is the proxy port?");
                    setPropertyFromQuestion(DetectProperty.BLACKDUCK_PROXY_USERNAME, "What is the Black Duck username?");
                    final Boolean setHubPassword = askYesOrNoWithMessage("Would you like to set the Black Duck password?",
                            "WARNING: If you choose to save the settings, this password will be stored in plain text. You can set this password as an environment variable BLACKDUCK_PROXY_PASSWORD.");
                    if (setHubPassword) {
                        setPropertyFromSecretQuestion(DetectProperty.BLACKDUCK_PROXY_PASSWORD, "What is the proxy password?");
                    }
                    final Boolean useNtlmProxy = askYesOrNo("Do you use a ntlm proxy?");
                    if (useNtlmProxy) {
                        setPropertyFromQuestion(DetectProperty.BLACKDUCK_PROXY_NTLM_DOMAIN, "What is the ntlm proxy domain?");
                        setPropertyFromQuestion(DetectProperty.BLACKDUCK_PROXY_NTLM_WORKSTATION, "What is the ntlm proxy workstation?");
                    }
                }

                final Boolean trustCert = askYesOrNo("Would you like to automatically trust certificates?");
                if (trustCert) {
                    setProperty(DetectProperty.BLACKDUCK_TRUST_CERT, "true");
                }

                final Boolean testHub = askYesOrNo("Would you like to test the Black Duck connection now?");
                if (testHub) {
                    try {
                        detectOptionManager.applyInteractiveOptions(getInteractiveOptions());
                        connected = hubServiceManager.testHubConnection(new SilentLogger());
                    } catch (final Exception e) {
                        println("Failed to test connection.");
                        println(e.toString());
                        println("");
                    }

                    if (!connected) {
                        println("Failed to connect.");
                        println("");
                        skipConnectionTest = !askYesOrNo("Would you like to retry entering Black Duck information?");
                    }
                } else {
                    skipConnectionTest = true;
                }
            }

            final Boolean customDetails = askYesOrNo("Would you like to provide a project name and version to use?");
            if (customDetails) {
                setPropertyFromQuestion(DetectProperty.DETECT_PROJECT_NAME, "What is the project name?");
                setPropertyFromQuestion(DetectProperty.DETECT_PROJECT_VERSION_NAME, "What is the project version?");
            }
        } else {
            setProperty(DetectProperty.BLACKDUCK_OFFLINE_MODE, "true");
        }

        final Boolean scan = askYesOrNo("Would you like run a CLI scan?");
        if (!scan) {
            setProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED, "true");
        } else if (scan && connectToHub) {
            final Boolean upload = askYesOrNo("Would you like to upload CLI scan results to the Black Duck server?");
            if (!upload) {
                setProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN, "true");
            }
        }

        if (scan) {
            final Boolean customScanner = askYesOrNo("Would you like to provide a custom scanner?");
            if (customScanner) {
                final Boolean downloadCustomScanner = askYesOrNo("Would you like to download the custom scanner?");
                if (downloadCustomScanner) {
                    setPropertyFromQuestion(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL, "What is the scanner host url?");
                } else {
                    setPropertyFromQuestion(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH, "What is the location of your offline scanner?");
                }
            }
        }

        performStandardOutflow();
    }

}
