package com.synopsys.integration.detectable.detectables.npm.lockfile.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmDependencyConverter;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmProject;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.PackageLock;
import com.synopsys.integration.detectable.detectables.npm.lockfile.result.NpmPackagerResult;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.util.NameVersion;

public class NpmLockfilePackager {
    private final Gson gson;
    private final ExternalIdFactory externalIdFactory;
    private final NpmLockFileProjectIdTransformer projectIdTransformer;
    private final NpmLockfileGraphTransformer graphTransformer;

    public NpmLockfilePackager(Gson gson, ExternalIdFactory externalIdFactory, NpmLockFileProjectIdTransformer projectIdTransformer, NpmLockfileGraphTransformer graphTransformer) {
        this.gson = gson;
        this.externalIdFactory = externalIdFactory;
        this.projectIdTransformer = projectIdTransformer;
        this.graphTransformer = graphTransformer;
    }

    public NpmPackagerResult parseAndTransform(@Nullable String packageJsonText, String lockFileText) {
        return parseAndTransform(packageJsonText, lockFileText, new ArrayList<>());
    }

    public NpmPackagerResult parseAndTransform(@Nullable String packageJsonText, String lockFileText, List<NameVersion> externalDependencies) {
        PackageJson packageJson = Optional.ofNullable(packageJsonText)
            .map(content -> gson.fromJson(content, PackageJson.class))
            .orElse(null);

        PackageLock packageLock = gson.fromJson(lockFileText, PackageLock.class);

        NpmDependencyConverter dependencyConverter = new NpmDependencyConverter(externalIdFactory);
        NpmProject project = dependencyConverter.convertLockFile(packageLock, packageJson);

        DependencyGraph dependencyGraph = graphTransformer.transform(packageLock, project, externalDependencies);
        ExternalId projectId = projectIdTransformer.transform(packageJson, packageLock);
        CodeLocation codeLocation = new CodeLocation(dependencyGraph, projectId);
        return new NpmPackagerResult(projectId.getName(), projectId.getVersion(), codeLocation);
    }

}
