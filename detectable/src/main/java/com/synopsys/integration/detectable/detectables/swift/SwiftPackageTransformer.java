/*
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
package com.synopsys.integration.detectable.detectables.swift;

import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.swift.model.SwiftPackage;

public class SwiftPackageTransformer {
    public static final Forge SWIFT_FORGE = Forge.COCOAPODS;

    private final ExternalIdFactory externalIdFactory;

    public SwiftPackageTransformer(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public CodeLocation transform(final SwiftPackage rootSwiftPackage) {
        final MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        for (final SwiftPackage swiftPackageDependency : rootSwiftPackage.getDependencies()) {
            final Dependency dependency = convertToDependency(dependencyGraph, swiftPackageDependency);
            dependencyGraph.addChildToRoot(dependency);
        }

        return new CodeLocation(dependencyGraph);
    }

    private Dependency convertToDependency(final MutableDependencyGraph dependencyGraph, final SwiftPackage swiftPackage) {
        final ExternalId externalId;
        if ("unspecified".equals(swiftPackage.getVersion())) {
            externalId = externalIdFactory.createModuleNamesExternalId(SWIFT_FORGE, swiftPackage.getName());
        } else {
            externalId = externalIdFactory.createNameVersionExternalId(SWIFT_FORGE, swiftPackage.getName(), swiftPackage.getVersion());
        }
        final Dependency dependency = new Dependency(externalId);

        for (final SwiftPackage swiftPackageDependency : swiftPackage.getDependencies()) {
            final Dependency childDependency = convertToDependency(dependencyGraph, swiftPackageDependency);
            dependencyGraph.addParentWithChild(dependency, childDependency);
        }

        return dependency;
    }
}
