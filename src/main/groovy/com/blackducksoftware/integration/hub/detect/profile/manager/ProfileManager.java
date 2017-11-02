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
package com.blackducksoftware.integration.hub.detect.profile.manager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.profile.NoScanProfile;
import com.blackducksoftware.integration.hub.detect.profile.OfflineProfile;

@Component
public class ProfileManager {
    private final Logger logger = LoggerFactory.getLogger(ProfileManager.class);
    public Map<String, Class<? extends Annotation>> profiles = new HashMap<>();
    public List<String> selectedProfiles = new ArrayList<>();

    public void init(final List<String> possibleProfiles) {
        try {
            RegisterProfile(OfflineProfile.class);
            RegisterProfile(NoScanProfile.class);
        } catch (final Exception e) {
            logger.error("Exception registering profile classes");
        }

        final Set<String> profileNameSet = profiles.keySet();
        for (final String profile : profileNameSet) {
            if (possibleProfiles.contains(profile)) {
                selectedProfiles.add(profile);
            }
        }
    }

    public Set<String> availableProfiles() {
        return profiles.keySet();
    }

    public <T extends Annotation> void RegisterProfile(final Class<T> clazz) throws Exception {
        final ProfileData data = clazz.getAnnotation(ProfileData.class);
        if (data == null) {
            throw new Exception("Tried to register a profile class but had no ProfileData annotation :" + clazz.getName());
        }
        final String profileName = data.profileName();

        profiles.put(profileName, clazz);

    }

    public Set<String> getProfilesFromOnboardingFlow(final Class<?> onboardingClass) {
        final Set<String> profileSet = new HashSet<>();

        for (final Entry<String, Class<? extends Annotation>> profile : profiles.entrySet()) {
            final Class<? extends Annotation> clazz = profile.getValue();
            final String profileName = profile.getKey();
            final Object annotation = onboardingClass.getAnnotation(clazz);

            if (annotation == null) {
                continue;
            }

            profileSet.add(profileName);

        }

        return profileSet;
    }

    public Set<String> getProfilesFromDetectField(final Field detectConfigurationField) {
        final Set<String> profileSet = new HashSet<>();

        for (final Entry<String, Class<? extends Annotation>> profile : profiles.entrySet()) {
            final Class<? extends Annotation> clazz = profile.getValue();
            final String profileName = profile.getKey();
            final Object annotation = detectConfigurationField.getAnnotation(clazz);

            if (annotation == null) {
                continue;
            }

            profileSet.add(profileName);

        }

        return profileSet;
    }

    Map<String, String> getProfileDefaultsFromDetectField(final Field detectConfigurationField) {
        final Map<String, String> profileValues = new HashMap<>();
        for (final Entry<String, Class<? extends Annotation>> profile : profiles.entrySet()) {
            final Class<? extends Annotation> clazz = profile.getValue();
            final String profileName = profile.getKey();
            final Object annotation = detectConfigurationField.getAnnotation(clazz);

            if (annotation == null) {
                continue;
            }

            Method overrideValueField = null;
            try {
                overrideValueField = clazz.getMethod("overrideDefault");
            } catch (final Exception e) {
                logger.error("Field 'overrideDefault' not found on annotation '" + clazz.getName() + "' but it was registered as a Profile.");
            }

            String value = null;
            try {
                value = (String) overrideValueField.invoke(annotation);
            } catch (final Exception e) {
                logger.error("Could not get value of field 'overrideDefault' on annotation '" + clazz.getName() + "' but it was registered as a Profile.");
            }

            if (value != null && !value.equals(ProfileData.DONT_OVERRIDE)) {
                profileValues.put(profileName, value);
            }
        }

        return profileValues;
    }

}
