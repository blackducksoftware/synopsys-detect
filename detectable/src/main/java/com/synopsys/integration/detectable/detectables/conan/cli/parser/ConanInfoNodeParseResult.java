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
package com.synopsys.integration.detectable.detectables.conan.cli.parser;

import java.util.Optional;

import com.synopsys.integration.detectable.detectables.conan.graph.ConanNode;

public class ConanInfoNodeParseResult {
    private final int lastParsedLineIndex;
    private final ConanNode conanNode;

    public ConanInfoNodeParseResult(int lastParsedLineIndex) {
        this.lastParsedLineIndex = lastParsedLineIndex;
        this.conanNode = null;
    }

    public ConanInfoNodeParseResult(int lastParsedLineIndex, ConanNode conanGraphNode) {
        this.lastParsedLineIndex = lastParsedLineIndex;
        this.conanNode = conanGraphNode;
    }

    public int getLastParsedLineIndex() {
        return lastParsedLineIndex;
    }

    public Optional<ConanNode> getConanNode() {
        return Optional.ofNullable(conanNode);
    }
}
