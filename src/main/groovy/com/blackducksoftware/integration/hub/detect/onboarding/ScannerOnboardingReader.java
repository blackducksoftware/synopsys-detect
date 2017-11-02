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

import java.io.InputStream;
import java.util.Scanner;

public class ScannerOnboardingReader implements OnboardingReader {

    private final Scanner scanner;

    public ScannerOnboardingReader(final Scanner scanner) {
        this.scanner = scanner;
    }

    public ScannerOnboardingReader(final InputStream stream) {
        this.scanner = new Scanner(stream);
    }

    @Override
    public String readLine() {
        return scanner.nextLine();
    }

    @Override
    public String readPassword() {
        return scanner.nextLine();
    }

}
