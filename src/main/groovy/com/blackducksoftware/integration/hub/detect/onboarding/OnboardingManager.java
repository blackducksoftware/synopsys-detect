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
package com.blackducksoftware.integration.hub.detect.onboarding;

import java.io.PrintStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.help.DetectOptionManager;
import com.blackducksoftware.integration.hub.detect.onboarders.DefaultOnboarder;
import com.blackducksoftware.integration.hub.detect.onboarders.Onboarder;
import com.blackducksoftware.integration.hub.detect.onboarding.reader.OnboardingReader;

@Component
public class OnboardingManager {
    private final Logger logger = LoggerFactory.getLogger(OnboardingManager.class);

    @Autowired
    DetectOptionManager detectOptionManager;

    @Autowired
    List<Onboarder> onboardingFlows;

    @Autowired
    DefaultOnboarder defaultOnboarder;

    public void onboard(final OnboardingReader onboardingReader, final PrintStream printStream) {
        final Onboarder onboarder = defaultOnboarder;

        onboarder.init(printStream, onboardingReader);

        onboarder.println("");
        onboarder.println("Onboarding flag found.");
        onboarder.println("Starting default onboarder.");
        onboarder.println("");

        try {
            onboarder.onboard();
            final List<OnboardingOption> onboardedOptions = onboarder.getOnboardedOptions();
            detectOptionManager.applyOnboardedOptions(onboardedOptions);
        } catch (final Exception e) {
            logger.error(e.toString());
            logger.error("Onboarding failed. Please retry onboarding or remove '-o' and '--onboard' from your options.");
            throw new RuntimeException(e);
        }
    }

}
