/* Copyright (C) 2017 Black Duck Software, Inc.
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

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.util.ProjectInfoGatherer

public class PackratPackager {
   private final ProjectInfoGatherer projectInfoGatherer
   private final NameVersionNodeTransformer nameVersionNodeTransformer

   public PackratPackager(final ProjectInfoGatherer projectInfoGatherer, NameVersionNodeTransformer nameVersionNodeTransformer) {
	   this.projectInfoGatherer = projectInfoGatherer
	   this.nameVersionNodeTransformer = nameVersionNodeTransformer
   }

   public List<DependencyNode> makeDependencyNodes(final String sourcePath, final String packratLock) {
	   String packratLessPath = sourcePath //Transform here
	   final String rootName = projectInfoGatherer.getDefaultProjectName(BomToolType.CRAN, packratLessPath)
	   final String rootVersion = projectInfoGatherer.getDefaultProjectVersionName()
	   Forge CRAN = new Forge("cran", "/");
	   
	   final ExternalId rootExternalId = new NameVersionExternalId(CRAN, rootName, rootVersion)
	   final DependencyNode root = new DependencyNode(rootName, rootVersion, rootExternalId)

	   PackRatNodeParser packratNodeParser = new PackRatNodeParser()
	   packratNodeParser.parseProjectDependencies(nameVersionNodeTransformer, root, packratLock, CRAN)

	   [root]
   }
}
