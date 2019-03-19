/**
 * detectable
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.detectable.detectables.bitbake;

public class BitbakeDetectableOptions {
    private final String buildEnvName;
    private final String[] packageNames;
    private final String referenceImplementation;

    public BitbakeDetectableOptions(final String buildEnvName, final String[] packageNames, final String referenceImplementation) {
        this.buildEnvName = buildEnvName;
        this.packageNames = packageNames;
        this.referenceImplementation = referenceImplementation;
    }

    public String getBuildEnvName() {
        return buildEnvName;
    }

    public String[] getPackageNames() {
        return packageNames;
    }

    public String getReferenceImplementation() {
        return referenceImplementation;
    }
}
