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

import java.util.List;
import java.util.Map;

public class ProfileDefaultValue {
    public final String originalDefault;
    public final Map<String, String> profileSpecificDefaults;
    public final String chosenProfile;
    public final String chosenDefault;

    public ProfileDefaultValue(final String originalDefault, final Map<String, String> profileSpecificDefaults, final List<String> selectedProfiles) {
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
