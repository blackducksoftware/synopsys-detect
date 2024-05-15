package com.synopsys.integration.detect.lifecycle.boot.autonomous.model;

import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jetbrains.annotations.NotNull;

public class ScanType implements Comparable<ScanType> {
    /**
     * Name of the scan tool or detector that is applicable.
     * Example: "SIGNATURE_SCAN" or "MAVEN".
     */
    private String scanTypeName;
    /**
     * Sorted property-value hash map for properties that are applicable only to the current scan type.
     * Accepts property name and value as a <code>String</code>.
     * Example: <code>scanProperties.put("detect.maven.path", "/path/to/maven/executable")</code>
     */
    private SortedMap<String, String> scanProperties = new TreeMap<>();
    /**
     * Sorted set of the absolute paths to the target that is being scanned.
     * Accepts a <code>String</code> value for the target's path.
     * For detectors, this is the set of path(s) to the package manager file(s).
     * For binary/container scan, this is the set of path(s) to the binary/image to be scanned.
     * <br>Example 1: <code>scanTargets.add("/path/to/build.gradle")</code>
     * <br>Example 2: <code>scanTargets.add("/path/to/binary_1.exe")</code>
     */
    private SortedSet<String> scanTargets = new TreeSet<>();

    @Override
    public int compareTo(@NotNull ScanType o) {
        return scanTypeName.compareTo(o.scanTypeName);
    }

    public ScanType(String scanTypeName, SortedMap<String, String> scanProperties, SortedSet<String> scanTargets) {
        this.scanTypeName = scanTypeName;
        this.scanTargets = scanTargets;
        this.scanProperties = scanProperties;
    }

    public String getScanTypeName() {
        return scanTypeName;
    }

    public void setScanTypeName(final String scanTypeName) {
        this.scanTypeName = scanTypeName;
    }

    public Map<String, String> getScanProperties() {
        return scanProperties;
    }

    public void setScanProperties(final SortedMap<String, String> scanProperties) {
        this.scanProperties = scanProperties;
    }

    public SortedSet<String> getScanTargets() {
        return scanTargets;
    }

    public void setScanTargets(final SortedSet<String> scanTargets) {
        this.scanTargets = scanTargets;
    }
}
