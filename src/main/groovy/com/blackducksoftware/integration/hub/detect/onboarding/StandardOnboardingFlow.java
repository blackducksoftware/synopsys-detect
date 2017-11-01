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

public class StandardOnboardingFlow {

    public StandardOnboardingFlow(final Onboarder onboarder) {
        this.onboarder = onboarder;
    }

    private final Onboarder onboarder;

    public void onboard() {

        onboarder.printReady();

        final Boolean hubConnect = onboarder.askYesOrNo("Would you like to connect to a Hub Instance?");
        if (hubConnect == true) {
            onboarder.askFieldQuestion("hubUrl", "What is the hub instance url?");
            onboarder.askFieldQuestion("hubUsername", "What is the hub username?");
            onboarder.askFieldQuestion("hubPassword", "What is the hub password?");
            onboarder.println("Hub information updated.");

            final Boolean useProxy = onboarder.askYesOrNo("Would you like to configure a proxy for the hub?");
            if (useProxy) {
                onboarder.askFieldQuestion("hubProxyHost", "What is the hub proxy host?");
                onboarder.askFieldQuestion("hubProxyPort", "What is the hub proxy port?");
                onboarder.askFieldQuestion("hubProxyUsername", "What is the hub proxy username?");
                onboarder.askFieldQuestion("hubProxyPassword", "What is the hub proxy password?");
                onboarder.println("Hub proxy updated.");
            }

            final Boolean trustCert = onboarder.askYesOrNo("Would you like to automatically trust the hub certificate?");
            if (trustCert) {
                onboarder.setField("hubTrustCertificate", "true");
                onboarder.println("Hub certificate updated.");
            }

            final Boolean hubName = onboarder.askYesOrNo("Would you like to provide a project name and version to use on the hub?");
            if (hubName) {
                onboarder.askFieldQuestion("projectName", "What is the hub project name?");
                onboarder.askFieldQuestion("projectVersionName", "What is the hub project version?");
                onboarder.println("Project information updated.");
            }

        } else {
            onboarder.println("Setting detect to offline mode.");
            onboarder.setField("hubOfflineMode", "true");
        }

        final Boolean scan = onboarder.askYesOrNo("Would you like run a CLI scan?");
        if (!scan) {
            onboarder.println("Disabling signature scanner.");
            onboarder.setField("hubSignatureScannerDisabled", "true");
        } else if (scan && hubConnect) {
            final Boolean upload = onboarder.askYesOrNo("Would you like to upload CLI scan results to the hub?");
            if (!upload) {
                onboarder.println("Setting signature scanner to dry run.");
                onboarder.setField("hubSignatureScannerDryRun", "true");
            }
        }

        if (scan) {
            final Boolean custom = onboarder.askYesOrNo("Would you like to provide a custom scanner?");
            if (custom) {
                final Boolean download = onboarder.askYesOrNo("Would you like to download the custom scanner?");
                if (download) {
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
