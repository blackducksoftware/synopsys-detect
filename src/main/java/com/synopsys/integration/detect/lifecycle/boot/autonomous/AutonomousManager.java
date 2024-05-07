package com.synopsys.integration.detect.lifecycle.boot.autonomous;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detect.lifecycle.boot.autonomous.model.ScanSettings;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;

public class AutonomousManager {

    private DirectoryManager directoryManager;
    private String detectSourcePath;
    private String hashedScanSettingsFileName;
    private File scanSettingsTargetFile;
    private ScanSettings scanSettings;

    public AutonomousManager(
        DirectoryManager directoryManager
    ) {
        this.directoryManager = directoryManager;

        detectSourcePath = directoryManager.getSourceDirectory().getPath();
        hashedScanSettingsFileName = StringUtils.join(UUID.nameUUIDFromBytes(detectSourcePath.getBytes()).toString(), ".json");

        File scanSettingsTargetDir = directoryManager.getScanSettingsOutputDirectory();
        scanSettingsTargetFile = new File(scanSettingsTargetDir, hashedScanSettingsFileName);

        scanSettings = initializeScanSettingsModel();
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
}
