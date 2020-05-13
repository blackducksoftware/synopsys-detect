package com.synopsys.integration.detectable.detectables.clang.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManager;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfo;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfoFactory;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerRunner;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.PackageDetailsResult;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.ClangPackageManagerResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.RpmPackageManagerResolver;

public class ClangPackageManagerRunnerTest {

    @Test
    public void testRpm() throws ExecutableRunnerException {

        final ClangPackageManagerInfoFactory factory = ClangPackageManagerInfoFactory.standardFactory();
        final ClangPackageManagerInfo packageManagerInfo = factory.rpm();
        final ClangPackageManagerResolver packageResolver = new RpmPackageManagerResolver(new Gson());
        final ClangPackageManager currentPackageManager = new ClangPackageManager(packageManagerInfo, packageResolver);

        final File workingDirectory = new File("test");
        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        final List<String> fileSpecificGetOwnerArgs = new ArrayList<>(packageManagerInfo.getPkgMgrGetOwnerCmdArgs());
        final File nonPkgOwnedIncludeFile = new File("/home/steve/detect.sh");
        fileSpecificGetOwnerArgs.add(nonPkgOwnedIncludeFile.getAbsolutePath());
        final String pkgMgrOutput = String.format("file %s is not owned by any package", nonPkgOwnedIncludeFile.getAbsolutePath());
        final ExecutableOutput pkgMgrResult = new ExecutableOutput("", 0, pkgMgrOutput, "");
        Mockito.when(executableRunner.execute(workingDirectory, packageManagerInfo.getPkgMgrCmdString(), fileSpecificGetOwnerArgs)).thenReturn(pkgMgrResult);
        final ClangPackageManagerRunner runner = new ClangPackageManagerRunner();

        // Test
        final PackageDetailsResult result = runner.getPackages(currentPackageManager, workingDirectory, executableRunner, nonPkgOwnedIncludeFile);

        // Verify
        assertEquals(1, result.getFailedDependencyFiles().size());
        assertEquals(nonPkgOwnedIncludeFile, result.getFailedDependencyFiles().iterator().next());
    }
    
    @Test
    public void testDpkg() throws ExecutableRunnerException {
        fail("Not implemented yet");
    }

    @Test
    public void testApk() throws ExecutableRunnerException {
        fail("Not implemented yet");
    }
}
