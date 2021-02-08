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
package com.synopsys.integration.detectable.detectable.explanation;

import java.io.File;

import com.synopsys.integration.detectable.ExecutableTarget;

public class FoundExecutable extends Explanation {
    private final String executableString;

    public FoundExecutable(File file) {
        this.executableString = file.toString();
    }

    public FoundExecutable(ExecutableTarget executableTarget) {
        this.executableString = executableTarget.toCommand();
    }

    public FoundExecutable(String name) {
        this.executableString = name;
    }

    @Override
    public String describeSelf() {
        return "Found executable: " + executableString;
    }
}
