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
package com.synopsys.integration.detectable.detectable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.explanation.Explanation;
import com.synopsys.integration.detectable.detectable.explanation.FoundExecutable;
import com.synopsys.integration.detectable.detectable.explanation.FoundFile;
import com.synopsys.integration.detectable.detectable.explanation.FoundInspector;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

public class PassedResultBuilder {
    private final List<Explanation> explanations = new ArrayList<>();
    private final List<File> relevantFiles = new ArrayList<>();

    public void foundFile(@NotNull File file) {
        explanations.add(new FoundFile(file));
        relevantFiles.add(file);
    }

    public void foundNullableFile(@Nullable File file) {
        if (file == null)
            return;
        foundFile(file);
    }

    public PassedDetectableResult build() {
        return new PassedDetectableResult(explanations, relevantFiles);
    }

    public void foundExecutable(final File exe) {
        explanations.add(new FoundExecutable(exe));
    }

    public void foundExecutable(final ExecutableTarget exe) {
        explanations.add(new FoundExecutable(exe));
    }

    public void foundInspector(final File inspector) {
        explanations.add(new FoundInspector(inspector));
    }
}
