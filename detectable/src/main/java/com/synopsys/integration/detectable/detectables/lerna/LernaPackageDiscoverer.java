package com.synopsys.integration.detectable.detectables.lerna;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.lerna.model.LernaPackage;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;
import com.synopsys.integration.util.ExcludedIncludedWildcardFilter;

// TODO: Split into separate parser and command runner
public class LernaPackageDiscoverer {
    private final DetectableExecutableRunner executableRunner;
    private final Gson gson;
    private final ExcludedIncludedWildcardFilter excludedIncludedFilter;

    public LernaPackageDiscoverer(DetectableExecutableRunner executableRunner, Gson gson, List<String> excludedPackages, List<String> includedPackages) {
        this.executableRunner = executableRunner;
        this.gson = gson;
        this.excludedIncludedFilter = ExcludedIncludedWildcardFilter.fromCollections(excludedPackages, includedPackages);
    }

    public List<LernaPackage> discoverLernaPackages(File workingDirectory, ExecutableTarget lernaExecutable) throws ExecutableRunnerException {
        ExecutableOutput lernaLsExecutableOutput = executableRunner.execute(ExecutableUtils.createFromTarget(workingDirectory, lernaExecutable, "ls", "--all", "--json"));
        String lernaLsOutput = lernaLsExecutableOutput.getStandardOutput();

        Type lernaPackageListType = new TypeToken<ArrayList<LernaPackage>>() {
        }.getType();
        List<LernaPackage> lernaPackages = gson.fromJson(lernaLsOutput, lernaPackageListType);

        return lernaPackages.stream()
            .filter(Objects::nonNull)
            .filter(lernaPackage -> excludedIncludedFilter.shouldInclude(lernaPackage.getName()))
            .collect(Collectors.toList());
    }
}
