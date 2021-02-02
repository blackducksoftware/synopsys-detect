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
package com.synopsys.integration.detect.lifecycle.run.operation.input;

import java.io.File;
import java.util.Optional;

import javax.annotation.Nullable;

import com.synopsys.integration.blackduck.codelocation.CodeLocationBatchOutput;
import com.synopsys.integration.blackduck.codelocation.CodeLocationOutput;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationAccumulator;
import com.synopsys.integration.util.NameVersion;

public class SignatureScanInput extends CodeLocationInput {
    private File dockerTar;

    public <O extends CodeLocationOutput, T extends CodeLocationBatchOutput<O>> SignatureScanInput(NameVersion nameVersion, CodeLocationAccumulator<O, T> codeLocationAccumulator, @Nullable File dockerTar) {
        super(nameVersion, codeLocationAccumulator);
        this.dockerTar = dockerTar;
    }

    public Optional<File> getDockerTar() {
        return Optional.ofNullable(dockerTar);
    }
}
