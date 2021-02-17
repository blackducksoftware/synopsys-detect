/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
