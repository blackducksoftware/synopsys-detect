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
package com.blackducksoftware.integration.hub.detect.bomtool

import java.nio.charset.StandardCharsets

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.PathExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.cran.PackratPackager
import com.blackducksoftware.integration.hub.detect.bomtool.output.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.type.BomToolType

@Component
class CranBomTool extends BomTool {
	@Autowired
	PackratPackager packratPackager

	BomToolType getBomToolType() {
		return BomToolType.CRAN
	}

	boolean isBomToolApplicable() {

		detectFileManager.containsAllFilesToDepth(sourcePath, detectConfiguration.getSearchDepth(),'packrat.lock')




		//		matchingSourcePaths = sourcePathSearcher.findFilenamePattern(detectConfiguration.getSearchDepth(), 'packrat.lock')
		//		descriptionSourcePaths = sourcePathSearcher.findFilenamePattern('DESCRIPTION')
		//
		//		!matchingSourcePaths.isEmpty()
	}


	List<DetectCodeLocation> extractDetectCodeLocations() {
		File sourceDirectory = detectConfiguration.sourceDirectory

		def packratLockFile = detectFileManager.findFilesToDepth(sourceDirectory, 'packrat.lock', detectConfiguration.getSearchDepth())
		//		def packratLockFile = new File(sourceDirectory, 'packrat.lock')
		String projectVersion = ''
		if(detectFileManager.containsAllFilesToDepth(sourcePath, 1,'packrat.lock')){
			def descriptionFile = new File(sourceDirectory, 'DESCRIPTION')
			String descriptionText = descriptionFile.getText(StandardCharsets.UTF_8.name())
			projectVersion = packratPackager.getVersion(descriptionText)
		}
		String packratLockText = packratLockFile[0].getText(StandardCharsets.UTF_8.name())
		Forge CRAN = new Forge("cran", "/")
		List<DependencyNode> dependencies = packratPackager.extractProjectDependencies(packratLockText, CRAN)
		Set<DependencyNode> dependenciesSet = new HashSet<>(dependencies)
		ExternalId externalId = new PathExternalId(CRAN, sourcePath)
		//String hash = getHash(packratLockText)

		def codeLocation = new DetectCodeLocation(getBomToolType(), sourcePath, '', projectVersion, '', externalId, dependenciesSet)
		[codeLocation]
	}


	//	List<DependencyNode> extractDependencyNodes() {
	//		List<DependencyNode> projectNodes = []
	//		matchingSourcePaths.each { sourcePath ->
	//			File sourceDirectory = new File(sourcePath)
	//			File[] packratLockFile = detectFileManager.findFilesToDepth(sourceDirectory, 'packrat.lock', detectConfiguration.getSearchDepth())
	//			File descriptionFile = null
	//			if(!descriptionSourcePaths.isEmpty()){
	//				descriptionSourcePaths.each { descriptionPath ->
	//					File descriptionDirectory = new File(sourcePath)
	//					descriptionFile = new File(sourceDirectory, 'DESCRIPTION')
	//				}
	//			}
	//
	//			final InputStream packratLockStream
	//			final InputStream descriptionStream
	//			try {
	//				packratLockStream = new FileInputStream(packratLockFile[0])
	//				if(descriptionFile){
	//					descriptionStream = new FileInputStream(descriptionFile)
	//				}
	//				String potentialProjectName = sourceDirectory.getName()
	//				String packratLock = IOUtils.toString(packratLockStream, StandardCharsets.UTF_8)
	//				String description = null
	//				if(descriptionStream){
	//					description = IOUtils.toString(descriptionStream, StandardCharsets.UTF_8)
	//				}
	//				def packratPackager = new PackratPackager(projectInfoGatherer, nameVersionNodeTransformer)
	//				def projects = packratPackager.makeDependencyNodes(sourcePath, packratLock, description)
	//				projectNodes.addAll(projects)
	//			} finally {
	//				IOUtils.closeQuietly(packratLockStream)
	//				IOUtils.closeQuietly(descriptionStream)
	//			}
	//		}
	//		projectNodes
	//	}
}