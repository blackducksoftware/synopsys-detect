/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detect.configuration.enumeration;

public enum ExitCodeType {
    SUCCESS(0, "Detect exited successfully."),
    FAILURE_BLACKDUCK_CONNECTIVITY(1, "Detect was unable to connect to Black Duck. Check your configuration and connection."),
    FAILURE_TIMEOUT(2, "Detect was unable to wait for actions to be completed on Black Duck. Check your Black Duck server or increase your timeout."),
    FAILURE_POLICY_VIOLATION(3, "Detect found policy violations."),
    FAILURE_PROXY_CONNECTIVITY(4, "Detect was unable to use the configured proxy. Check your configuration and connection."),
    FAILURE_DETECTOR(5, "Detect had one or more detector failures while extracting dependencies. Check that all projects build and your environment is configured correctly."),
    FAILURE_SCAN(6, "Detect was unable to run the signature scanner against your source. Check your configuration."),
    FAILURE_CONFIGURATION(7, "Detect was unable to start due to issues with it's configuration. Check and fix your configuration."),
    FAILURE_DETECTOR_REQUIRED(9, "Detect did not run all of the required detectors. Fix detector issues or disable required detectors."),
    FAILURE_BLACKDUCK_VERSION_NOT_SUPPORTED(10, "Detect attempted an operation that was not supported by your version of Black Duck. Ensure your Black Duck is compatible with this version of detect."),
    FAILURE_BLACKDUCK_FEATURE_ERROR(11, "Detect encountered an error while attempting an operation on Black Duck. Ensure your Black Duck is compatible with this version of detect."),
    FAILURE_POLARIS_CONNECTIVITY(12, "Detect was unable to connect to Polaris. Check your configuration and connection."),
    FAILURE_GENERAL_ERROR(99, "Detect encountered a known error, details of the error are provided."),
    FAILURE_UNKNOWN_ERROR(100, "Detect encountered an unknown error.");

    private int exitCode;
    private String description;

    private ExitCodeType(final int exitCode, String description) {
        this.exitCode = exitCode;
        this.description = description;
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

    public String getDescription() {
        return description;
    }
}
