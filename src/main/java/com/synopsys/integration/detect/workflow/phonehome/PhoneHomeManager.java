/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detect.workflow.phonehome;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.phonehome.PhoneHomeResponse;

public abstract class PhoneHomeManager {
    private final Logger logger = LoggerFactory.getLogger(PhoneHomeManager.class);

    protected final DetectInfo detectInfo;
    protected final EventSystem eventSystem;
    protected PhoneHomeResponse currentPhoneHomeResponse;
    protected Map<String, String> additionalMetaData;

    public PhoneHomeManager(final Map<String, String> additionalMetaData, final DetectInfo detectInfo, final EventSystem eventSystem) {
        this.detectInfo = detectInfo;
        this.eventSystem = eventSystem;
        this.additionalMetaData = additionalMetaData;

        eventSystem.registerListener(Event.ApplicableCompleted, this::startPhoneHome);
        eventSystem.registerListener(Event.DetectorsProfiled, event -> startPhoneHome(event.getAggregateTimings()));
    }

    public abstract PhoneHomeResponse phoneHome(final Map<String, String> metadata, final String... artifactModules);

    public void startPhoneHome() {
        // detect will attempt to phone home twice - once upon startup and
        // once upon getting all the detector metadata.
        //
        // We would prefer to always wait for all the detector metadata, but
        // sometimes there is not enough time to complete a phone home before
        // detect exits (if the scanner is disabled, for example).
        safelyPhoneHome(new HashMap<>());
    }

    private void startPhoneHome(final Set<DetectorType> applicableDetectorTypes) {
        if (applicableDetectorTypes != null) {
            final String[] artifactModules = applicableDetectorTypes.stream().map(DetectorType::toString).toArray(String[]::new);
            safelyPhoneHome(new HashMap<>(), artifactModules);
        }
    }

    public void startPhoneHome(final Map<DetectorType, Long> aggregateTimes) {
        final Map<String, String> metadata = new HashMap<>();
        if (aggregateTimes != null) {
            final String applicableBomToolsString = aggregateTimes.keySet().stream()
                                                        .map(it -> String.format("%s:%s", it.toString(), aggregateTimes.get(it)))
                                                        .collect(Collectors.joining(","));
            metadata.put("detectorTimes", applicableBomToolsString);
        }
        safelyPhoneHome(metadata);
    }

    private void safelyPhoneHome(final Map<String, String> metadata, final String... artifactModules) {
        endPhoneHome();
        try {
            currentPhoneHomeResponse = phoneHome(metadata, artifactModules);
        } catch (final IllegalStateException e) {
            logger.debug(e.getMessage(), e);
        }
    }

    public void endPhoneHome() {
        if (currentPhoneHomeResponse != null) {
            final Boolean result = currentPhoneHomeResponse.getImmediateResult();
            logger.trace(String.format("Phone home ended with result: %b", result));
        }
    }
}
