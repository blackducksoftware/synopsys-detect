package com.blackduck.integration.detect.lifecycle.autonomous.model;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Optional;

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
     * Sorted set of {@link PackageManagerType} objects.
     * Each <code>DetectorType</code> object stores properties that are only applicable to that detector type.
     */
    private SortedSet<PackageManagerType> detectorTypes = new TreeSet<>();

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

    public SortedSet<PackageManagerType> getDetectorTypes() {
        return detectorTypes;
    }

    public void setDetectorTypes(final SortedSet<PackageManagerType> detectorTypes) {
        this.detectorTypes = detectorTypes;
    }

    public SortedSet<ScanType> getScanTypes() {
        return scanTypes;
    }

    public void setScanTypes(final SortedSet<ScanType> scanTypes) {
        this.scanTypes = scanTypes;
    }

    public ScanType getScanTypeWithName(String scanTypeName) {
        Optional<ScanType> scanType = scanTypes.stream().filter(scanTool -> scanTool.getScanTypeName().equals(scanTypeName)).findFirst();

        if(scanType.isPresent()) {
            return scanType.get();
        } else {
            ScanType newScanType = new ScanType(scanTypeName, new TreeMap<>(), new TreeSet<>());
            scanTypes.add(newScanType);
            return newScanType;
        }
    }

    public PackageManagerType getDetectorTypeWithName(String detectorTypeName) {
        Optional<PackageManagerType> detectorType = detectorTypes.stream().filter(detector -> detector.getDetectorTypeName().equals(detectorTypeName)).findFirst();

        if(detectorType.isPresent()) {
            return detectorType.get();
        } else {
            PackageManagerType newDetectorType = new PackageManagerType(detectorTypeName, new TreeMap<>(), new TreeSet<>());
            detectorTypes.add(newDetectorType);
            return newDetectorType;
        }
    }

    public boolean isEmpty() {
        return globalDetectProperties.isEmpty() && detectorTypes.isEmpty() && scanTypes.isEmpty();
    }
}
