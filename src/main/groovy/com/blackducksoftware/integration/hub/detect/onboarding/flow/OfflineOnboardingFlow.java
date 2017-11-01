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
package com.blackducksoftware.integration.hub.detect.onboarding.flow;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.onboarding.Onboarder;
import com.blackducksoftware.integration.hub.detect.onboarding.OnboardingFlow;
import com.blackducksoftware.integration.hub.detect.profile.OfflineProfile;

@Component
@OfflineProfile()
public class OfflineOnboardingFlow implements OnboardingFlow {

    @Override
    public void onboard(final Onboarder onboarder) {

        onboarder.printWelcome();

        final Boolean scan = onboarder.askYesOrNo("Would you like run a CLI scan?");
        if (!scan) {
            onboarder.setField("hubSignatureScannerDisabled", "true");
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

        onboarder.performStandardOutflow();

    }

}
