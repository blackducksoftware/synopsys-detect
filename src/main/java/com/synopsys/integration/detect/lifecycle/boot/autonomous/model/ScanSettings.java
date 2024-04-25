package com.synopsys.integration.detect.lifecycle.boot.autonomous.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections.set.AbstractSortedSetDecorator;

public class ScanSettings {
    /**
     * Sorted property-value hash map for Detect's globally applicable properties.
     * Accepts property name and value as a <code>String</code>.
     * <br>Example: <code>globalDetectProperties.put("detect.tools", "BINARY_SCAN,DETECTOR")</code>
     */
    private SortedMap<String, String> globalDetectProperties = new TreeMap<>();
    /**
     * Sorted property-value hash map for properties that are shared across all detectors.
     * Accepts property name and value as a <code>String</code>.
     * <br>Example: <code>globalDetectProperties.put("detect.detector.search.depth", "3")</code>
     */
    private Map<String, String> detectorSharedProperties = new HashMap<>();
    /**
     * Sorted set of {@link ScanType} objects.
     * Each <code>ScanType</code> object stores properties that are only applicable to that scan type.
     */
    private SortedSet<ScanType> scanTypes = new TreeSet<>();

    public SortedMap<String, String> getGlobalDetectProperties() {
        return globalDetectProperties;
    }

    public void setGlobalDetectProperties(final SortedMap<String, String> globalDetectProperties) {
        this.globalDetectProperties = globalDetectProperties;
    }

    public SortedSet<ScanType> getScanTypes() {
        return scanTypes;
    }

    public void setScanTypes(final SortedSet<ScanType> scanTypes) {
        this.scanTypes = scanTypes;
    }
}
