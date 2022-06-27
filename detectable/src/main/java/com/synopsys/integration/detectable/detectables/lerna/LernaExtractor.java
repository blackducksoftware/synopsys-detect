package com.synopsys.integration.detectable.detectables.lerna;

import java.io.File;
import java.util.List;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectables.lerna.model.LernaPackage;
import com.synopsys.integration.detectable.detectables.lerna.model.LernaResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class LernaExtractor {
    private final LernaPackageDiscoverer lernaPackageDiscoverer;
    private final LernaPackager lernaPackager;

    public LernaExtractor(LernaPackageDiscoverer lernaPackageDiscoverer, LernaPackager lernaPackager) {
        this.lernaPackageDiscoverer = lernaPackageDiscoverer;
        this.lernaPackager = lernaPackager;
    }

    public Extraction extract(File sourceDirectory, File packageJson, ExecutableTarget lernaExecutable) throws ExecutableRunnerException {
        List<LernaPackage> lernaPackages = lernaPackageDiscoverer.discoverLernaPackages(sourceDirectory, lernaExecutable);
        LernaResult lernaResult = lernaPackager.generateLernaResult(sourceDirectory, packageJson, lernaPackages);

        if (lernaResult.getException().isPresent()) {
            return new Extraction.Builder()
                .exception(lernaResult.getException().get())
                .build();
        }

        return new Extraction.Builder()
            .projectName(lernaResult.getProjectName())
            .projectVersion(lernaResult.getProjectVersionName())
            .success(lernaResult.getCodeLocations())
            .build();
    }
}
