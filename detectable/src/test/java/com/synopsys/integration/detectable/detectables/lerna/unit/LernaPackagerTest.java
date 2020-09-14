package com.synopsys.integration.detectable.detectables.lerna.unit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.lerna.LernaDetectable;
import com.synopsys.integration.detectable.detectables.lerna.LernaOptions;
import com.synopsys.integration.detectable.detectables.lerna.LernaPackager;
import com.synopsys.integration.detectable.detectables.lerna.model.LernaPackage;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmLockfileOptions;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockfilePackager;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockOptions;
import com.synopsys.integration.detectable.detectables.yarn.YarnPackager;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnTransformer;

class LernaPackagerTest {

    @Test
    public void lernaNoErrorsTest() {
        FileFinder fileFinder = Mockito.mock(FileFinder.class);
        File sourceDirectory = new File("source");
        File lernaPackageDirectory = new File(sourceDirectory, "lernaPackageDirectory");
        
        File packageJsonFile = new File(lernaPackageDirectory, LernaDetectable.PACKAGE_JSON);
        File packageLockFile = new File(lernaPackageDirectory, LernaDetectable.PACKAGE_LOCK_JSON);
        Mockito.when(fileFinder.findFile(lernaPackageDirectory, LernaDetectable.PACKAGE_JSON)).thenReturn(packageJsonFile);
        Mockito.when(fileFinder.findFile(lernaPackageDirectory, LernaDetectable.PACKAGE_LOCK_JSON)).thenReturn(packageLockFile);
        Mockito.when(fileFinder.findFile(lernaPackageDirectory, LernaDetectable.SHRINKWRAP_JSON)).thenReturn(null);
        Mockito.when(fileFinder.findFile(lernaPackageDirectory, LernaDetectable.YARN_LOCK)).thenReturn(null);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();

        NpmLockfilePackager npmLockfilePackager = new NpmLockfilePackager(gson, externalIdFactory);
        NpmLockfileOptions npmLockfileOptions = new NpmLockfileOptions(true);

        YarnLockParser yarnLockParser = new YarnLockParser();
        YarnTransformer yarnTransformer = new YarnTransformer(externalIdFactory);
        YarnLockOptions yarnLockOptions = new YarnLockOptions(false);
        YarnPackager yarnPackager = new YarnPackager(gson, yarnLockParser, yarnTransformer, yarnLockOptions);

        LernaOptions lernaOptions = new LernaOptions(true);
        LernaPackager lernaPackager = new LernaPackager(fileFinder, npmLockfilePackager, npmLockfileOptions, yarnPackager, lernaOptions);

        LernaPackage lernaPackage = new LernaPackage("testPackage", "testVersion", false, "packages/testPackage");

        List<LernaPackage> lernaPackages = new ArrayList<>();
        lernaPackages.add(lernaPackage);

        lernaPackager.generateLernaResult(sourceDirectory, lernaPackages);
    }
}