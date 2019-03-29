/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.go.godep;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.go.godep.parse.GoLockParser;

public class GoDepExtractor {
    private final GoDepLockFileGenerator goDepLockFileGenerator;
    private final GoLockParser goLockParser;
    private final ExternalIdFactory externalIdFactory;

    public GoDepExtractor(final GoDepLockFileGenerator goDepLockFileGenerator, final GoLockParser goLockParser, final ExternalIdFactory externalIdFactory) {
        this.goDepLockFileGenerator = goDepLockFileGenerator;
        this.goLockParser = goLockParser;
        this.externalIdFactory = externalIdFactory;
    }

    public Extraction extract(final File directory, final File goDepInspector, final boolean allowsRunInit) {
        try {
            final Optional<File> lockFile = goDepLockFileGenerator.findOrMakeLockFile(directory, goDepInspector, allowsRunInit);

            if (lockFile.isPresent()) {
                try (final InputStream lockInputStream = new FileInputStream(lockFile.get())) {
                    return extract(directory, lockInputStream);
                }
            } else {
                return new Extraction.Builder().failure("Failed to find a go lock file.").build();
            }
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    public Extraction extract(final File directory, final InputStream goLockInputStream) {
        final DependencyGraph graph = goLockParser.parseDepLock(goLockInputStream);
        final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.GOLANG, directory.toString()); // TODO: don't use directory as external id
        final CodeLocation codeLocation = new CodeLocation(graph, externalId);
        return new Extraction.Builder().success(codeLocation).build();
    }

}
