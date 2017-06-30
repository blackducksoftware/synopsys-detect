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

import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.bomtool.cran.PackratPackager
import com.blackducksoftware.integration.hub.detect.type.BomToolType

@Component
class CranBomTool extends BomTool {
	List<String> matchingSourcePaths = []
	static final String PACKRAT_PATTERN = "packrat.lock"
	static final String DESCRIPTION_PATTERN = "DESCRIPTION"

	BomToolType getBomToolType() {
		return BomToolType.CRAN
	}

	boolean isBomToolApplicable() {
		//File sourceDirectory = new File(sourcePath)
		if (sourcePathSearcher.findFilenamePattern(2, 'packrat.lock')){
			matchingSourcePaths.addAll(sourcePathSearcher.findFilenamePattern(3, 'packrat.lock'))
		}
		else{
			matchingSourcePaths.add("DNE")
		}
		if (sourcePathSearcher.findFilenamePattern(1, 'DESCRIPTION')){
		matchingSourcePaths.addAll(sourcePathSearcher.findFilenamePattern(1, 'DESCRIPTION'))
		}
		else{
			matchingSourcePaths.add("DNE")
		}

		!matchingSourcePaths.isEmpty()
	}

	List<DependencyNode> extractDependencyNodes() {
		List<DependencyNode> projectNodes = []
		int counter = 0
		String sourcePath
		File sourceDirectory
		File[] packratLockFile
			if (matchingSourcePaths[0] != "DNE"){
			sourcePath = matchingSourcePaths[0]
			sourceDirectory = new File(sourcePath)
			packratLockFile = detectFileManager.findFilesToDepth(sourceDirectory, 'packrat.lock', 2)
			counter ++
			}
			File[] descriptionFile
			if(matchingSourcePaths[1] != "DNE"){
			sourcePath = matchingSourcePaths[1]
			sourceDirectory = new File(sourcePath)
			descriptionFile = detectFileManager.findFilesToDepth(sourceDirectory, 'DESCRIPTION', 1)
			counter --
			}
			
			switch (counter){
				case 0: 
				
					final InputStream packratLockStream
					final InputStream descriptionStream
					try {
						packratLockStream = new FileInputStream(packratLockFile[0])
						descriptionStream = new FileInputStream(descriptionFile[0])
						String potentialProjectName = sourceDirectory.getName()
						String packratLock = IOUtils.toString(packratLockStream, StandardCharsets.UTF_8)
						String description = IOUtils.toString(descriptionStream, StandardCharsets.UTF_8)
						def packratPackager = new PackratPackager(projectInfoGatherer, nameVersionNodeTransformer)
						def projects = packratPackager.makeDependencyNodes(sourcePath, packratLock, description)
						projectNodes.addAll(projects)
					} finally {
						IOUtils.closeQuietly(packratLockStream)
						IOUtils.closeQuietly(descriptionStream)
					}
					break
				
				case -1:
					
					final InputStream descriptionStream
					try {
						descriptionStream = new FileInputStream(descriptionFile[0])
						String description = IOUtils.toString(descriptionStream, StandardCharsets.UTF_8)
						def packratPackager = new PackratPackager(projectInfoGatherer, nameVersionNodeTransformer)
						def projects = packratPackager.makeDependencyNodes(sourcePath, "DNE", description)
						projectNodes.addAll(projects)
					} finally {
						IOUtils.closeQuietly(descriptionStream)
					}
					break
					
				case 1: 
				
					final InputStream packratLockStream
					try {
						packratLockStream = new FileInputStream(packratLockFile[0])
						String potentialProjectName = sourceDirectory.getName()
						String packratLock = IOUtils.toString(packratLockStream, StandardCharsets.UTF_8)
						def packratPackager = new PackratPackager(projectInfoGatherer, nameVersionNodeTransformer)
						def projects = packratPackager.makeDependencyNodes(sourcePath, packratLock, "DNE")
						projectNodes.addAll(projects)
					} finally {
						IOUtils.closeQuietly(packratLockStream)
					}
					break
			
			}
		
		projectNodes
	}
}