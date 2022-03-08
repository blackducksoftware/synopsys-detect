package com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver;

import java.io.File;
import java.util.List;

import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfo;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.PackageDetails;
import com.synopsys.integration.executable.ExecutableRunnerException;

public interface ClangPackageManagerResolver {
    List<PackageDetails> resolvePackages(
        ClangPackageManagerInfo currentPackageManager,
        DetectableExecutableRunner executableRunner,
        File workingDirectory,
        String queryPackageOutput
    )
        throws ExecutableRunnerException, NotOwnedByAnyPkgException;
}
