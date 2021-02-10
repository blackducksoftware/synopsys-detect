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
package com.synopsys.integration.detectable.detectables.pip.poetry.parser;

import java.util.Optional;

import org.tomlj.TomlTable;

public class ToolPoetrySectionResult {
    private final boolean found;
    private final TomlTable toolPoetrySection;

    public static ToolPoetrySectionResult FOUND(TomlTable poetrySection) {
        return new ToolPoetrySectionResult(true, poetrySection);
    }

    public static ToolPoetrySectionResult NOT_FOUND() {
        return new ToolPoetrySectionResult(false, null);
    }

    private ToolPoetrySectionResult(final boolean found, final TomlTable toolPoetrySection) {
        this.found = found;
        this.toolPoetrySection = toolPoetrySection;
    }

    public boolean wasFound() {
        return found;
    }

    public Optional<TomlTable> getToolPoetrySection() {
        return Optional.ofNullable(toolPoetrySection);
    }
}
