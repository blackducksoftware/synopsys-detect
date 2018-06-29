/**
 * detect-configuration
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
package com.blackducksoftware.integration.hub.detect.configuration;

public abstract class BaseConfig {
    protected static final String NUGET = "nuget";
    protected static final String GRADLE = "gradle";
    protected static final String DOCKER = "docker";

    protected static final String GROUP_HUB_CONFIGURATION = "hub configuration";
    protected static final String GROUP_GENERAL = "general";
    protected static final String GROUP_LOGGING = "logging";
    protected static final String GROUP_CLEANUP = "cleanup";
    protected static final String GROUP_PATHS = "paths";
    protected static final String GROUP_BOMTOOL = "bomtool";
    protected static final String GROUP_CODELOCATION = "codelocation";
    protected static final String GROUP_CONDA = "conda";
    protected static final String GROUP_CPAN = "cpan";
    protected static final String GROUP_DOCKER = "docker";
    protected static final String GROUP_GO = "go";
    protected static final String GROUP_GRADLE = "gradle";
    protected static final String GROUP_HEX = "hex";
    protected static final String GROUP_MAVEN = "maven";
    protected static final String GROUP_NPM = "npm";
    protected static final String GROUP_NUGET = "nuget";
    protected static final String GROUP_PACKAGIST = "packagist";
    protected static final String GROUP_PEAR = "pear";
    protected static final String GROUP_PIP = "pip";
    protected static final String GROUP_POLICY_CHECK = "policy check";
    protected static final String GROUP_PROJECT_INFO = "project info";
    protected static final String GROUP_PYTHON = "python";
    protected static final String GROUP_SBT = "sbt";
    protected static final String GROUP_SIGNATURE_SCANNER = "signature scanner";
    protected static final String GROUP_YARN = "yarn";

    protected static final String SEARCH_GROUP_SIGNATURE_SCANNER = "scanner";
    protected static final String SEARCH_GROUP_POLICY = "policy";
    protected static final String SEARCH_GROUP_HUB = "hub";
    protected static final String SEARCH_GROUP_PROXY = "proxy";
    protected static final String SEARCH_GROUP_OFFLINE = "offline";
    protected static final String SEARCH_GROUP_PROJECT = "project";
    protected static final String SEARCH_GROUP_DEBUG = "debug";
    protected static final String SEARCH_GROUP_SEARCH = "search";

    protected static final String PRINT_GROUP_DEFAULT = SEARCH_GROUP_HUB;

    protected int convertInt(final Integer integerObj) {
        return integerObj == null ? 0 : integerObj.intValue();
    }

    protected long convertLong(final Long longObj) {
        return longObj == null ? 0L : longObj.longValue();
    }
}
