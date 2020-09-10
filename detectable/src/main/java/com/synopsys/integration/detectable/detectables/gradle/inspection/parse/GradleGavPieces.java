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

import java.util.Collections;
import java.util.List;

public class GradleGavPieces {
    private final List<String> resolvedGavPieces;
    private final List<String> replacedGavPieces;

    public static GradleGavPieces createGav(List<String> resolvedGavPieces) {
        return new GradleGavPieces(resolvedGavPieces, Collections.emptyList());
    }

    public static GradleGavPieces createGavWithReplacement(List<String> resolvedGavPieces, List<String> replacedGavPieces) {
        return new GradleGavPieces(resolvedGavPieces, replacedGavPieces);
    }

    private GradleGavPieces(List<String> resolvedGavPieces, List<String> replacedGavPieces) {
        this.resolvedGavPieces = resolvedGavPieces;
        this.replacedGavPieces = replacedGavPieces;
    }

    public List<String> getGavPieces() {
        return resolvedGavPieces;
    }

    public List<String> getReplacedGavPieces() {
        return replacedGavPieces;
    }
}
