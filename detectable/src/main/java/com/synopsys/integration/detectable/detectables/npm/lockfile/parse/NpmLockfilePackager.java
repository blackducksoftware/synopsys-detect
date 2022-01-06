package com.synopsys.integration.detectable.detectables.npm.lockfile.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
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
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
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

    public NpmPackagerResult parseAndTransform(@Nullable String packageJsonText, String lockFileText, boolean includeDevDependencies, boolean includePeerDependencies) {
        return parseAndTransform(packageJsonText, lockFileText, includeDevDependencies, includePeerDependencies, new ArrayList<>());
    }

    public NpmPackagerResult parseAndTransform(@Nullable String packageJsonText, String lockFileText, boolean includeDevDependencies, boolean includePeerDependencies, List<NameVersion> externalDependencies) {
        PackageJson packageJson = Optional.ofNullable(packageJsonText)
            .map(content -> gson.fromJson(content, PackageJson.class))
            .orElse(null);

        PackageLock packageLock = gson.fromJson(lockFileText, PackageLock.class);

        NpmDependencyConverter dependencyConverter = new NpmDependencyConverter(externalIdFactory);
        NpmProject project = dependencyConverter.convertLockFile(packageLock, packageJson);

        MutableDependencyGraph dependencyGraph = graphTransformer.transform(packageLock, project, includeDevDependencies, includePeerDependencies, externalDependencies);
        ExternalId projectId = projectIdTransformer.transform(packageJson, packageLock);
        CodeLocation codeLocation = new CodeLocation(dependencyGraph, projectId);
        return new NpmPackagerResult(projectId.getName(), projectId.getVersion(), codeLocation);
    }

}
