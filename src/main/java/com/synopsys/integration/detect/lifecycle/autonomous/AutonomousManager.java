package com.synopsys.integration.detect.lifecycle.autonomous;

import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScan;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatch;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectPropertyConfiguration;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;
import java.util.Set;
import java.util.Map;
import java.util.TreeMap;
import java.util.SortedMap;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detect.lifecycle.autonomous.model.ScanSettings;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameGenerator;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;

public class AutonomousManager {

    private DirectoryManager directoryManager;
    private String detectSourcePath;
    private String hashedScanSettingsFileName;
    private File scanSettingsTargetFile;
    private ScanSettings scanSettings;
    private boolean autonomousScanEnabled;
    private final DetectConfigurationFactory detectConfigurationFactory;
    private final DetectPropertyConfiguration detectConfiguration;

    public AutonomousManager(
            DirectoryManager directoryManager,
            DetectConfigurationFactory detectConfigurationFactory,
            DetectPropertyConfiguration detectConfiguration,
            boolean autonomousScanEnabled) {
        this.detectConfiguration = detectConfiguration;
        this.detectConfigurationFactory = detectConfigurationFactory;
        this.directoryManager = directoryManager;
        this.autonomousScanEnabled = autonomousScanEnabled;
        detectSourcePath = directoryManager.getSourceDirectory().getPath();
        createScanSettingsTargetFile();
        scanSettings = initializeScanSettingsModel();
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

    public void writeScanSettingsModelToTarget(ScanSettings scanSettings) throws IOException {
        updateScanSettingsModel(scanSettings);
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
    
    public void runBinaryBatchScan(Map<DetectTool, Set<String>> scanTypeEvidenceMap) {
        BinaryScanBatch binaryScanBatch = new BinaryScanBatch();
        CodeLocationNameGenerator codeLocationNameGenerator = detectConfigurationFactory.createCodeLocationOverride()
            .map(CodeLocationNameGenerator::withOverride)
            .orElse(CodeLocationNameGenerator.withPrefixSuffix("prefix", "suffix"));// Temporary code - discard after move to DetectRun.
        CodeLocationNameManager codeLocationNameManager = new CodeLocationNameManager(codeLocationNameGenerator);
        for (String path : scanTypeEvidenceMap.get(DetectTool.BINARY_SCAN)) {
            File binaryScanFile = new File(path);
            String codeLocationName = codeLocationNameManager.createBinaryScanCodeLocationName(
                binaryScanFile,
                binaryScanFile.getName(),// should be project name?
                "1"//should be project version?
            );
            binaryScanBatch.addBinaryScan(new BinaryScan(new File(path), binaryScanFile.getName(), "1", codeLocationName));
        }
        // scan mode needed to determine binary batch upload process
        // default - call BinaryUploadOperation.uploadBinaryScanFiles(...)
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
}
