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
package com.blackducksoftware.integration.hub.detect;

import java.io.PrintStream;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.help.DetectOption;
import com.blackducksoftware.integration.hub.detect.onboarding.OnboardingOption;

public interface DetectConfigurationPrinter {

    void printHeader(final PrintStream printStream, DetectInfo detectInfo, final List<DetectOption> detectOptions);

    void printConfiguration(final PrintStream printStream, DetectInfo detectInfo, DetectConfiguration detectConfiguration, final List<DetectOption> detectOptions, List<OnboardingOption> onboardedOptions);

}
