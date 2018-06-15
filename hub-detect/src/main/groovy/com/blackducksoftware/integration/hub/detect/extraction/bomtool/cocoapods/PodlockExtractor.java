/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.cocoapods;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.cocoapods.parse.CocoapodsPackager;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extractor;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction.ExtractionResultType;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

@Component
public class PodlockExtractor extends Extractor<PodlockContext> {

    @Autowired
    CocoapodsPackager cocoapodsPackager;

    @Autowired
    protected ExternalIdFactory externalIdFactory;

    @Override
    public Extraction extract(final PodlockContext context) {
        String podLockText;
        try {
            podLockText = FileUtils.readFileToString(context.podlock, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            return new Extraction.Builder().exception(e).build();
        }

        DependencyGraph dependencyGraph;
        try {
            dependencyGraph = cocoapodsPackager.extractDependencyGraph(podLockText);
        } catch (final IOException e) {
            return new Extraction.Builder().exception(e).build();
        }

        final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.COCOAPODS, context.directory.toString());

        final DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(BomToolType.COCOAPODS, context.directory.toString(), externalId, dependencyGraph).build();

        return new Extraction.Builder().success(codeLocation).build();
    }

}
