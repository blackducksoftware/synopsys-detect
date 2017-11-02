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

import java.io.Console;

public class ConsoleOnboardingReader implements OnboardingReader {

    private final Console console;

    public ConsoleOnboardingReader(final Console console) {
        this.console = console;
    }

    @Override
    public String readLine() {
        return console.readLine();
    }

    @Override
    public String readPassword() {
        return console.readPassword().toString();
    }

}
