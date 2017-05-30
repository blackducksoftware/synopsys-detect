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
package com.blackducksoftware.integration.hub.packman.util.command

class ExecutableRunnerException extends Exception {
    ExecutableRunnerException(final Throwable innerException) {
        super(innerException)
    }

    ExecutableRunnerException(final ExecutableOutput commandOutput) {
        super("${commandOutput.standardOutput}\n\n${commandOutput.errorOutput}")
    }
}
