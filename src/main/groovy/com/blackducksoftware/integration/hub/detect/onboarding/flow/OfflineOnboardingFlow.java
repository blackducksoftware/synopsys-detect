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
package com.blackducksoftware.integration.hub.detect.onboarding.flow;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.onboarding.Onboarder;
import com.blackducksoftware.integration.hub.detect.onboarding.OnboardingFlow;

@Component
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
