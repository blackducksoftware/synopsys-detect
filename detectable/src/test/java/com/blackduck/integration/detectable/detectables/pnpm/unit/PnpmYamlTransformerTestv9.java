package com.blackduck.integration.detectable.detectables.pnpm.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map.Entry;

import com.blackduck.integration.detectable.detectables.pnpm.lockfile.model.PnpmPackageInfo;
import com.blackduck.integration.detectable.detectables.pnpm.lockfile.process.PnpmLinkedPackageResolver;
import com.blackduck.integration.detectable.detectables.pnpm.lockfile.process.PnpmYamlTransformer;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.util.NameVersion;

public class PnpmYamlTransformerTestv9 {
    PnpmYamlTransformer instance = new PnpmYamlTransformer(null, "9");
    
    @Test
    public void testGetDependencyInformationMethod() throws Exception {
        // Mock data, for v9 the package info is empty with data in the snapshot
        PnpmPackageInfo packageInfo = new PnpmPackageInfo();
        Entry<String, PnpmPackageInfo> packageEntry = 
                new SimpleEntry<>("@ampproject/remapping@2.3.0", packageInfo);

        Map<String, PnpmPackageInfo> snapshots = new HashMap<>();
        PnpmPackageInfo snapshotEntry = new PnpmPackageInfo();
        Map<String, String> dependencies = new HashMap<>();
        dependencies.put("random/dependency", "0.3.5");
        snapshotEntry.dependencies = dependencies;
        snapshots.put("@ampproject/remapping@2.3.0", snapshotEntry);

        Class<?>[] parameterTypes = new Class<?>[] { Entry.class, Map.class };
        Method method = PnpmYamlTransformer.class.getDeclaredMethod("getDependencyInformation", parameterTypes);
        method.setAccessible(true);

        PnpmPackageInfo result = (PnpmPackageInfo) method.invoke(instance, packageEntry, snapshots);

        // Check that the method was able to link the packageEntry to the snapshotEntry 
        assertEquals(snapshotEntry, result);
    }
    
    @Test
    public void testConvertRawEntryToPackageId() throws Exception {
        Class<?>[] parameterTypes = new Class<?>[] { String.class, String.class, PnpmLinkedPackageResolver.class, String.class };
        Method method = PnpmYamlTransformer.class.getDeclaredMethod("convertRawEntryToPackageId", parameterTypes);
        method.setAccessible(true);
        
        String result = (String) method.invoke(instance, "express", "4.19.2", null, null);
        
        assertEquals("express@4.19.2", result);
    }
    
    @Test
    public void testParseNameVersionFromId() throws Exception {
        Class<?>[] parameterTypes = new Class<?>[] { String.class };
        Method method = PnpmYamlTransformer.class.getDeclaredMethod("parseNameVersionFromId", parameterTypes);
        method.setAccessible(true);
        
        Optional<NameVersion> optional = 
                (Optional<NameVersion>) method.invoke(instance, "@ampproject/remapping@2.3.0");
        
        NameVersion result = optional.get();
        
        assertEquals("@ampproject/remapping", result.getName());
        assertEquals("2.3.0", result.getVersion());
    }
    
    @Test
    public void testFileAndUrlVersionCreation() throws Exception {
        Method method = PnpmYamlTransformer.class.getDeclaredMethod("buildDependencyFromPackageEntry", Map.Entry.class);
        method.setAccessible(true);
        
        Map<String, PnpmPackageInfo> packageEntry = new HashMap<>();
        PnpmPackageInfo packageInfo = new PnpmPackageInfo();
        packageInfo.version = "10.3.1";
        packageEntry.put("@use-gesture/vanilla@file:vanilla.tgz", packageInfo);
        
        for (Map.Entry<String, PnpmPackageInfo> entry : packageEntry.entrySet()) {
            Optional<Dependency> optDependency = (Optional<Dependency>) method.invoke(instance, entry);
            Dependency dependency = optDependency.get();
            
            assertEquals("@use-gesture/vanilla", dependency.getName());
            assertEquals("10.3.1", dependency.getVersion());
        }
    }
}
