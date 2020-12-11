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
package com.synopsys.integration.detectable.detectables.conan.graph.n.gen.deleteme;

import java.util.List;

public class NamedNode extends ParentNode {
    private final List<String> requiresRefs;
    private final List<String> buildRequiresRefs;

    public NamedNode(String ref, String filename, String name, String version, String user, String channel, String recipeRevision, String packageId, String packageRevision,
        boolean rootNode, List<String> requiresRefs, List<String> buildRequiresRefs) {
        super(ref, filename, name, version, user, channel, recipeRevision, packageId, packageRevision,
            rootNode);
        this.requiresRefs = requiresRefs;
        this.buildRequiresRefs = buildRequiresRefs;
    }

    public List<String> getRequiresRefs() {
        return requiresRefs;
    }

    public List<String> getBuildRequiresRefs() {
        return buildRequiresRefs;
    }
}
