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
package com.blackducksoftware.integration.hub.detect.exitcode;

public enum ExitCodeType {
    SUCCESS(0),
    FAILURE_HUB_CONNECTIVITY(1),
    FAILURE_TIMEOUT(2),
    FAILURE_POLICY_VIOLATION(3),
    FAILURE_PROXY_CONNECTIVITY(4),
    FAILURE_GENERAL_ERROR(99),
    FAILURE_UNKNOWN_ERROR(100);

    private int exitCode;

    private ExitCodeType(final int exitCode) {
        this.exitCode = exitCode;
    }

    /**
     * A failure always beats a success and a failure with a lower exit code beats a failure with a higher exit code.
     */
    public static ExitCodeType getWinningExitCodeType(final ExitCodeType first, final ExitCodeType second) {
        if (first.isSuccess()) {
            return second;
        } else if (second.isSuccess()) {
            return first;
        } else {
            if (first.getExitCode() < second.getExitCode()) {
                return first;
            } else {
                return second;
            }
        }
    }

    public int getExitCode() {
        return exitCode;
    }

    public boolean isSuccess() {
        return this == SUCCESS;
    }

}
