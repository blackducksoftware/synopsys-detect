package com.synopsys.integration.detectable.detectables.clang.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
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
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.DpkgPkgDetailsResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.RpmPackageManagerResolver;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class ClangPackageManagerRunnerTest {
    private final File dependencyFile = new File("/usr/include/X11/Core.h");

    @Test
    public void testDpkgPkg() throws ExecutableRunnerException {
        ClangPackageManagerInfoFactory factory = ClangPackageManagerInfoFactory.standardFactory();
        ClangPackageManagerInfo packageManagerInfo = factory.dpkg();
        DpkgPkgDetailsResolver versionResolver = new DpkgPkgDetailsResolver();
        ClangPackageManagerResolver packageResolver = new DpkgPackageManagerResolver(versionResolver);
        String pkgOwnerPattern = "libxt-dev:amd64: %s\n";
        String pkgDetailsPattern = "Package: %s\n"
            + "Architecture: amd64\n"
            + "Version: 1:1.1.5-1\n"
            + "Status: install ok installed\n";
        testSuccessCase(packageManagerInfo, packageResolver, "libxt-dev", "amd64", false, "1:1.1.5-1", pkgOwnerPattern, pkgDetailsPattern);
    }

    @Test
    public void testDpkgPkgNotInstalled() throws ExecutableRunnerException {
        ClangPackageManagerInfoFactory factory = ClangPackageManagerInfoFactory.standardFactory();
        ClangPackageManagerInfo packageManagerInfo = factory.dpkg();
        DpkgPkgDetailsResolver versionResolver = new DpkgPkgDetailsResolver();
        ClangPackageManagerResolver packageResolver = new DpkgPackageManagerResolver(versionResolver);
        String pkgOwnerPattern = "libxt-dev:amd64: %s\n";
        String pkgDetailsPattern = "Package: %s\n"
            + "Architecture: amd64\n"
            + "Version: 1:1.1.5-1\n"
            + "Status: pending\n";
        testNoResultsCase(packageManagerInfo, packageResolver, "libxt-dev", "amd64", pkgOwnerPattern, pkgDetailsPattern);
    }

    @Test
    public void testDpkgPkgBuriedArchitecture() throws ExecutableRunnerException {
        ClangPackageManagerInfoFactory factory = ClangPackageManagerInfoFactory.standardFactory();
        ClangPackageManagerInfo packageManagerInfo = factory.dpkg();
        DpkgPkgDetailsResolver versionResolver = new DpkgPkgDetailsResolver();
        ClangPackageManagerResolver packageResolver = new DpkgPackageManagerResolver(versionResolver);
        String pkgOwnerPattern = "libxt-dev: %s\n";
        String pkgDetailsPattern = "Package: %s\n"
            + "Architecture: amd64\n"
            + "Version: 1:1.1.5-1\n"
            + "Status: install ok installed\n";
        testSuccessCase(packageManagerInfo, packageResolver, "libxt-dev", "amd64", true, "1:1.1.5-1", pkgOwnerPattern, pkgDetailsPattern);
    }

    @Test
    public void testRpmNonPkgOwnedIncludeFile() throws ExecutableRunnerException {
        ClangPackageManagerInfoFactory factory = ClangPackageManagerInfoFactory.standardFactory();
        ClangPackageManagerInfo packageManagerInfo = factory.rpm();
        ClangPackageManagerResolver packageResolver = new RpmPackageManagerResolver(new Gson());
        String pkgOwnerPattern = "%s is not owned by any package";
        testNonPkgOwnedIncludeFile(packageManagerInfo, packageResolver, pkgOwnerPattern);
    }

    @Test
    public void testDpkgNonPkgOwnedIncludeFile() throws ExecutableRunnerException {
        ClangPackageManagerInfoFactory factory = ClangPackageManagerInfoFactory.standardFactory();
        ClangPackageManagerInfo packageManagerInfo = factory.dpkg();
        DpkgPkgDetailsResolver versionResolver = new DpkgPkgDetailsResolver();
        ClangPackageManagerResolver packageResolver = new DpkgPackageManagerResolver(versionResolver);
        String pkgOwnerPattern = "dpkg-query: no path found matching pattern %s";
        testNonPkgOwnedIncludeFile(packageManagerInfo, packageResolver, pkgOwnerPattern);
    }

    @Test
    public void testApkNonPkgOwnedIncludeFile() throws ExecutableRunnerException {
        ClangPackageManagerInfoFactory factory = ClangPackageManagerInfoFactory.standardFactory();
        ClangPackageManagerInfo packageManagerInfo = factory.apk();
        ApkArchitectureResolver archResolver = new ApkArchitectureResolver();
        ClangPackageManagerResolver packageResolver = new ApkPackageManagerResolver(archResolver);
        String pkgOwnerPattern = "ERROR: %s: Could not find owner package";
        testNonPkgOwnedIncludeFile(packageManagerInfo, packageResolver, pkgOwnerPattern);
    }

    private void testNonPkgOwnedIncludeFile(
        ClangPackageManagerInfo packageManagerInfo, ClangPackageManagerResolver packageResolver,
        String pkgMgrOwnerQueryResultPattern
    ) throws ExecutableRunnerException {

        // Test
        PackageDetailsResult result = runTest(packageManagerInfo, packageResolver, null, null, false,
            pkgMgrOwnerQueryResultPattern, null, dependencyFile
        );

        // Verify
        assertEquals(1, result.getUnRecognizedDependencyFiles().size());
        assertEquals(dependencyFile, result.getUnRecognizedDependencyFiles().iterator().next());
    }

    private void testSuccessCase(
        ClangPackageManagerInfo packageManagerInfo, ClangPackageManagerResolver packageResolver,
        String pkgName, String pkgArchitecture, boolean archBuried, String pkgVersion,
        String pkgMgrQueryResultPattern, String pkgMgrDetailsQueryResultPattern
    ) throws ExecutableRunnerException {

        // Test
        PackageDetailsResult result = runTest(packageManagerInfo, packageResolver, pkgName, pkgArchitecture,
            archBuried,
            pkgMgrQueryResultPattern, pkgMgrDetailsQueryResultPattern, dependencyFile
        );

        // Verify
        assertEquals(0, result.getUnRecognizedDependencyFiles().size());
        assertEquals(1, result.getFoundPackages().size());
        PackageDetails foundPkgDetails = result.getFoundPackages().iterator().next();
        assertEquals(pkgName, foundPkgDetails.getPackageName());
        assertEquals(pkgArchitecture, foundPkgDetails.getPackageArch());
        assertEquals(pkgVersion, foundPkgDetails.getPackageVersion());
    }

    private void testNoResultsCase(
        ClangPackageManagerInfo packageManagerInfo, ClangPackageManagerResolver packageResolver,
        String pkgName, String pkgArchitecture,
        String pkgMgrQueryResultPattern, String pkgMgrDetailsQueryResultPattern
    ) throws ExecutableRunnerException {

        // Test
        PackageDetailsResult result = runTest(packageManagerInfo, packageResolver, pkgName, pkgArchitecture, false,
            pkgMgrQueryResultPattern, pkgMgrDetailsQueryResultPattern, dependencyFile
        );

        // Verify
        assertEquals(0, result.getUnRecognizedDependencyFiles().size());
        assertEquals(0, result.getFoundPackages().size());
    }

    private PackageDetailsResult runTest(
        ClangPackageManagerInfo packageManagerInfo, ClangPackageManagerResolver packageResolver,
        String pkgName,
        String pkgArch, boolean archBuried,
        String pkgMgrOwnerQueryResultPattern, String pkgMgrDetailsQueryResultPattern,
        File dependencyFile
    )
        throws ExecutableRunnerException {
        ClangPackageManager currentPackageManager = new ClangPackageManager(packageManagerInfo, packageResolver);

        File workingDirectory = new File("test");
        DetectableExecutableRunner executableRunner = Mockito.mock(DetectableExecutableRunner.class);

        List<String> fileSpecificGetOwnerArgs = new ArrayList<>(packageManagerInfo.getPkgMgrGetOwnerCmdArgs());
        fileSpecificGetOwnerArgs.add(dependencyFile.getAbsolutePath());

        if (packageManagerInfo.getPkgInfoArgs().isPresent() && (pkgMgrDetailsQueryResultPattern != null)) {
            List<String> fileSpecificGetDetailsArgs = new ArrayList<>(packageManagerInfo.getPkgInfoArgs().get());
            // Architecture might be available from the initial "who owns this" query, buried in the later-gotten details, or altogether absent
            // If absent or buried, the detail query should omit arch; otherwise: the detail query should include arch
            if (archBuried || pkgArch == null) {
                fileSpecificGetDetailsArgs.add(pkgName);
            } else {
                fileSpecificGetDetailsArgs.add(pkgName + ":" + pkgArch);
            }
            String pkgMgrGetDetailsQueryFileOutput = String.format(pkgMgrDetailsQueryResultPattern, dependencyFile);
            ExecutableOutput pkgMgrGetDetailsQueryFileResult = new ExecutableOutput(0, pkgMgrGetDetailsQueryFileOutput, "");
            Mockito.when(executableRunner.execute(workingDirectory, packageManagerInfo.getPkgMgrCmdString(), fileSpecificGetDetailsArgs))
                .thenReturn(pkgMgrGetDetailsQueryFileResult);
        }

        String pkgMgrGetOwnerQueryFileOutput = String.format(pkgMgrOwnerQueryResultPattern, dependencyFile.getAbsolutePath());
        ExecutableOutput pkgMgrGetOwnerQueryFileResult = new ExecutableOutput(0, pkgMgrGetOwnerQueryFileOutput, "");
        Mockito.when(executableRunner.execute(workingDirectory, packageManagerInfo.getPkgMgrCmdString(), fileSpecificGetOwnerArgs)).thenReturn(pkgMgrGetOwnerQueryFileResult);

        ClangPackageManagerRunner runner = new ClangPackageManagerRunner();

        // Test
        return runner.getPackages(currentPackageManager, workingDirectory, executableRunner, dependencyFile);
    }
}
