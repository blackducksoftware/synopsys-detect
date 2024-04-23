package com.synopsys.integration.detect.lifecycle.boot.autonomous;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.detect.lifecycle.boot.autonomous.model.ScanSettings;

public class ScanSettingsSerializer {
    private static ScanSettings scanSettings;
    private static final String SCAN_SETTINGS_TARGET_LOCATION = "";

    public void setScanSettings(final ScanSettings scanSettings) {
        this.scanSettings = scanSettings;
    }

    public static ScanSettings deserializeScanSettingsFile(File scanSettingsFile) {
        try {
            String scanSettingsString = FileUtils.readFileToString(scanSettingsFile, StandardCharsets.UTF_8);
            Gson gson = new GsonBuilder().create();
            ScanSettings scanSettings = gson.fromJson(scanSettingsString, ScanSettings.class);
            return scanSettings;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeScanSettingsFile() throws IOException {
        File scanSettingsFile = new File(SCAN_SETTINGS_TARGET_LOCATION);
        Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT)
            .setPrettyPrinting().create();
        String serializedOutput = gson.toJson(scanSettings);
        try (FileWriter fw = new FileWriter(scanSettingsFile)) {
            fw.write(serializedOutput);
            fw.flush();
        } catch (IOException e) {
            throw e;
        }
    }
}
