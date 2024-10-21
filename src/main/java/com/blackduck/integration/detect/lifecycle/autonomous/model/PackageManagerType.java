package com.blackduck.integration.detect.lifecycle.autonomous.model;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jetbrains.annotations.NotNull;

public class PackageManagerType implements Comparable<PackageManagerType> {
    /**
     * Name of the detector that is applicable.
     * Example: "GRADLE", "MAVEN", "CONAN".
     */
    private String detectorTypeName;
    /**
     * Sorted property-value hash map for properties that are applicable only to the current detector type.
     * Accepts property name and value as a <code>String</code>.
     * Example: <code>detectorProperties.put("detect.maven.path", "/path/to/maven/executable")</code>
     */
    private SortedMap<String, String> detectorProperties = new TreeMap<>();
    /**
     * Sorted set of the absolute paths to the target that is being scanned.
     * Accepts a <code>String</code> value for the target's path.
     * This is the set of path(s) to the package manager file(s).
     */
    private SortedSet<String> detectorScanTargets = new TreeSet<>();

    @Override
    public int compareTo(@NotNull PackageManagerType o) {
        return detectorTypeName.compareTo(o.detectorTypeName);
    }

    public PackageManagerType(String detectorTypeName, SortedMap<String, String> detectorProperties, SortedSet<String> detectorScanTargets) {
        this.detectorTypeName = detectorTypeName;
        this.detectorProperties = detectorProperties;
        this.detectorScanTargets = detectorScanTargets;
    }

    public String getDetectorTypeName() {
        return detectorTypeName;
    }

    public void setDetectorTypeName(final String detectorTypeName) {
        this.detectorTypeName = detectorTypeName;
    }

    public SortedMap<String, String> getDetectorProperties() {
        return detectorProperties;
    }

    public void setDetectorProperties(final SortedMap<String, String> detectorProperties) {
        this.detectorProperties = detectorProperties;
    }

    public SortedSet<String> getDetectorScanTargets() {
        return detectorScanTargets;
    }

    public void setDetectorScanTargets(final SortedSet<String> detectorScanTargets) {
        this.detectorScanTargets = detectorScanTargets;
    }
}
