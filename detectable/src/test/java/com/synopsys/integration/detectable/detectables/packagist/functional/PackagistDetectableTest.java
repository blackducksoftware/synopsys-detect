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

import java.io.IOException;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectables.packagist.ComposerLockDetectableOptions;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class PackagistDetectableTest extends DetectableFunctionalTest {

    public PackagistDetectableTest() throws IOException {
        super("composer");
    }

    @Override
    protected void setup() throws IOException {
        addFile(Paths.get("composer.lock"),
            "{",
            "   \"_readme\": [\"dummy README\"],",
            "   \"hash\": \"3bedbf455f54168900e467a64bbe2b86\",",
            "   \"packages\": [",
            "       {",
            "           \"name\": \"clue/graph\",",
            "           \"version\": \"v0.9.0\",",
            "           \"source\": {",
            "               \"type\": \"git\",",
            "               \"url\": \"https://github.com/clue/graph.git\",",
            "               \"reference\": \"0336a4d5229fa61a20ccceaeab25e52ac9542700\"",
            "           },",
            "           \"require\": {",
            "               \"php\": \">=5.3.0\"",
            "           },",
            "           \"require-dev\": {",
            "               \"phpunit/phpunit\": \"~4.0\"",
            "           }",
            "       },",
            "       {",
            "           \"name\": \"graphp/algorithms\",",
            "           \"version\": \"v0.8.1\",",
            "           \"source\": {",
            "               \"type\": \"git\",",
            "               \"url\": \"https://github.com/graphp/algorithms.git\",",
            "               \"reference\": \"81db4049c35730767ec8f97fb5c4844234b86cef\"",
            "           },",
            "           \"require\": {",
            "               \"clue/graph\": \"~0.9.0|~0.8.0\",",
            "               \"php\": \">=5.3\"",
            "           },",
            "           \"require-dev\": {",
            "               \"psr/log\": \"~1.0\",",
            "               \"symfony/event-dispatcher\": \"~2.1\",",
            "               \"symfony/phpunit-bridge\": \"~2.7\"",
            "           }",
            "       },",
            "   ],",
            "   \"packages-dev\": []",
            "}"
        );

        addFile(Paths.get("composer.json"),
            "{",
            "   \"name\": \"clue/graph-composer\",",
            "   \"version\": \"1.0.0\",",
            "   \"keywords\": [\"dependency graph\", \"visualize dependencies\", \"visualize composer\"],",
            "   \"license\": \"MIT\",",
            "   \"require\": {",
            "       \"php\": \"^5.3.6 || ^7.0\",",
            "       \"clue/graph\": \"^0.9.0\",",
            "       \"jms/composer-deps-analyzer\": \"0.1.*\",",
            "       \"symfony/console\": \"^2.1 || ^3.0\",",
            "       \"graphp/graphviz\": \"^0.2.0\"",
            "   },",
            "   \"autoload\": {",
            "       \"psr-0\": { \"Clue\\\\GraphComposer\": \"src/\" }",
            "   },",
            "   \"bin\": [ \"bin/graph-composer\" ],",
            "   \"extra\": {",
            "       \"phar\": {",
            "           \"bundler\": \"composer\"",
            "       }",
            "   },",
            "   \"config\": {",
            "       \"platform\": {",
            "           \"php\": \"5.3.6\"",
            "       }",
            "   }",
            "}"
        );
    }

    @NotNull
    @Override
    public Detectable create(@NotNull final DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createComposerDetectable(detectableEnvironment, new ComposerLockDetectableOptions(true));
    }

    @Override
    public void assertExtraction(@NotNull final Extraction extraction) {
        Assertions.assertNotEquals(0, extraction.getCodeLocations().size(), "A code location should have been generated.");

        final NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PACKAGIST, extraction.getCodeLocations().get(0).getDependencyGraph());

        graphAssert.hasRootSize(1);
        graphAssert.hasRootDependency("clue/graph", "v0.9.0");
        graphAssert.hasParentChildRelationship("graphp/algorithms", "v0.8.1", "clue/graph", "v0.9.0");
    }
}
