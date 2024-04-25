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

/**
 * ScanSettings is a data model used to store the properties and scan targets used by Detect for the given or
 * derived <code>detect.source.path</code> value, when Detect's autonomous scanning mode is enabled.
 * <br>If Detect has access to a scan settings JSON file from a previous run on the <code>detect.source.path</code>,
 * the ScanSettings model will be initialized by deserializing this scan settings JSON file.
 * <br>The ScanSettings model will also be updated based on the explicit properties provided
 * by the user, and the autonomous decisions taken by Detect during the boot stage.
 * <br>Once Detect's autonomous decisions and configurations are ready,
 * these scan settings will be saved in an updated scan settings JSON file in the <code>blackduck</code> directory.
 */
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
    private SortedMap<String, String> detectorSharedProperties = new TreeMap<>();
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

    public SortedMap<String, String> getDetectorSharedProperties() {
        return detectorSharedProperties;
    }

    public void setDetectorSharedProperties(final SortedMap<String, String> detectorSharedProperties) {
        this.detectorSharedProperties = detectorSharedProperties;
    }

    public SortedSet<ScanType> getScanTypes() {
        return scanTypes;
    }

    public void setScanTypes(final SortedSet<ScanType> scanTypes) {
        this.scanTypes = scanTypes;
    }
}
