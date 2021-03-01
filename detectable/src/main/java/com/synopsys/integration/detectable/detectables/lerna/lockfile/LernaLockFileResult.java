/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.lerna.lockfile;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LernaLockFileResult {
    private final String npmLockContents;
    private final List<String> yarnLockContents;

    private LernaLockFileResult(@Nullable String npmLockContents, @Nullable List<String> yarnLockContents) {
        this.npmLockContents = npmLockContents;
        this.yarnLockContents = yarnLockContents;
    }

    public static LernaLockFileResult foundNpm(@NotNull String npmLockFile) {
        return new LernaLockFileResult(npmLockFile, null);
    }

    public static LernaLockFileResult foundYarn(@NotNull List<String> yarnLockFile) {
        return new LernaLockFileResult(null, yarnLockFile);
    }

    public static LernaLockFileResult foundNone() {
        return new LernaLockFileResult(null, null);
    }

    public Optional<String> getNpmLockContents() {
        return Optional.ofNullable(npmLockContents);
    }

    public Optional<List<String>> getYarnLockContents() {
        return Optional.ofNullable(yarnLockContents);
    }

    public boolean hasLockFile() {
        return getNpmLockContents().isPresent() || getYarnLockContents().isPresent();
    }
}
