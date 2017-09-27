/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.detect.bomtool.cran

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder
import com.blackducksoftware.integration.hub.bdio.model.dependencyid.DependencyId
import com.blackducksoftware.integration.hub.bdio.model.dependencyid.NameDependencyId
import com.blackducksoftware.integration.hub.bdio.model.dependencyid.NameVersionDependencyId
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.bomtool.CranBomTool

import groovy.transform.TypeChecked

@TypeChecked
public class PackRatNodeParser {
    private final Logger logger = LoggerFactory.getLogger(PackRatNodeParser.class)
    
    public ExternalIdFactory externalIdFactory;
    public PackRatNodeParser(ExternalIdFactory externalIdFactory){
        this.externalIdFactory = externalIdFactory;
    }

    DependencyGraph parseProjectDependencies(final String packratLockContents) {
        LazyExternalIdDependencyGraphBuilder graphBuilder = new LazyExternalIdDependencyGraphBuilder();

        DependencyId currentParent = null

        String[] lines = packratLockContents.split(System.lineSeparator())
        String name
        String version

        for (String line : lines) {
            if (line.contains('Package: ')) {
                name = line.replace('Package: ', '').trim()
                currentParent = new NameDependencyId(name);
                graphBuilder.setDependencyName(currentParent, name);
                graphBuilder.addChildToRoot(currentParent);
                version = null;
                continue
            }

            if (line.contains('Version: ')) {
                version = line.replace('Version: ', '').trim()
                graphBuilder.setDependencyVersion(currentParent, version);
                DependencyId realId = new NameVersionDependencyId(name, version);
                ExternalId externalId = externalIdFactory.createNameVersionExternalId(CranBomTool.CRAN, name, version)
                graphBuilder.setDependencyAsAlias(realId, currentParent);
                graphBuilder.setDependencyInfo(realId, name, version, externalId)
            }

            currentParent = new NameVersionDependencyId(name, version)

            if (line.contains('Requires: ')) {
                String[] parts = line.replace('Requires: ','').split(',')
                for (int i; i < parts.size(); i++) {
                    String childName = parts[i].trim()
                    graphBuilder.addParentWithChild(currentParent, new NameDependencyId(childName))
                }
            }
        }

        graphBuilder.build()
    }
}
