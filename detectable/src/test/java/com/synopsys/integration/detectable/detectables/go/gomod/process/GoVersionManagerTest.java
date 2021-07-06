package com.synopsys.integration.detectable.detectables.go.gomod.process;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListUJsonData;
import com.synopsys.integration.detectable.detectables.go.gomod.model.ReplaceData;

class GoVersionManagerTest {
    static String MODULE_A_PATH = "example.io/module/a";
    static String MODULE_B_PATH = "example.io/module/b";
    static GoVersionManager goVersionManager;

    @BeforeAll
    static void init() {
        GoListUJsonData moduleA = new GoListUJsonData();
        moduleA.setPath(MODULE_A_PATH);
        moduleA.setVersion("1.0.0");

        GoListUJsonData moduleB = new GoListUJsonData();
        moduleB.setPath(MODULE_B_PATH);
        moduleB.setVersion("2.0.0");
        ReplaceData replaceData = new ReplaceData();
        replaceData.setPath(MODULE_B_PATH);
        replaceData.setVersion("2.3.4");
        moduleB.setReplace(replaceData);

        List<GoListUJsonData> goListUJsonData = Arrays.asList(moduleA, moduleB);
        goVersionManager = new GoVersionManager(goListUJsonData);
    }

    @Test
    void nonExistentTest() {
        Optional<String> versionForModule = goVersionManager.getVersionForModule("non-existent.io/module");
        assertFalse(versionForModule.isPresent());
    }

    @Test
    void happyPathTest() {
        Optional<String> versionForModule = goVersionManager.getVersionForModule(MODULE_A_PATH);
        assertTrue(versionForModule.isPresent());
        assertEquals("1.0.0", versionForModule.get());
    }

    @Test
    void replacementTest() {
        Optional<String> versionForModule = goVersionManager.getVersionForModule(MODULE_B_PATH);
        assertTrue(versionForModule.isPresent());
        assertEquals("2.3.4", versionForModule.get(), "The version should be replaced.");
    }
}
