/**
 * hub-detect
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
package com.blackducksoftware.integration.hub.detect.workflow.phonehome;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchResult;
import com.google.gson.Gson;
import com.synopsys.integration.phonehome.PhoneHomeResponse;

public abstract class PhoneHomeManager {
    private final Logger logger = LoggerFactory.getLogger(PhoneHomeManager.class);

    protected final Gson gson;
    protected final DetectInfo detectInfo;
    protected final EventSystem eventSystem;
    protected PhoneHomeResponse currentPhoneHomeResponse;
    protected Map<String, String> additionalMetaData;

    public PhoneHomeManager(Map<String, String> additionalMetaData, final DetectInfo detectInfo, final Gson gson, EventSystem eventSystem) {
        this.gson = gson;
        this.detectInfo = detectInfo;
        this.eventSystem = eventSystem;
        this.additionalMetaData = additionalMetaData;

        eventSystem.registerListener(Event.SearchCompleted, event -> searchCompleted(event));
        eventSystem.registerListener(Event.BomToolsProfiled, event -> startPhoneHome(event.bomToolTimings));
    }

    public abstract PhoneHomeResponse phoneHome(final Map<String, String> metadata);

    public void startPhoneHome() {
        // hub-detect will attempt to phone home twice - once upon startup and
        // once upon getting all the detector metadata.
        //
        // We would prefer to always wait for all the detector metadata, but
        // sometimes there is not enough time to complete a phone home before
        // hub-detect exits (if the scanner is disabled, for example).
        safelyPhoneHome(new HashMap<>());
    }

    public void startPhoneHome(final Set<DetectorType> applicableDetectorTypes) {
        final Map<String, String> metadata = new HashMap<>();
        if (applicableDetectorTypes != null) {
            final String applicableBomToolsString = applicableDetectorTypes.stream()
                                                        .map(DetectorType::toString)
                                                        .collect(Collectors.joining(","));
            metadata.put("bomToolTypes", applicableBomToolsString);
        }
        safelyPhoneHome(metadata);
    }

    public void startPhoneHome(final Map<DetectorType, Long> applicableDetectorTimes) {
        final Map<String, String> metadata = new HashMap<>();
        if (applicableDetectorTimes != null) {
            final String applicableBomToolsString = applicableDetectorTimes.keySet().stream()
                                                        .map(it -> String.format("%s:%s", it.toString(), applicableDetectorTimes.get(it)))
                                                        .collect(Collectors.joining(","));
            metadata.put("bomToolTypes", applicableBomToolsString);
        }
        safelyPhoneHome(metadata);
    }

    public void searchCompleted(final SearchResult searchResult) {
        startPhoneHome(searchResult.getApplicableBomTools());
    }

    private void safelyPhoneHome(final Map<String, String> metadata) {
        endPhoneHome();
        try {
            currentPhoneHomeResponse = phoneHome(metadata);
        } catch (final IllegalStateException e) {
            logger.debug(e.getMessage(), e);
        }
    }

    public void endPhoneHome() {
        if (currentPhoneHomeResponse != null) {
            currentPhoneHomeResponse.getImmediateResult();
        }
    }
}
