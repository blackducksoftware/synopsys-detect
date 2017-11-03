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
package com.blackducksoftware.integration.hub.detect.help;

import java.util.List;
import java.util.Map;

public class DetectDefaultValue {
    public final String originalDefault;
    public final Map<String, String> profileSpecificDefaults;
    public final String chosenProfile;
    public final String chosenDefault;

    public DetectDefaultValue(final String originalDefault, final Map<String, String> profileSpecificDefaults, final List<String> selectedProfiles) {
        this.profileSpecificDefaults = profileSpecificDefaults;
        this.originalDefault = originalDefault;

        String bestDefault = originalDefault;
        String bestProfile = null;
        for (final String profile : selectedProfiles) {
            if (profileSpecificDefaults.containsKey(profile)) {
                bestDefault = profileSpecificDefaults.get(profile);
                bestProfile = profile;
                break;
            }
        }

        chosenProfile = bestProfile;
        chosenDefault = bestDefault;
    }

    public boolean containsProfile(final String profile) {
        return profileSpecificDefaults.containsKey(profile);
    }
}
