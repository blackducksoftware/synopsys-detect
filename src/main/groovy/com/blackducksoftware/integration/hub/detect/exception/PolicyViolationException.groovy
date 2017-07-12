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
package com.blackducksoftware.integration.hub.detect.exception

import org.springframework.boot.ExitCodeGenerator

class PolicyViolationException extends Exception implements ExitCodeGenerator {
    private int exceptionExitCode = 1

    public PolicyViolationException() {
        super()
    }

    public PolicyViolationException(String message) {
        super(message)
    }

    public PolicyViolationException(int exitCode) {
        super()
        exceptionExitCode = exitCode
    }

    public PolicyViolationException(String message, int exitCode) {
        super(message)
        exceptionExitCode = exitCode
    }

    @Override
    public int getExitCode() {
        return exceptionExitCode
    }
}
