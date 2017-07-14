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
/* Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.detect.bomtool.cran;

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode;
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeBuilder;
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer

public class DescriptionParser {

	private final Logger logger = LoggerFactory.getLogger(PackRatNodeParser.class)

	private NameVersionNode rootNameVersionNode
	private NameVersionNodeBuilder nameVersionNodeBuilder
	private HashSet<String> directDependencyNames
	private NameVersionNode currentParent

	private boolean inSpecsSection = false
	private boolean inDependenciesSection = false

	private NameVersionNodeTransformer nameVersionNodeTransformer
	private DependencyNode rootProject
	private final String packratLockContents
	private Forge CRAN
	private String lines




	String getProjectVersion(final String descriptionContents){
		String[] lines = descriptionContents.split("\n")
		String version;

		for (String line : lines) {

			if (line.contains("Version")){
				version = line.replace("Version: ", "").trim();
				break
			}
		}

		version
	}
}