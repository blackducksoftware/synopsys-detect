package com.synopsys.integration.detectable.detectables.clang.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.ApkArchitectureResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.ApkPackageManagerResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.ClangPackageManagerResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.DpkgPackageManagerResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.DpkgVersionResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.RpmPackageManagerResolver;

public class ClangPackageManagerRunnerTest {

    @Test
    public void testRpmNonPkgOwnedIncludeFile() throws ExecutableRunnerException {

        ClangPackageManagerInfoFactory factory = ClangPackageManagerInfoFactory.standardFactory();
        ClangPackageManagerInfo packageManagerInfo = factory.rpm();
        ClangPackageManagerResolver packageResolver = new RpmPackageManagerResolver(new Gson());
        doTestNonPkgOwnedIncludeFile(packageManagerInfo, packageResolver, "%s is not owned by any package");
    }

    @Test
    public void testDpkgNonPkgOwnedIncludeFile() throws ExecutableRunnerException {
        ClangPackageManagerInfoFactory factory = ClangPackageManagerInfoFactory.standardFactory();
        ClangPackageManagerInfo packageManagerInfo = factory.dpkg();
        DpkgVersionResolver versionResolver = new DpkgVersionResolver();
        ClangPackageManagerResolver packageResolver = new DpkgPackageManagerResolver(versionResolver);

        doTestNonPkgOwnedIncludeFile(packageManagerInfo, packageResolver, "dpkg-query: no path found matching pattern %s");
    }

    @Test
    public void testApkNonPkgOwnedIncludeFile() throws ExecutableRunnerException {
        ClangPackageManagerInfoFactory factory = ClangPackageManagerInfoFactory.standardFactory();
        ClangPackageManagerInfo packageManagerInfo = factory.apk();
        ApkArchitectureResolver archResolver = new ApkArchitectureResolver();
        ClangPackageManagerResolver packageResolver = new ApkPackageManagerResolver(archResolver);

        doTestNonPkgOwnedIncludeFile(packageManagerInfo, packageResolver, "ERROR: %s: Could not find owner package");
    }

    private void doTestNonPkgOwnedIncludeFile(ClangPackageManagerInfo packageManagerInfo, ClangPackageManagerResolver packageResolver,
        String pkgMgrQueryResultPattern) throws ExecutableRunnerException {
        ClangPackageManager currentPackageManager = new ClangPackageManager(packageManagerInfo, packageResolver);

        File workingDirectory = new File("test");
        ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        List<String> fileSpecificGetOwnerArgs = new ArrayList<>(packageManagerInfo.getPkgMgrGetOwnerCmdArgs());
        File nonPkgOwnedIncludeFile = new File("/home/steve/detect.sh");
        fileSpecificGetOwnerArgs.add(nonPkgOwnedIncludeFile.getAbsolutePath());
        String pkgMgrGetOwnerQueryFileOutput = String.format(pkgMgrQueryResultPattern, nonPkgOwnedIncludeFile.getAbsolutePath());
        ExecutableOutput pkgMgrGetOwnerQueryFileResult = new ExecutableOutput("", 0, pkgMgrGetOwnerQueryFileOutput, "");
        Mockito.when(executableRunner.execute(workingDirectory, packageManagerInfo.getPkgMgrCmdString(), fileSpecificGetOwnerArgs)).thenReturn(pkgMgrGetOwnerQueryFileResult);
        ClangPackageManagerRunner runner = new ClangPackageManagerRunner();

        // Test
        PackageDetailsResult result = runner.getPackages(currentPackageManager, workingDirectory, executableRunner, nonPkgOwnedIncludeFile);

        // Verify
        assertEquals(1, result.getUnRecognizedDependencyFiles().size());
        assertEquals(nonPkgOwnedIncludeFile, result.getUnRecognizedDependencyFiles().iterator().next());
    }
}
