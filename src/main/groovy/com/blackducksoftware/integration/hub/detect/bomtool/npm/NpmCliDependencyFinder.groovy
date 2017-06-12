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
package com.blackducksoftware.integration.hub.detect.bomtool.npm

import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner
import com.google.gson.Gson
import com.google.gson.stream.JsonReader

@Component
class NpmCliDependencyFinder {

	private final Logger logger = LoggerFactory.getLogger(NpmCliDependencyFinder.class)

	@Autowired
	Gson gson

	@Autowired
	NpmNodeModulesDependencyFinder nmFinder

	public DependencyNode generateDependencyNode(String rootDirectoryPath, String exePath) {
		DependencyNode result;

		def npmLsExe = new Executable(new File(rootDirectoryPath), exePath, ['ls', '-json'])
		def exeRunner = new ExecutableRunner()
		def tempJsonOutFile = new File(NpmConstants.OUTPUT_FILE)

		exeRunner.executeToFile(npmLsExe, tempJsonOutFile)

		if(tempJsonOutFile?.length() > 0) {
			logger.info("Running npm ls and converting values")
			result = convertNpmJsonFileToDependencyNode(tempJsonOutFile)
		} else {
			logger.info("Npm doesn't look to be installed, finding alternative routes...")
			result = nmFinder.generateDependencyNode(rootDirectoryPath)
		}

		if(tempJsonOutFile) {
			try {
				tempJsonOutFile.delete()
			} catch (IOException e) {
				println(e.stackTrace)
			}
		}

		result
	}

	private DependencyNode convertNpmJsonFileToDependencyNode(File file) {
		convertToDependencyNode(jsonDeserialization(file), null)
	}

	private NpmCliNode jsonDeserialization(File depOut) {
		gson.fromJson(new JsonReader(new FileReader(depOut)), NpmCliNode.class)
	}

	private DependencyNode convertToDependencyNode(NpmCliNode node, String keyName) {
		def name = node.name

		//If a keyname is provided through recursion, use that name instead of the node name (Because of package.json structure)
		if(keyName) {
			name = keyName
		}

		def version = node.version
		def externalId = new NameVersionExternalId(Forge.NPM, name, version)
		def dependencyNode = new DependencyNode(name, version, externalId)
		node.dependencies.each {
			dependencyNode.children.add(convertToDependencyNode(it.getValue(), it.getKey()))
		}

		dependencyNode
	}
}
