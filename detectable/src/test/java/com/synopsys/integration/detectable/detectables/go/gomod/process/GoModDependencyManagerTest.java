package com.synopsys.integration.detectable.detectables.go.gomod.process;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListAllData;
import com.synopsys.integration.detectable.detectables.go.gomod.model.ReplaceData;

class GoModDependencyManagerTest {
    static String MODULE_A_PATH = "example.io/module/a";
    static String MODULE_B_PATH = "example.io/module/b";
    static String MODULE_NO_VERSION_PATH = "example.io/module/no_version";
    static GoModDependencyManager goModDependencyManager;
    static ExternalIdFactory externalIdFactory;

    @BeforeAll
    static void init() {
        GoListAllData moduleA = new GoListAllData();
        moduleA.setPath(MODULE_A_PATH);
        moduleA.setVersion("1.0.0");

        GoListAllData moduleB = new GoListAllData();
        moduleB.setPath(MODULE_B_PATH);
        moduleB.setVersion("2.0.0");
        ReplaceData replaceData = new ReplaceData();
        replaceData.setPath(MODULE_A_PATH);
        replaceData.setVersion("2.3.4");
        moduleB.setReplace(replaceData);

        GoListAllData noVersionModule = new GoListAllData();
        noVersionModule.setPath(MODULE_NO_VERSION_PATH);
        // Explicitly not setting a version here.

        GoListAllData incompatibleModule = new GoListAllData();
        incompatibleModule.setPath("example.io/incompatible");
        incompatibleModule.setVersion("2.0.0+incompatible");

        GoListAllData gitModule = new GoListAllData();
        gitModule.setPath("example.io/hash");
        gitModule.setVersion("version_with_hash-123abc-456xyz");

        List<GoListAllData> goListAllData = Arrays.asList(moduleA, moduleB, incompatibleModule, gitModule, noVersionModule);
        externalIdFactory = new ExternalIdFactory();
        goModDependencyManager = new GoModDependencyManager(goListAllData, externalIdFactory);
    }

    @Test
    void versionWithIncompatible() {
        Dependency dependency = goModDependencyManager.getDependencyForModule("example.io/incompatible");
        Optional<String> versionForModule = Optional.ofNullable(dependency.getVersion());
        assertTrue(versionForModule.isPresent());
        assertEquals("2.0.0", versionForModule.get());
    }

    @Test
    void versionWithGitHash() {
        Dependency dependency = goModDependencyManager.getDependencyForModule("example.io/hash");
        Optional<String> versionForModule = Optional.ofNullable(dependency.getVersion());
        assertTrue(versionForModule.isPresent());
        assertEquals("456xyz", versionForModule.get());
    }

    @Test
    void nonExistentModuleVersionTest() {
        Dependency dependency = goModDependencyManager.getDependencyForModule("non-existent.io/module");
        Optional<String> versionForModule = Optional.ofNullable(dependency.getVersion());
        assertFalse(versionForModule.isPresent());
    }

    @Test
    void happyPathTest() {
        Dependency dependency = goModDependencyManager.getDependencyForModule(MODULE_A_PATH);
        Optional<String> versionForModule = Optional.ofNullable(dependency.getVersion());
        assertTrue(versionForModule.isPresent());
        assertEquals("1.0.0", versionForModule.get());
    }

    @Test
    void replacementTest() {
        Dependency dependencyForModule = goModDependencyManager.getDependencyForModule(MODULE_B_PATH);
        assertEquals("2.3.4", dependencyForModule.getVersion(), "The version should be replaced.");
        assertEquals(MODULE_A_PATH, dependencyForModule.getName(), "The path should be replaced.");
    }

    @Test
    void noVersionTest() {
        Dependency dependency = goModDependencyManager.getDependencyForModule(MODULE_NO_VERSION_PATH);
        Optional<String> versionForModule = Optional.ofNullable(dependency.getVersion());
        assertFalse(versionForModule.isPresent(), "This module should have no version information.");
    }

    @Test
    void nonExistentModulePathTest() {
        Dependency dependency = goModDependencyManager.getDependencyForModule("non-existent.io/module");
        Optional<String> pathForModule = Optional.ofNullable(dependency.getName());
        Optional<String> versionForModule = Optional.ofNullable(dependency.getVersion());
        assertTrue(pathForModule.isPresent());
        assertFalse(versionForModule.isPresent(), "If a module cannot be mapped to a dependency, a default dependency should be created with a null version.");
    }
}
