/**
 * detect-application
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.synopsys.integration.detect.detector.cran;

import java.util.List;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class PackratPackager {
    private final ExternalIdFactory externalIdFactory;

    public PackratPackager(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph extractProjectDependencies(final List<String> packratLock) {
        PackRatNodeParser packRatNodeParser = new PackRatNodeParser(externalIdFactory);
        return packRatNodeParser.parseProjectDependencies(packratLock);
    }

    public String getProjectName(final List<String> descriptionContents) {
        String name = null;
        for (String line : descriptionContents) {
            if (line.contains("Package: ")) {
                name = line.replace("Package: ", "").trim();
                break;
            }
        }
        return name;
    }

    public String getVersion(final List<String> descriptionContents) {
        for (String descriptionContent : descriptionContents) {
            if (descriptionContent.contains("Version: ")) {
                return descriptionContent.replace("Version: ", "").trim();
            }
        }
        return null;
    }

}
