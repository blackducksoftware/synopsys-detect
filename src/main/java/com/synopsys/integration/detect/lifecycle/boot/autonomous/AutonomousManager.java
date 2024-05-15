package com.synopsys.integration.detect.lifecycle.boot.autonomous;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.List;
import java.util.SortedSet;
import java.util.UUID;
import java.util.Optional;
import java.util.Arrays;
import java.util.List;

import com.synopsys.integration.detect.lifecycle.boot.autonomous.model.ScanType;
import com.synopsys.integration.detector.base.DetectorType;
import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detect.lifecycle.boot.autonomous.model.ScanSettings;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;


public class AutonomousManager {

    private DirectoryManager directoryManager;
    private boolean autonomousScanEnabled = false;
    private String detectSourcePath;
    private String hashedScanSettingsFileName;
    private File scanSettingsTargetFile;
    private ScanSettings scanSettings;
    private SortedMap<String, String> userProvidedProperties = new TreeMap<>();

    private SortedMap<String, String> globalProperties = new TreeMap<>();

    private SortedMap<String, String> detectorSharedProperties = new TreeMap<>();

    private static final List<String> propertiesNotAutonomous = Arrays.asList("blackduck.api.token", "detect.diagnostic");

    public AutonomousManager(
        DirectoryManager directoryManager,
        boolean autonomousScanEnabled,
        SortedMap<String, String> userProvidedProperties
    ) {
        this.autonomousScanEnabled = autonomousScanEnabled;
        this.userProvidedProperties = userProvidedProperties;
        this.directoryManager = directoryManager;

        if(autonomousScanEnabled) {
            detectSourcePath = directoryManager.getSourceDirectory().getPath();
            hashedScanSettingsFileName = StringUtils.join(UUID.nameUUIDFromBytes(detectSourcePath.getBytes()).toString(), ".json");

            File scanSettingsTargetDir = directoryManager.getScanSettingsOutputDirectory();
            scanSettingsTargetFile = new File(scanSettingsTargetDir, hashedScanSettingsFileName);

            scanSettings = initializeScanSettingsModel();
            detectorSharedProperties = scanSettings.getDetectorSharedProperties();
            globalProperties = scanSettings.getGlobalDetectProperties();
        }
    }

    public boolean getAutonomousScanEnabled() {
        return autonomousScanEnabled;
    }

    public ScanSettings getScanSettingsModel() {
        return scanSettings;
    }

    public void updateScanSettingsModel(ScanSettings scanSettings) {
        this.scanSettings = scanSettings;
    }

    public String getHashedScanSettingsFileName() {
        return hashedScanSettingsFileName;
    }

    public boolean isScanSettingsFilePresent() {
        return scanSettingsTargetFile != null && scanSettingsTargetFile.exists();
    }

    public void writeScanSettingsModelToTarget() throws IOException {
        String serializedScanSettings = ScanSettingsSerializer.serializeScanSettingsModel(scanSettings);
        try (FileWriter fw = new FileWriter(scanSettingsTargetFile)) {
            fw.write(serializedScanSettings);
            fw.flush();
        } catch (IOException e) {
            throw e;
        }
    }

    private ScanSettings initializeScanSettingsModel() {
        if (isScanSettingsFilePresent()) {
            return ScanSettingsSerializer.deserializeScanSettingsFile(scanSettingsTargetFile);
        }
        return new ScanSettings();
    }

    public SortedMap<String, String> getAllScanSettingsProperties() {
        SortedMap<String, String> scanSettingsProperties = new TreeMap<>();
        scanSettingsProperties.putAll(scanSettings.getDetectorSharedProperties());
        scanSettingsProperties.putAll(scanSettings.getGlobalDetectProperties());
        scanSettings.getScanTypes().forEach(scanType ->  {
            scanSettingsProperties.putAll(scanType.getScanProperties());
        });

        return scanSettingsProperties;
    }

    public void updateScanSettingsProperties(SortedMap<String, String> propertiesMap, List<String> adoptedScanTypes) {
        userProvidedProperties.forEach((propertyKey, propertyValue) -> {
            Optional<String> scanTypeValue = findScanType(adoptedScanTypes, propertyKey);
            determinePropertyTypeAndUpdate(propertyKey, propertyValue, scanTypeValue, true);
        });
        propertiesMap.forEach((propertyKey, propertyValue) -> {
            Optional<String> scanTypeValue = findScanType(adoptedScanTypes, propertyKey);
            determinePropertyTypeAndUpdate(propertyKey, propertyValue, scanTypeValue, false);
        });

        scanSettings.setDetectorSharedProperties(detectorSharedProperties);
        scanSettings.setGlobalDetectProperties(globalProperties);
    }

    private void determinePropertyTypeAndUpdate(String propertyKey, String propertyValue, Optional<String> scanTypeValue, boolean userProvidedProperty) {
        if(isDetectorTypeProperty(propertyKey, propertyValue)) {
            updateDetectorProperties(propertyKey, propertyValue, userProvidedProperty);
        } else if (scanTypeValue.isPresent()) {
            String scanTypeName = scanTypeValue.get();
            ScanType scanType = scanSettings.getScanTypeWithName(scanTypeName);
            if(userProvidedProperty && !propertyValue.isEmpty()) {
                scanType.getScanProperties().put(propertyKey, propertyValue);
            } else if(!scanType.getScanProperties().containsKey(propertyKey) && !propertyValue.isEmpty()) {
                scanType.getScanProperties().put(propertyKey, propertyValue);
            }
        } else if(isGlobalTypeProperty(propertyKey, propertyValue)) {
            updateGlobalProperties(propertyKey, propertyValue, userProvidedProperty);
        }
    }

    private Optional<String> findScanType(List<String> adoptedScanTypes, String key) {
        return adoptedScanTypes.stream().filter(tool -> {
            String tempKey = key.replace(".","_");
            return tempKey.contains(tool.toLowerCase());
        }).findFirst();
    }

    public void updateScanTargets(SortedMap<String, SortedSet<String>> scanTargetMap) {
        scanTargetMap.forEach((scanType, scanTargets) -> {
            ScanType scanType1 = scanSettings.getScanTypeWithName(scanType);
            scanType1.getScanTargets().addAll(scanTargets);
        });
    }

    private boolean isDetectorTypeProperty(String propertyKey, String propertyValue) {
        return (propertyKey.contains("detector") || propertyKey.equals("detect.accuracy.required")) && !propertyValue.isEmpty();
    }

    private void updateDetectorProperties(String propertyKey, String propertyValue, boolean userProvidedProperty) {
        if(userProvidedProperty) {
            detectorSharedProperties.put(propertyKey, propertyValue);
        } else if(!detectorSharedProperties.containsKey(propertyKey)) {
            detectorSharedProperties.put(propertyKey, propertyValue);
        }
    }

    private boolean isGlobalTypeProperty(String propertyKey, String propertyValue) {
        boolean detectorProperty = Arrays.stream(DetectorType.values()).anyMatch(value -> propertyKey.contains(value.toString().toLowerCase()));
        return !propertiesNotAutonomous.contains(propertyKey) && !detectorProperty && !propertyValue.isEmpty();
    }

    private void updateGlobalProperties(String propertyKey, String propertyValue, boolean userProvidedProperty) {
        if(userProvidedProperty) {
            globalProperties.put(propertyKey, propertyValue);
        } else if(!globalProperties.containsKey(propertyKey)) {
            globalProperties.put(propertyKey, propertyValue);
        }
    }
}
