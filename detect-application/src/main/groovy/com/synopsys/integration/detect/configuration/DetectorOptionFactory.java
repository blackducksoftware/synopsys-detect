/**
 * detect-application
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
package com.synopsys.integration.detect.configuration;

import com.synopsys.integration.detect.detector.bitbake.BitbakeDetectorOptions;

public class DetectorOptionFactory {
    private DetectConfiguration detectConfiguration;

    public DetectorOptionFactory(final DetectConfiguration detectConfiguration) {this.detectConfiguration = detectConfiguration;}

    public BitbakeDetectorOptions createBitbakeDetectorOptions() {
        String buildEnvName = detectConfiguration.getProperty(DetectProperty.DETECT_BITBAKE_BUILD_ENV_NAME, PropertyAuthority.None);
        String[] bitbakePackageNames = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_BITBAKE_PACKAGE_NAMES, PropertyAuthority.None);
        return new BitbakeDetectorOptions(buildEnvName, bitbakePackageNames);
    }

}
