/**
 * detector
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
package com.synopsys.integration.detector.base;

public enum DetectorStatusCode {
    CARGO_LOCKFILE_NOT_FOUND("A Cargo.toml was located in the target project, but the Cargo.lock file was NOT located."),
    EXCEPTION("An exception occured."),
    EXCLUDED("Detector type was excluded."),
    EXECUTABLE_FAILED("During extraction, one or more executables did not execute successfully."),
    EXTRACTION_FAILED("During extraction, one or more exceptions were encountered."),
    EXECUTABLE_NOT_FOUND("The necessary executable was not found."),
    FAILED("Detector failed."),
    FALLBACK_NOT_NEEDED("The fallback detector was not needed, as its preceding detector passed."),
    FILES_NOT_FOUND("Necessary files were not found within the target project."),
    FILE_NOT_FOUND("A file was not found within the target project."),
    FORCED_NESTED_PASSED("Forced to pass because nested forced by user."),
    GO_PKG_LOCKFILE_NOT_FOUND("A Gopkg.toml was located in the target project, but the Gopkg.lock file was NOT located."),
    INSPECTOR_NOT_FOUND("The necessary inspector was not found"),
    MAX_DEPTH_EXCEEDED("Max depth was exceeded."),
    NOT_NESTABLE("Not nestable and a detector already applied in parent directory."),
    NOT_SELF_NESTABLE("Nestable but this detector already applied in a parent directory."),
    NPM_NODE_MODULES_NOT_FOUND("A package.json was located in the target project, but the node_modules folder was NOT located."),
    PASSED("Detector passed."),
    POETRY_LOCKFILE_NOT_FOUND("A pyproject.toml was located in the target project, but the Poetry.lock file was NOT located."),
    PROPERTY_INSUFFICIENT("The properties are insufficient to run."),
    SECTION_NOT_FOUND("A necessary section was not found within a file within the target project."),
    UNKNOWN_DETECTOR_RESULT("There was an unknown result."),
    WRONG_OPERATING_SYSTEM_RESULT("Cannot run on the used operating system."),
    YIELDED("Yielded to other detectors.");

    private String description;

    DetectorStatusCode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
