/**
 * detect-configuration
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detect.configuration;

import static com.synopsys.integration.detect.configuration.HelpConstants.DETECTORS_SUPER_GROUP;

public enum HelpGroups {
    //Group
    GROUP_ARTIFACTORY("artifactory"),
    GROUP_BLACKDUCK_SERVER("blackduck server"),
    GROUP_CLEANUP("cleanup"),
    GROUP_CODELOCATION("codelocation"),
    GROUP_GENERAL("general"),
    GROUP_LOGGING("logging"),
    GROUP_PATHS("paths"),
    GROUP_POLICY_CHECK("policy check"),
    GROUP_PROJECT("project"),
    GROUP_PROJECT_INFO("project info"),
    GROUP_PROXY("proxy"),
    GROUP_REPORT("report"),
    GROUP_SOURCE_SCAN("source scan"),
    GROUP_SOURCE_PATH("source path"),

    //Tool Groups
    GROUP_DETECTOR("detector"),
    GROUP_POLARIS("polaris"),
    GROUP_SIGNATURE_SCANNER("signature scanner"),

    //Detector Groups
    GROUP_BAZEL("bazel", DETECTORS_SUPER_GROUP),
    GROUP_BITBAKE("bitbake", DETECTORS_SUPER_GROUP),
    GROUP_CONDA("conda", DETECTORS_SUPER_GROUP),
    GROUP_CPAN("cpan", DETECTORS_SUPER_GROUP),
    GROUP_DOCKER("docker", DETECTORS_SUPER_GROUP),
    GROUP_GO("go", DETECTORS_SUPER_GROUP),
    GROUP_GRADLE("gradle", DETECTORS_SUPER_GROUP),
    GROUP_HEX("hex", DETECTORS_SUPER_GROUP),
    GROUP_MAVEN("maven", DETECTORS_SUPER_GROUP),
    GROUP_NPM("npm", DETECTORS_SUPER_GROUP),
    GROUP_NUGET("nuget", DETECTORS_SUPER_GROUP),
    GROUP_PACKAGIST("packagist", DETECTORS_SUPER_GROUP),
    GROUP_PEAR("pear", DETECTORS_SUPER_GROUP),
    GROUP_PIP("pip", DETECTORS_SUPER_GROUP),
    GROUP_PYTHON("python", DETECTORS_SUPER_GROUP),
    GROUP_RUBY("ruby", DETECTORS_SUPER_GROUP),
    GROUP_SBT("sbt", DETECTORS_SUPER_GROUP),
    GROUP_YARN("yarn", DETECTORS_SUPER_GROUP),

    //Additional groups (should not be used as a primary group
    SEARCH_GROUP_BLACKDUCK("blackduck"),
    SEARCH_GROUP_DEBUG("debug"),
    SEARCH_GROUP_GLOBAL("global"),
    SEARCH_GROUP_OFFLINE("offline"),
    SEARCH_GROUP_POLICY("policy"),
    SEARCH_GROUP_PROJECT_SETTING("project setting"),
    SEARCH_GROUP_REPORT_SETTING("report setting"),
    SEARCH_GROUP_SEARCH("search"),
    DEFAULT_HELP("default");

    private final String name;
    private final String superGroup;

    HelpGroups(final String name, final String supergroup) {
        this.name = name;
        this.superGroup = supergroup;
    }

    HelpGroups(final String name) {
        this.name = name;
        this.superGroup = null;
    }

    public String getName() {
        return name;
    }

    public String getSuperGroup() {
        return superGroup;
    }
}
