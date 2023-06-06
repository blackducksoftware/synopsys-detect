package com.synopsys.integration.detectable.detectables.npm.lockfile.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmDependencyConverter;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.PackageLock;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.PackageLockPackage;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockfilePackager;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

public class NpmDependencyConverterTest {

    @Test
    public void testLinkPackagesDependencies() {
        Gson gson = new Gson();
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        NpmDependencyConverter converter = new NpmDependencyConverter(externalIdFactory);
        NpmLockfilePackager packager = new NpmLockfilePackager(gson, externalIdFactory, null, null);
        
        String lockFileText = FunctionalTestFiles.asString("/npm/packages-linkage-test/package-lock.json");
        lockFileText = packager.removePathInfoFromPackageName(lockFileText);   
        PackageLock packageLock = gson.fromJson(lockFileText, PackageLock.class);  
        
        // In the supplied JSON there is an open source project connect that has a dependency
        // on the open source project finalhandler. Ensure that before linkage we are missing
        // this relationship and that after linkage connect shows finalhandler as a dependency.   
        String parentProject = "connect";
        String childProject = "finalhandler";
        
        PackageLockPackage connectLinkageState = packageLock.packages.get(parentProject);
        Assertions.assertTrue(connectLinkageState.packages == null ||
                !connectLinkageState.packages.containsKey(childProject));
        
        converter.linkPackagesDependencies(packageLock);
        connectLinkageState = packageLock.packages.get(parentProject);
        Assertions.assertTrue(connectLinkageState.packages.containsKey(childProject));
    }
}
