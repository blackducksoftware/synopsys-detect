package com.synopsys.integration.detectable.detectables.pnpm.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmPackageInfo;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.process.PnpmLinkedPackageResolver;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.process.PnpmYamlTransformer;
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
}
