/**
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.detect.onboarders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.hub.HubServiceWrapper;

@Component
public class DefaultOnboarder extends Onboarder {

    @Autowired
    private HubServiceWrapper hubServiceWrapper;

    @Override
    public void onboard() {

        printWelcome();

        final Boolean connectToHub = askYesOrNo("Would you like to connect to a Hub Instance?");
        if (connectToHub == true) {
            boolean connected = false;
            boolean skipConnectionTest = false;
            while (!connected && !skipConnectionTest) {
                askFieldQuestion("hubUrl", "What is the hub instance url?");
                askFieldQuestion("hubUsername", "What is the hub username?");
                askSecretFieldQuestion("hubPassword", "What is the hub password?");

                final Boolean useProxy = askYesOrNo("Would you like to configure a proxy for the hub?");
                if (useProxy) {
                    askFieldQuestion("hubProxyHost", "What is the hub proxy host?");
                    askFieldQuestion("hubProxyPort", "What is the hub proxy port?");
                    askFieldQuestion("hubProxyUsername", "What is the hub proxy username?");
                    askSecretFieldQuestion("hubProxyPassword", "What is the hub proxy password?");
                }

                final Boolean trustCert = askYesOrNo("Would you like to automatically trust the hub certificate?");
                if (trustCert) {
                    setField("hubTrustCertificate", "true");
                }

                final Boolean testHub = askYesOrNo("Would you like to test the hub connection now?");
                if (testHub) {
                    try {
                        saveOptionsToConfiguration();
                        connected = hubServiceWrapper.testHubConnection();
                    } catch (final Exception e) {
                        println("Failed to test hub connection.");
                        println(e.toString());
                    }

                    if (!connected) {
                        println("Failed to connect to the hub.");
                        skipConnectionTest = !askYesOrNo("Would you like to retry entering the hub information?");
                    }
                } else {
                    skipConnectionTest = true;
                }
            }

            final Boolean customDetails = askYesOrNo("Would you like to provide a project name and version to use on the hub?");
            if (customDetails) {
                askFieldQuestion("projectName", "What is the hub project name?");
                askFieldQuestion("projectVersionName", "What is the hub project version?");
            }

        } else {
            setField("hubOfflineMode", "true");
        }

        final Boolean scan = askYesOrNo("Would you like run a CLI scan?");
        if (!scan) {
            setField("hubSignatureScannerDisabled", "true");
        } else if (scan && connectToHub) {
            final Boolean upload = askYesOrNo("Would you like to upload CLI scan results to the hub?");
            if (!upload) {
                setField("hubSignatureScannerDryRun", "true");
            }
        }

        if (scan) {
            final Boolean customScanner = askYesOrNo("Would you like to provide a custom scanner?");
            if (customScanner) {
                final Boolean downloadCustomScanner = askYesOrNo("Would you like to download the custom scanner?");
                if (downloadCustomScanner) {
                    askFieldQuestion("hubSignatureScannerHostUrl", "What is the scanner host url?");
                } else {
                    askFieldQuestion("hubSignatureScannerOfflineLocalPath", "What is the location of your offline scanner?");
                }
            }
        }

        performStandardOutflow();

    }

}
