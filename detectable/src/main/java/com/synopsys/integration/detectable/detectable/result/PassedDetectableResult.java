/**
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
package com.synopsys.integration.detectable.detectable.result;

import java.io.File;
import java.util.Collections;
import java.util.List;

import com.synopsys.integration.detectable.detectable.explanation.Explanation;

public class PassedDetectableResult implements DetectableResult {
    private final List<Explanation> explanations;
    private final List<File> relevantFiles;

    public PassedDetectableResult(final List<Explanation> explanations, List<File> relevantFiles) {
        this.explanations = explanations;
        this.relevantFiles = relevantFiles;
    }

    public PassedDetectableResult(final List<Explanation> explanations) {
        this.explanations = explanations;
        this.relevantFiles = Collections.emptyList();
    }

    public PassedDetectableResult() {
        this.explanations = Collections.emptyList();
        this.relevantFiles = Collections.emptyList();
    }

    public PassedDetectableResult(Explanation explanation) {
        this.explanations = Collections.singletonList(explanation);
        this.relevantFiles = Collections.emptyList();
    }

    @Override
    public boolean getPassed() {
        return true;
    }

    @Override
    public String toDescription() {
        return "Passed.";
    }

    @Override
    public List<Explanation> getExplanation() {
        return explanations;
    }

    @Override
    public List<File> getRelevantFiles() {
        return relevantFiles;
    }
}
