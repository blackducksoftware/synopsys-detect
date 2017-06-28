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
package com.blackducksoftware.integration.hub.detect.bomtool.cran;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;import java.io.ObjectOutputStream.ReplaceTable
import java.util.ArrayList;
import java.util.HashSet;


import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode;
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeBuilder;
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeImpl;import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer

public class PackRatNodeParser {

	private final Logger logger = LoggerFactory.getLogger(PackRatNodeParser.class)

	private NameVersionNode rootNameVersionNode
	private NameVersionNodeBuilder nameVersionNodeBuilder
	private HashSet<String> directDependencyNames
	private NameVersionNode currentParent

	private boolean inSpecsSection = false
	private boolean inDependenciesSection = false

	void parseProjectDependencies(NameVersionNodeTransformer nameVersionNodeTransformer, DependencyNode rootProject, final String packratLockContents, Forge CRAN) {
		        rootNameVersionNode = new NameVersionNodeImpl([name: rootProject.name, version: rootProject.version])
		        nameVersionNodeBuilder = new NameVersionNodeBuilder(rootNameVersionNode)
		        directDependencyNames = new HashSet<>()
		        currentParent = rootNameVersionNode
				
				String[] lines = packratLockContents.split("\n")
				String name;
				String version;
		
				for (String line : lines) {
					//if (line != null) {
						
						
						if (line.contains("Package")){
							name = line.replace("Package: ", "").trim();
							directDependencyNames.add(name)
						}
						
						if(line.contains("Version")){
							version = line.replace("Version:", "").trim();
						}
						
						currentParent = this.createNameVersionNodeImpl(name, version)
						
						if (line.contains("Requires")) {
							String[] parts = line.replace("Requires","").split(",");
							for (int i; i < parts.size(); i++){
								NameVersionNode node = this.createNameVersionNodeImpl(parts[i], "")
								nameVersionNodeBuilder.addChildNodeToParent(currentParent, node)
								
							}
						//}
					
		
					}
					
						currentParent = rootNameVersionNode
		
				}
				
				directDependencyNames.each { directDependencyName ->
				NameVersionNode nameVersionNode = nameVersionNodeBuilder.nameToNodeMap[directDependencyName]
				if (nameVersionNode) {
					DependencyNode directDependencyNode = nameVersionNodeTransformer.createDependencyNode(CRAN, nameVersionNode)
					rootProject.children.add(directDependencyNode)
				} else {
					logger.error("Could not find ${directDependencyName} in the populated map.")
				}
			}
		
		
	}


	private NameVersionNode createNameVersionNodeImpl(String name, String version){
	        return new NameVersionNodeImpl([name: name, version: version])
	        
		}
	
	/*private NameVersionNode createNameVersionNode(String name, String version){
        nameVersionNodeBuilder.addChildNodeToParent(new NameVersionNode([name: name, version: version]), rootNameVersionNode)
        
	}*/

}
