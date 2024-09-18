package com.blackduck.integration.detect.lifecycle.autonomous;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;

import com.blackduck.integration.detect.lifecycle.autonomous.model.ScanSettings;
import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ScanSettingsSerializer {

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

    public static String serializeScanSettingsModel(ScanSettings scanSettings) {
        Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT)
            .setPrettyPrinting().create();
        return gson.toJson(scanSettings);
    }
}
