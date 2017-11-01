/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.detect.onboarding;

import com.blackducksoftware.integration.hub.detect.hub.HubServiceWrapper;

public class StandardOnboardingFlow {

    public StandardOnboardingFlow(final Onboarder onboarder, final HubServiceWrapper hubServiceWrapper) {
        this.onboarder = onboarder;
        this.hubServiceWrapper = hubServiceWrapper;
    }

    private final Onboarder onboarder;
    private final HubServiceWrapper hubServiceWrapper;

    public void onboard() {

        onboarder.printReady();

        final Boolean connectToHub = onboarder.askYesOrNo("Would you like to connect to a Hub Instance?");
        if (connectToHub == true) {
            boolean connected = false;
            boolean skipConnectionTest = false;
            while (!connected && !skipConnectionTest) {
                onboarder.askFieldQuestion("hubUrl", "What is the hub instance url?");
                onboarder.askFieldQuestion("hubUsername", "What is the hub username?");
                onboarder.askFieldQuestion("hubPassword", "What is the hub password?");

                final Boolean useProxy = onboarder.askYesOrNo("Would you like to configure a proxy for the hub?");
                if (useProxy) {
                    onboarder.askFieldQuestion("hubProxyHost", "What is the hub proxy host?");
                    onboarder.askFieldQuestion("hubProxyPort", "What is the hub proxy port?");
                    onboarder.askFieldQuestion("hubProxyUsername", "What is the hub proxy username?");
                    onboarder.askFieldQuestion("hubProxyPassword", "What is the hub proxy password?");
                }

                final Boolean trustCert = onboarder.askYesOrNo("Would you like to automatically trust the hub certificate?");
                if (trustCert) {
                    onboarder.setField("hubTrustCertificate", "true");
                }

                final Boolean testHub = onboarder.askYesOrNo("Would you like to test the hub connection now?");
                if (testHub) {
                    try {
                        onboarder.saveOptionsToConfiguration();
                        connected = hubServiceWrapper.testHubConnection();
                    } catch (final Exception e) {
                        onboarder.println("Failed to test hub connection.");
                        onboarder.println(e.toString());
                    }

                    if (!connected) {
                        skipConnectionTest = !onboarder.askYesOrNo("Would you like to retry entering the hub information?");
                    }
                } else {
                    skipConnectionTest = true;
                }
            }

            final Boolean customDetails = onboarder.askYesOrNo("Would you like to provide a project name and version to use on the hub?");
            if (customDetails) {
                onboarder.askFieldQuestion("projectName", "What is the hub project name?");
                onboarder.askFieldQuestion("projectVersionName", "What is the hub project version?");
            }

        } else {
            onboarder.setField("hubOfflineMode", "true");
        }

        final Boolean scan = onboarder.askYesOrNo("Would you like run a CLI scan?");
        if (!scan) {
            onboarder.setField("hubSignatureScannerDisabled", "true");
        } else if (scan && connectToHub) {
            final Boolean upload = onboarder.askYesOrNo("Would you like to upload CLI scan results to the hub?");
            if (!upload) {
                onboarder.setField("hubSignatureScannerDryRun", "true");
            }
        }

        if (scan) {
            final Boolean customScanner = onboarder.askYesOrNo("Would you like to provide a custom scanner?");
            if (customScanner) {
                final Boolean downloadCustomScanner = onboarder.askYesOrNo("Would you like to download the custom scanner?");
                if (downloadCustomScanner) {
                    onboarder.askFieldQuestion("hubSignatureScannerHostUrl", "What is the scanner host url?");
                } else {
                    onboarder.askFieldQuestion("hubSignatureScannerOfflineLocalPath", "What is the location of your offline scanner?");
                }
            }
        }

        onboarder.printSuccess();
        onboarder.askToSave();
        onboarder.readyToStart();

    }

}
