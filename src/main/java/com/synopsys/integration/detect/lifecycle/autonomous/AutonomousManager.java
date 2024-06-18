package com.synopsys.integration.detect.lifecycle.autonomous;

import com.synopsys.integration.detect.configuration.DetectPropertyConfiguration;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.List;
import java.util.SortedSet;
import java.util.UUID;
import java.util.Arrays;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;

import com.synopsys.integration.detect.lifecycle.autonomous.model.PackageManagerType;
import com.synopsys.integration.detect.lifecycle.autonomous.model.ScanType;
import com.synopsys.integration.detector.base.DetectorType;
import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detect.lifecycle.autonomous.model.ScanSettings;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutonomousManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DirectoryManager directoryManager;
    private final String detectSourcePath;
    private String hashedScanSettingsFileName;
    private File scanSettingsTargetFile;
    private ScanSettings scanSettings;
    private boolean autonomousScanEnabled;
    private String blackDuckScanMode;
    private final DetectPropertyConfiguration detectConfiguration;
    private SortedMap<String, String> userProvidedProperties = new TreeMap<>();
    private SortedMap<String, String> allProperties = new TreeMap<>();
    private Set<String> decidedScanTypes = new HashSet<>();
    private Set<String> decidedDetectorTypes = new HashSet<>();
    private static final List<String> propertiesNotAutonomous = Arrays.asList("blackduck.api.token", "detect.diagnostic", "detect.source.path", "blackduck.proxy.password");

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
            logger.info("Autonomous Scan mode is enabled.");
            createScanSettingsTargetFile();
            scanSettings = initializeScanSettingsModel();
            initializeProperties();
        }
    }

    public boolean getAutonomousScanEnabled() {
        return autonomousScanEnabled;
    }

    public ScanSettings getScanSettingsModel() {
        return scanSettings;
    }

    public File getScanSettingsTargetFile() {
        return scanSettingsTargetFile;
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
        savePropertiesToModel();
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
            logger.debug("Found previous scan settings file at " + scanSettingsTargetFile.getAbsolutePath() + " and will be used for making autonomous scan decisions.");
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
        return autoDetectTool.decide(hasImageOrTar, detectConfiguration, Paths.get(detectSourcePath));
    }

    private void initializeProperties() {
        allProperties.putAll(scanSettings.getGlobalDetectProperties());
        scanSettings.getScanTypes().forEach(scanType ->  {
            allProperties.putAll(scanType.getScanProperties());
            scanType.getScanProperties().clear();
        });
        scanSettings.getDetectorTypes().forEach(detectorType -> {
            allProperties.putAll(detectorType.getDetectorProperties());
            detectorType.getDetectorProperties().clear();
        });
        scanSettings.getGlobalDetectProperties().clear();
    }

    public SortedMap<String, String> getAllScanSettingsProperties() {
        return allProperties;
    }

    private void removeDeletedProperties(List<String> allPropertyKeys) {
        allProperties.entrySet().removeIf(entry -> !allPropertyKeys.contains(entry.getKey()));
    }

    public void removeExcludedToolsAndDetectors(Set<String> excludedScanTypes, Set<String> excludedDetectorTypes) {
        scanSettings.getScanTypes().removeIf(scanType -> excludedScanTypes.contains(scanType.getScanTypeName()));
        scanSettings.getDetectorTypes().removeIf(detectorType -> excludedDetectorTypes.contains(detectorType.getDetectorTypeName()));
    }

    public void updateScanSettingsProperties(SortedMap<String, String> defaultPropertiesMap, Set<String> adoptedScanTypes, Set<String> detectorTypes, List<String> allPropertyKeys) {
        removeDeletedProperties(allPropertyKeys);

        allProperties.putAll(userProvidedProperties);
        defaultPropertiesMap.forEach((propertyKey, propertyValue) -> {
            if(!propertyValue.isEmpty()) {
                allProperties.putIfAbsent(propertyKey, propertyValue);
            }
        });

        decidedScanTypes = adoptedScanTypes;
        decidedDetectorTypes = detectorTypes;
    }

    public void savePropertiesToModel() {
        allProperties.forEach((propertyKey, propertyValue) -> {
            Optional<String> scanTypeValue = findScanType(propertyKey);
            Optional<String> detectorTypeValue = findDetectorType(propertyKey);
            determinePropertyTypeAndUpdate(propertyKey, propertyValue, detectorTypeValue, scanTypeValue);
        });
    }

    private void determinePropertyTypeAndUpdate(String propertyKey, String propertyValue, Optional<String> detectorTypeValue, Optional<String> scanTypeValue) {
        if(detectorTypeValue.isPresent()) {
            updateScanOrDetectorProperty("Detector", detectorTypeValue.get(), propertyKey, propertyValue);
        } else if (scanTypeValue.isPresent()) {
            updateScanOrDetectorProperty("Scantype", scanTypeValue.get(), propertyKey, propertyValue);
        } else if(propertyKey.equals("detect.accuracy.required")) {
            updateScanOrDetectorProperty("Scantype", "DETECTOR", propertyKey, propertyValue);
        } else if(isGlobalTypeProperty(propertyKey, propertyValue)) {
            updateGlobalProperties(propertyKey, propertyValue);
        }
    }

    private Optional<String> findScanType(String key) {
        return decidedScanTypes.stream().filter(tool -> {
            String tempKey = key.replace(".","_");
            return tempKey.contains(tool.toLowerCase());
        }).findFirst();
    }

    private Optional<String> findDetectorType(String key) {
        return decidedDetectorTypes.stream().filter(detector -> {
            String tempKey = key.replace(".","_");
            return tempKey.contains(detector.toLowerCase());
        }).findFirst();
    }

    private void updateScanOrDetectorProperty(String propertyType, String propertyTypeName, String propertyKey, String propertyValue) {
        if(propertyType.equals("Detector")) {
            PackageManagerType packageManagerType = scanSettings.getDetectorTypeWithName(propertyTypeName);
            packageManagerType.getDetectorProperties().put(propertyKey, propertyValue);
        } else {
            ScanType scanType = scanSettings.getScanTypeWithName(propertyTypeName);
            scanType.getScanProperties().put(propertyKey, propertyValue);
        }
    }

    public void updateScanTargets(SortedMap<String, SortedSet<String>> packageManagerTargets, Map<DetectTool, Set<String>> scanTypeTargets) {
        packageManagerTargets.forEach((packageManager, scanTargets) -> {
            PackageManagerType packageManagerType = scanSettings.getDetectorTypeWithName(packageManager);
            packageManagerType.getDetectorScanTargets().addAll(scanTargets);
        });

        scanTypeTargets.forEach((scanType, scanTargets) -> {
            ScanType scanType1 = scanSettings.getScanTypeWithName(scanType.toString());
            scanType1.getScanTargets().addAll(scanTargets);
        });
    }

    private boolean isGlobalTypeProperty(String propertyKey, String propertyValue) {
        String tempKey = propertyKey.replace(".","_");
        boolean detectorProperty = Arrays.stream(DetectorType.values()).anyMatch(value -> propertyKey.contains(value.toString().toLowerCase()) || tempKey.contains(value.toString().toLowerCase()));
        return !propertiesNotAutonomous.contains(propertyKey) && !detectorProperty && !propertyValue.isEmpty() && !propertyKey.contains("detector") && !propertyKey.contains("ruby");
    }

    private void updateGlobalProperties(String propertyKey, String propertyValue) {
        if(propertyKey.equals("detect.blackduck.scan.mode")) {
            propertyValue = blackDuckScanMode;
            scanSettings.getGlobalDetectProperties().put(propertyKey, propertyValue);
        } else {
            scanSettings.getGlobalDetectProperties().put(propertyKey, propertyValue);
        }
    }

    public void updateUserProvidedBinaryScanTargets(List<File> binaryScanTargets) {
        ScanType scanType = scanSettings.getScanTypeWithName("BINARY_SCAN");
        binaryScanTargets.forEach(file -> scanType.getScanTargets().add(file.getAbsolutePath()));
    }
}
