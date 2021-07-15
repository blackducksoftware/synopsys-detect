/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.phonehome;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.status.Operation;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.phonehome.PhoneHomeResponse;

public abstract class PhoneHomeManager {
    private final Logger logger = LoggerFactory.getLogger(PhoneHomeManager.class);

    protected final DetectInfo detectInfo;
    protected final EventSystem eventSystem;
    protected PhoneHomeResponse currentPhoneHomeResponse;
    protected Map<String, String> additionalMetaData;

    public PhoneHomeManager(Map<String, String> additionalMetaData, DetectInfo detectInfo, EventSystem eventSystem) {
        this.detectInfo = detectInfo;
        this.eventSystem = eventSystem;
        this.additionalMetaData = additionalMetaData;

        eventSystem.registerListener(Event.ApplicableCompleted, this::startPhoneHome);
        eventSystem.registerListener(Event.DetectorsProfiled, event -> startPhoneHome(event.getAggregateTimings()));
        eventSystem.registerListener(Event.DetectOperation, this::appendOperationMetadata);
    }

    public abstract PhoneHomeResponse phoneHome(Map<String, String> metadata, String... artifactModules);

    public void startPhoneHome() {
        // detect will attempt to phone home twice - once upon startup and
        // once upon getting all the detector metadata.
        //
        // We would prefer to always wait for all the detector metadata, but
        // sometimes there is not enough time to complete a phone home before
        // detect exits (if the scanner is disabled, for example).
        safelyPhoneHome(new HashMap<>());
    }

    private void startPhoneHome(Set<DetectorType> applicableDetectorTypes) {
        if (applicableDetectorTypes != null) {
            String[] artifactModules = applicableDetectorTypes.stream().map(DetectorType::toString).toArray(String[]::new);
            safelyPhoneHome(new HashMap<>(), artifactModules);
        }
    }

    public void startPhoneHome(Map<DetectorType, Long> aggregateTimes) {
        Map<String, String> metadata = new HashMap<>();
        if (aggregateTimes != null) {
            String applicableBomToolsString = aggregateTimes.keySet().stream()
                                                  .map(it -> String.format("%s:%s", it.toString(), aggregateTimes.get(it)))
                                                  .collect(Collectors.joining(","));
            metadata.put("detectorTimes", applicableBomToolsString);
        }
        safelyPhoneHome(metadata);
    }

    private void safelyPhoneHome(Map<String, String> metadata, String... artifactModules) {
        endPhoneHome();
        try {
            currentPhoneHomeResponse = phoneHome(metadata, artifactModules);
        } catch (IllegalStateException e) {
            logger.debug(e.getMessage(), e);
        }
    }

    public void endPhoneHome() {
        if (currentPhoneHomeResponse != null) {
            Boolean result = currentPhoneHomeResponse.getImmediateResult();
            logger.trace(String.format("Phone home ended with result: %b", result));
        }
    }

    private void appendOperationMetadata(Operation operation) {
        Optional<String> phoneHomeKey = operation.getPhoneHomeKey();
        if (phoneHomeKey.isPresent()) {
            String status = StatusType.FAILURE.equals(operation.getStatusType()) ? ":" + operation.getStatusType() : ""; // Assume success, mention failure.
            String runTime = operation.getEndTime()
                                 .map(endTime -> Duration.between(operation.getStartTime(), endTime))
                                 .map(duration -> Long.toString(duration.toMillis()))
                                 .map(duration -> ":" + duration)
                                 .orElse("");
            additionalMetaData.compute("operations", (k, currentValue) -> {
                String operationMetadata = phoneHomeKey.get() + status + runTime;
                return formatMetadataValue(currentValue, operationMetadata);
            });
            logger.info(additionalMetaData.get("operations"));
        }
    }

    private String formatMetadataValue(String currentValue, String newValue) {
        if (StringUtils.isBlank(currentValue)) {
            return newValue;
        }
        // Filters duplicate operations
        if (!currentValue.contains(newValue)) {
            return String.format("%s,%s", currentValue, newValue);
        }
        return currentValue;
    }
}
