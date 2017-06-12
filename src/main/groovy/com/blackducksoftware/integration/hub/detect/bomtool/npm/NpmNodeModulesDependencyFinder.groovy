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

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.util.FileFinder
import com.google.gson.Gson
import com.google.gson.stream.JsonReader

@Component
class NpmNodeModulesDependencyFinder {

	@Autowired
	Gson gson

	@Autowired
	FileFinder fileFinder

	/*
	 * arraylist used to check for circular dependencies
	 */
	private ArrayList<String> modules = new ArrayList<String>()

	public DependencyNode generateDependencyNode(String nodeProjectDirectory) {
		def packageJsonFile = fileFinder.findFile(nodeProjectDirectory, NpmConstants.PACKAGE_JSON)
		def nodeModulesFile = fileFinder.findFile(nodeProjectDirectory, NpmConstants.NODE_MODULES)

		PackageNode pNode

		if(packageJsonFile?.exists()) {
			pNode = deserializeJsonToPackageNode(packageJsonFile)
		}

		if(nodeModulesFile.exists() && nodeModulesFile.isDirectory()) {
			if(pNode) {
				return createNodeFromPackageNode(pNode, nodeModulesFile)
			} else {
				return createNodeFromNodeModules(nodeModulesFile)
			}
		} else if(pNode) {
			//We have only the package.json and no node_modules folder which would require REST calls to complete. Try to log something
		}
	}

	/*
	 * The best case scenario for parsing npm without npm installed (uses package.json and node_modules).
	 * Note: May need to check for circular dependencies, it looks as if node allows it
	 */
	public DependencyNode createNodeFromPackageNode(PackageNode pNode, File nodeModulesFile) {
		DependencyNode node = createDependencyNode(pNode)

		//Set<String> allDependencies = pNode?.dependencies?.keySet()?.addAll(pNode.optionalDependencies?.keySet())

		for(String filename : pNode.dependencies?.keySet()) {
			if(modules.contains(filename)) {
				continue
			} else {
				modules.add(filename)
				def packageFile = fileFinder.findFile(nodeModulesFile.getAbsolutePath() + '/' + filename + '/', NpmConstants.PACKAGE_JSON)
				if(packageFile) {
					node.children.add(createNodeFromPackageNode(deserializeJsonToPackageNode(packageFile), nodeModulesFile))
				}
			}
		}

		node
	}

	/*
	 * A slower check through the node_modules directory that doesn't require a starting package.json for the tree.
	 */
	public DependencyNode createNodeFromNodeModules(File nodeModulesFile) {
		def pNode = new PackageNode();
		pNode.name = 'BrianMandel'
		pNode.version = new Date().format('MM.dd.yy')

		final DependencyNode node = createDependencyNode(pNode)

		nodeModulesFile.eachDir() { dir ->
			def packageFile = fileFinder.findFile(dir.getAbsolutePath() + '/', NpmConstants.PACKAGE_JSON)
			if(packageFile) {
				DependencyNode tempNode = createNodeFromPackageNode(deserializeJsonToPackageNode(packageFile), nodeModulesFile)

				node.children.add(tempNode)
			}
		}

		node
	}

	private DependencyNode createDependencyNode(PackageNode pNode) {
		def name = pNode.name
		def version = pNode.version
		def externalId = new NameVersionExternalId(Forge.NPM, name, version)

		def node = new DependencyNode(name, version, externalId)
	}

	public PackageNode deserializeJsonToPackageNode(File depOut) {
		def fileReader
		def jsonReader
		try {
			fileReader = new FileReader(depOut)
			jsonReader = new JsonReader(fileReader)
		} catch (Exception e) {
			println(depOut)
			e.printStackTrace()
		}
		def packageNode = (PackageNode) gson.fromJson(jsonReader, PackageNode.class)
		packageNode
	}
}
