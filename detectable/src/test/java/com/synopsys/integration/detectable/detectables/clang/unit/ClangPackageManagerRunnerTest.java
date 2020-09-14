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
import com.synopsys.integration.detectable.detectables.clang.packagemanager.PackageDetails;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.PackageDetailsResult;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.ApkArchitectureResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.ApkPackageManagerResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.ClangPackageManagerResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.DpkgPackageManagerResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.DpkgVersionResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.RpmPackageManagerResolver;

public class ClangPackageManagerRunnerTest {
    private final File dependencyFile = new File("/usr/include/X11/Core.h");

    @Test
    public void testDpkgPkg() throws ExecutableRunnerException {
        ClangPackageManagerInfoFactory factory = ClangPackageManagerInfoFactory.standardFactory();
        ClangPackageManagerInfo packageManagerInfo = factory.dpkg();
        DpkgVersionResolver versionResolver = new DpkgVersionResolver();
        ClangPackageManagerResolver packageResolver = new DpkgPackageManagerResolver(versionResolver);
        String pkgDetailsPattern = "Package: %s\n"
                                       + "Architecture: amd64\n"
                                       + "Version: 1:1.1.5-1\n";
        testSuccessCase(packageManagerInfo, packageResolver, "libxt-dev", "amd64", "1:1.1.5-1", "libxt-dev:amd64: %s ", pkgDetailsPattern);
    }

    @Test
    public void testRpmNonPkgOwnedIncludeFile() throws ExecutableRunnerException {

        ClangPackageManagerInfoFactory factory = ClangPackageManagerInfoFactory.standardFactory();
        ClangPackageManagerInfo packageManagerInfo = factory.rpm();
        ClangPackageManagerResolver packageResolver = new RpmPackageManagerResolver(new Gson());
        testNonPkgOwnedIncludeFile(packageManagerInfo, packageResolver, "%s is not owned by any package", null);
    }

    @Test
    public void testDpkgNonPkgOwnedIncludeFile() throws ExecutableRunnerException {
        ClangPackageManagerInfoFactory factory = ClangPackageManagerInfoFactory.standardFactory();
        ClangPackageManagerInfo packageManagerInfo = factory.dpkg();
        DpkgVersionResolver versionResolver = new DpkgVersionResolver();
        ClangPackageManagerResolver packageResolver = new DpkgPackageManagerResolver(versionResolver);

        testNonPkgOwnedIncludeFile(packageManagerInfo, packageResolver, "dpkg-query: no path found matching pattern %s", null);
    }

    @Test
    public void testApkNonPkgOwnedIncludeFile() throws ExecutableRunnerException {
        ClangPackageManagerInfoFactory factory = ClangPackageManagerInfoFactory.standardFactory();
        ClangPackageManagerInfo packageManagerInfo = factory.apk();
        ApkArchitectureResolver archResolver = new ApkArchitectureResolver();
        ClangPackageManagerResolver packageResolver = new ApkPackageManagerResolver(archResolver);

        testNonPkgOwnedIncludeFile(packageManagerInfo, packageResolver, "ERROR: %s: Could not find owner package", null);
    }

    private void testNonPkgOwnedIncludeFile(ClangPackageManagerInfo packageManagerInfo, ClangPackageManagerResolver packageResolver,
        String pkgMgrOwnerQueryResultPattern, String pkgMgrDetailsQueryResultPattern) throws ExecutableRunnerException {

        // Test
        PackageDetailsResult result = runTest(packageManagerInfo, packageResolver, null,
            pkgMgrOwnerQueryResultPattern, pkgMgrDetailsQueryResultPattern, dependencyFile);

        // Verify
        assertEquals(1, result.getUnRecognizedDependencyFiles().size());
        assertEquals(dependencyFile, result.getUnRecognizedDependencyFiles().iterator().next());
    }

    private void testSuccessCase(ClangPackageManagerInfo packageManagerInfo, ClangPackageManagerResolver packageResolver,
        String pkgName, String pkgArchitecture, String pkgVersion,
        String pkgMgrQueryResultPattern, String pkgMgrDetailsQueryResultPattern) throws ExecutableRunnerException {

        // Test
        PackageDetailsResult result = runTest(packageManagerInfo, packageResolver, pkgName,
            pkgMgrQueryResultPattern, pkgMgrDetailsQueryResultPattern, dependencyFile);

        // Verify
        assertEquals(0, result.getUnRecognizedDependencyFiles().size());
        assertEquals(1, result.getFoundPackages().size());
        PackageDetails foundPkgDetails = result.getFoundPackages().iterator().next();
        assertEquals(pkgName, foundPkgDetails.getPackageName());
        assertEquals(pkgArchitecture, foundPkgDetails.getPackageArch());
        assertEquals(pkgVersion, foundPkgDetails.getPackageVersion());
    }

    private PackageDetailsResult runTest(ClangPackageManagerInfo packageManagerInfo, ClangPackageManagerResolver packageResolver,
        String pkgName,
        String pkgMgrOwnerQueryResultPattern, String pkgMgrDetailsQueryResultPattern,
        File dependencyFile)
        throws ExecutableRunnerException {
        ClangPackageManager currentPackageManager = new ClangPackageManager(packageManagerInfo, packageResolver);

        File workingDirectory = new File("test");
        ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);

        List<String> fileSpecificGetOwnerArgs = new ArrayList<>(packageManagerInfo.getPkgMgrGetOwnerCmdArgs());
        fileSpecificGetOwnerArgs.add(dependencyFile.getAbsolutePath());

        if (packageManagerInfo.getPkgInfoArgs().isPresent() && (pkgMgrDetailsQueryResultPattern != null)) {
            List<String> fileSpecificGetDetailsArgs = new ArrayList<>(packageManagerInfo.getPkgInfoArgs().get());
            fileSpecificGetDetailsArgs.add(pkgName);
            String pkgMgrGetDetailsQueryFileOutput = String.format(pkgMgrDetailsQueryResultPattern, dependencyFile);
            ExecutableOutput pkgMgrGetDetailsQueryFileResult = new ExecutableOutput("", 0, pkgMgrGetDetailsQueryFileOutput, "");
            Mockito.when(executableRunner.execute(workingDirectory, packageManagerInfo.getPkgMgrCmdString(), fileSpecificGetDetailsArgs)).thenReturn(pkgMgrGetDetailsQueryFileResult);
        }

        String pkgMgrGetOwnerQueryFileOutput = String.format(pkgMgrOwnerQueryResultPattern, dependencyFile.getAbsolutePath());
        ExecutableOutput pkgMgrGetOwnerQueryFileResult = new ExecutableOutput("", 0, pkgMgrGetOwnerQueryFileOutput, "");
        Mockito.when(executableRunner.execute(workingDirectory, packageManagerInfo.getPkgMgrCmdString(), fileSpecificGetOwnerArgs)).thenReturn(pkgMgrGetOwnerQueryFileResult);

        ClangPackageManagerRunner runner = new ClangPackageManagerRunner();

        // Test
        PackageDetailsResult result = runner.getPackages(currentPackageManager, workingDirectory, executableRunner, dependencyFile);
        return result;
    }
}
