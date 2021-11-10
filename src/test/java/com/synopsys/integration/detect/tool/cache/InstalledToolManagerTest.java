package com.synopsys.integration.detect.tool.cache;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InstalledToolManagerTest {
    @Test
    public void testSaveAndUpdateData() {
        InstalledToolManager installedToolManager = new InstalledToolManager();
        installedToolManager.saveInstalledToolLocation("tool", "place");

        Map<String, String> preExistingData = new HashMap<>();
        preExistingData.put("tool2", "place2");

        InstalledToolData preExistingInstalledToolData = new InstalledToolData();
        preExistingInstalledToolData.toolData = preExistingData;

        installedToolManager.addPreExistingInstallData(preExistingInstalledToolData);

        InstalledToolData installedToolData = installedToolManager.getInstalledToolData();
        Assertions.assertTrue(installedToolData.toolData.entrySet().stream()
            .anyMatch(entry -> entry.getKey().equals("tool") && entry.getValue().equals("place"))
        );
        Assertions.assertTrue(installedToolData.toolData.entrySet().stream()
            .anyMatch(entry -> entry.getKey().equals("tool2") && entry.getValue().equals("place2"))
        );
    }
}
