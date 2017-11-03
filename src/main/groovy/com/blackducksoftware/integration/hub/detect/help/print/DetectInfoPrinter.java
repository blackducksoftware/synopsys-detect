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
package com.blackducksoftware.integration.hub.detect.help.print;

import java.io.PrintStream;

import com.blackducksoftware.integration.hub.detect.DetectInfo;

public class DetectInfoPrinter {
    public void printInfo(final PrintStream printStream, final DetectInfo detectInfo) {
        printStream.println("");
        printStream.println("Detect Version: " + detectInfo.getDetectVersion());
        printStream.println("");
    }
}
