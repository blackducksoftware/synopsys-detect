/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.gradle.inspection.parse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.bdio.model.dependencyid.DependencyId;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleGav;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleGavId;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.ReplacedGradleGav;

public class DependencyReplacementResolver {
    @Nullable
    private final DependencyReplacementResolver parentResolver;
    private final Map<DependencyId, GradleGav> replacementMap;

    public static DependencyReplacementResolver createFromParentResolver(DependencyReplacementResolver dependencyReplacementResolver) {
        return new DependencyReplacementResolver(dependencyReplacementResolver);
    }

    public static DependencyReplacementResolver createRootResolver() {
        return new DependencyReplacementResolver(null);
    }

    private DependencyReplacementResolver(@Nullable DependencyReplacementResolver parentResolver) {
        this.parentResolver = parentResolver;
        this.replacementMap = new HashMap<>();
    }

    public void addReplacementData(ReplacedGradleGav replaced, GradleGav replacement) {
        replacementMap.put(replaced.toDependencyId(), replacement);
    }

    public Optional<GradleGav> getReplacement(GradleGavId dependency) {
        GradleGav replacement = null;

        if (parentResolver != null) {
            replacement = parentResolver.getReplacement(dependency).orElse(null);
        }

        if (replacement == null) {
            replacement = replacementMap.get(dependency.toDependencyId());
            if (parentResolver != null && replacement != null) {
                replacement = parentResolver.getReplacement(replacement).orElse(replacement);
            }
        }

        return Optional.ofNullable(replacement);

    }
}
