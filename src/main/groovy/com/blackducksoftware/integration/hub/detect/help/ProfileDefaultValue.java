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
package com.blackducksoftware.integration.hub.detect.help;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileDefaultValue {
    private final String originalDefault;
    private final Map<String, String> profileSpecificDefaults;

    public ProfileDefaultValue(final String originalDefault) {
        this.profileSpecificDefaults = new HashMap<>();
        this.originalDefault = originalDefault;
    }

    public ProfileDefaultValue(final String originalDefault, final Map<String, String> profileSpecificDefaults) {
        this.profileSpecificDefaults = profileSpecificDefaults;
        this.originalDefault = originalDefault;
    }

    public void addDefault(final String profile, final String value) {
        profileSpecificDefaults.put(profile, value);
    }

    public String defaultValue(final List<String> profiles) {
        String actualDefault = originalDefault;
        for (final String profile : profiles) {
            if (profileSpecificDefaults.containsKey(profile)) {
                actualDefault = profileSpecificDefaults.get(profile);
                break;
            }
        }
        return actualDefault;
    }

    public String matchingProfile(final List<String> profiles) {
        for (final String profile : profiles) {
            if (profileSpecificDefaults.containsKey(profile)) {
                return profile;
            }
        }
        return null;
    }

    public boolean containsProfile(final String profile) {
        return profileSpecificDefaults.containsKey(profile);
    }
}
