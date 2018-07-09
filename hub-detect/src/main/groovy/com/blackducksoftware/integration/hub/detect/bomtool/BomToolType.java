/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.bomtool;

public enum BomToolType {
    PODLOCK,
    CONDA_CLI,
    CPAN_CLI,
    PACKRAT_LOCK,
    DOCKER,
    GO_CLI,
    GO_LOCK,
    GO_VNDR,
    GRADLE_INSPECTOR,
    REBAR,
    MAVEN_POM_CLI,
    MAVEN_POM_WRAPPER_CLI,
    NPM_CLI,
    NPM_PACKAGELOCK,
    NPM_SHRINKWRAP,
    NUGET_PROJECT_INSPECTOR,
    NUGET_SOLUTION_INSPECTOR,
    COMPOSER_LOCK,
    PEAR_CLI,
    PIP_ENV,
    PIP_INSPECTOR,
    GEMLOCK,
    SBT_RESOLUTION_CACHE,
    YARN_LOCK,
    CLANG
}
