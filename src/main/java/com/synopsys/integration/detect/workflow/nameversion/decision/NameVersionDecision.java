/**
 * synopsys-detect
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
package com.synopsys.integration.detect.workflow.nameversion.decision;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import com.synopsys.integration.util.NameVersion;

public abstract class NameVersionDecision {
    @Nullable
    private final NameVersion nameVersion;

    protected NameVersionDecision() {
        this(null);
    }

    protected NameVersionDecision(@Nullable final NameVersion chosenNameVersion) {
        this.nameVersion = chosenNameVersion;
    }

    public Optional<NameVersion> getNameVersion() {
        return Optional.ofNullable(nameVersion);
    }

    public Optional<NameVersion> getChosenNameVersion() {
        return getNameVersion();
    }

    public abstract void printDescription(Logger logger);
}
