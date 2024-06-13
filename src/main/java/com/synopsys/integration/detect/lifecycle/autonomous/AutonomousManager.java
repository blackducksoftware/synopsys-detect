package com.synopsys.integration.detect.lifecycle.autonomous;

import com.synopsys.integration.detect.configuration.DetectPropertyConfiguration;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.List;
import java.util.SortedSet;
import java.util.UUID;
import java.util.Arrays;
import java.util.Set;
import java.util.Map;

import com.synopsys.integration.detect.lifecycle.autonomous.model.ScanType;
import com.synopsys.integration.detector.base.DetectorType;
import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detect.lifecycle.autonomous.model.ScanSettings;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;

public class AutonomousManager {
    private final DirectoryManager directoryManager;
    private final String detectSourcePath;
    private String hashedScanSettingsFileName;
    private File scanSettingsTargetFile;
    private ScanSettings scanSettings;
    private boolean autonomousScanEnabled;
    private String blackDuckScanMode;
    private final DetectPropertyConfiguration detectConfiguration;
    private SortedMap<String, String> userProvidedProperties = new TreeMap<>();
    private SortedMap<String, String> globalProperties = new TreeMap<>();
    private SortedMap<String, String> detectorSharedProperties = new TreeMap<>();
    private static final List<String> propertiesNotAutonomous = Arrays.asList("blackduck.api.token", "detect.diagnostic", "detect.source.path", "detect.tools");

    public AutonomousManager(
            DirectoryManager directoryManager,
            DetectPropertyConfiguration detectConfiguration,
            boolean autonomousScanEnabled,
            SortedMap<String, String> userProvidedProperties) {
        this.detectConfiguration = detectConfiguration;
        this.directoryManager = directoryManager;
        this.autonomousScanEnabled = autonomousScanEnabled;
        this.userProvidedProperties = userProvidedProperties;
        detectSourcePath = directoryManager.getSourceDirectory().getPath();
        if(autonomousScanEnabled) {
            createScanSettingsTargetFile();
            scanSettings = initializeScanSettingsModel();
        }
    }

    public boolean getAutonomousScanEnabled() {
        return autonomousScanEnabled;
    }

    public ScanSettings getScanSettingsModel() {
        return scanSettings;
    }

    public String getHashedScanSettingsFileName() {
        return hashedScanSettingsFileName;
    }

    public boolean isScanSettingsFilePresent() {
        return scanSettingsTargetFile != null && scanSettingsTargetFile.exists();
    }

    public void setBlackDuckScanMode(String scanMode) {
        this.blackDuckScanMode = scanMode;
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

    private void createScanSettingsTargetFile() {
        hashedScanSettingsFileName = StringUtils.join(UUID.nameUUIDFromBytes(detectSourcePath.getBytes()).toString(), ".json");
        File scanSettingsTargetDir = directoryManager.getScanSettingsOutputDirectory();
        scanSettingsTargetFile = new File(scanSettingsTargetDir, hashedScanSettingsFileName);
    }
    
    public Map<DetectTool, Set<String>> getScanTypeMap(boolean hasImageOrTar) {
        ScanTypeDecider autoDetectTool = new ScanTypeDecider();
        return autoDetectTool.decide(hasImageOrTar, detectConfiguration);
    }

    public SortedMap<String, String> getAllScanSettingsProperties() {
        SortedMap<String, String> scanSettingsProperties = new TreeMap<>();
        detectorSharedProperties = scanSettings.getDetectorSharedProperties();
        globalProperties = scanSettings.getGlobalDetectProperties();
        scanSettingsProperties.putAll(detectorSharedProperties);
        scanSettingsProperties.putAll(globalProperties);
        scanSettings.getScanTypes().forEach(scanType ->  {
            scanSettingsProperties.putAll(scanType.getScanProperties());
        });

        return scanSettingsProperties;
    }

    public void updateScanSettingsProperties(SortedMap<String, String> defaultPropertiesMap, List<String> adoptedScanTypes) {
        userProvidedProperties.forEach((propertyKey, propertyValue) -> {
            Optional<String> scanTypeValue = findScanType(adoptedScanTypes, propertyKey);
            determinePropertyTypeAndUpdate(propertyKey, propertyValue, scanTypeValue, true);
        });
        defaultPropertiesMap.forEach((propertyKey, propertyValue) -> {
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

    public void updateScanTargets(SortedMap<String, SortedSet<String>> packageManagerTargets, Map<DetectTool, Set<String>> scanTypeTargets) {
        packageManagerTargets.forEach((packageManager, scanTargets) -> {
            ScanType scanType1 = scanSettings.getScanTypeWithName(packageManager);
            scanType1.getScanTargets().addAll(scanTargets);
        });

        scanTypeTargets.forEach((scanType, scanTargets) -> {
            ScanType scanType1 = scanSettings.getScanTypeWithName(scanType.toString());
            scanType1.getScanTargets().addAll(scanTargets);
        });
    }

    private boolean isDetectorTypeProperty(String propertyKey, String propertyValue) {
        boolean isDetectorToolIncluded = scanSettings.getScanTypes().stream().anyMatch(scanType -> scanType.getScanTypeName().equals("DETECTOR"));
        return (propertyKey.contains("detector") || propertyKey.equals("detect.accuracy.required")) && !propertyValue.isEmpty() && isDetectorToolIncluded;
    }

    private void updateDetectorProperties(String propertyKey, String propertyValue, boolean userProvidedProperty) {
        if(userProvidedProperty) {
            detectorSharedProperties.put(propertyKey, propertyValue);
        } else {
            detectorSharedProperties.putIfAbsent(propertyKey, propertyValue);
        }
    }

    private boolean isGlobalTypeProperty(String propertyKey, String propertyValue) {
        String tempKey = propertyKey.replace(".","_");
        boolean detectorProperty = Arrays.stream(DetectorType.values()).anyMatch(value -> propertyKey.contains(value.toString().toLowerCase()) || tempKey.contains(value.toString().toLowerCase()));
        return !propertiesNotAutonomous.contains(propertyKey) && !detectorProperty && !propertyValue.isEmpty() && !propertyKey.contains("detector") && !propertyKey.contains("ruby");
    }

    private void updateGlobalProperties(String propertyKey, String propertyValue, boolean userProvidedProperty) {
        if(propertyKey.equals("detect.blackduck.scan.mode")) {
            propertyValue = blackDuckScanMode;
            globalProperties.put(propertyKey, propertyValue);
        } else if(userProvidedProperty) {
            globalProperties.put(propertyKey, propertyValue);
        } else {
            globalProperties.putIfAbsent(propertyKey, propertyValue);
        }
    }
}
