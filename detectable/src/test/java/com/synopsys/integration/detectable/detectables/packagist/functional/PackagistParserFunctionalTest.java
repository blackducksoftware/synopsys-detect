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
package com.synopsys.integration.detectable.detectables.packagist.functional;

import org.junit.Assert;
import org.junit.Test;

import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.packagist.ComposerLockDetectableOptions;
import com.synopsys.integration.detectable.detectables.packagist.model.PackagistParseResult;
import com.synopsys.integration.detectable.detectables.packagist.parse.PackagistParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;

public class PackagistParserFunctionalTest {

    @Test
    public void packagistParserTest() throws MissingExternalIdException {
        final ComposerLockDetectableOptions composerLockDetectableOptions = new ComposerLockDetectableOptions(true);
        final PackagistParser packagistParser = new PackagistParser(new ExternalIdFactory(), composerLockDetectableOptions);

        final String composerLockText = FunctionalTestFiles.asString("/packagist/composer.lock");
        final String composerJsonText = FunctionalTestFiles.asString("/packagist/composer.json");
        final PackagistParseResult result = packagistParser.getDependencyGraphFromProject(composerJsonText, composerLockText);

        Assert.assertEquals("clue/graph-composer", result.getProjectName());
        Assert.assertEquals("1.0.0", result.getProjectVersion());

        GraphCompare.assertEqualsResource("/packagist/PackagistTestDependencyNode_graph.json", result.getCodeLocation().getDependencyGraph());
    }
}
