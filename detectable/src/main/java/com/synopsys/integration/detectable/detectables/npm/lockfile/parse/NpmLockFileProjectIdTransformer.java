package com.synopsys.integration.detectable.detectables.npm.lockfile.parse;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.PackageLock;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;

public class NpmLockFileProjectIdTransformer {
    private final Logger logger = LoggerFactory.getLogger(NpmLockFileProjectIdTransformer.class);
    private final Gson gson;
    private final ExternalIdFactory externalIdFactory;

    public NpmLockFileProjectIdTransformer(Gson gson, ExternalIdFactory externalIdFactory) {
        this.gson = gson;
        this.externalIdFactory = externalIdFactory;
    }

    public ExternalId transform(@Nullable PackageJson packageJson, PackageLock packageLock) {
        return Optional.ofNullable(packageJson)
            .map(it -> externalIdFactory.createNameVersionExternalId(Forge.NPMJS, it.name, it.version))
            .orElse(externalIdFactory.createNameVersionExternalId(Forge.NPMJS, packageLock.name, packageLock.version));
    }

}
