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
package com.synopsys.integration.detectable.detectables.npm.lockfile.functional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.npm.cli.parse.NpmCliParser;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmParseResult;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;

public class NpmOutputParserTest {
    @Test
    public void npmCliDependencyFinder() {
        final NpmCliParser parser = new NpmCliParser(new ExternalIdFactory());
        final String testIn = FunctionalTestFiles.asString("/npm/packman_proj_dependencies.json");
        final NpmParseResult result = parser.convertNpmJsonFileToCodeLocation(testIn);

        Assertions.assertEquals("node-js", result.getProjectName());
        Assertions.assertEquals("0.2.0", result.getProjectVersion());
        GraphCompare.assertEqualsResource("/npm/npmParseOutput_graph.json", result.getCodeLocation().getDependencyGraph());
    }
}
