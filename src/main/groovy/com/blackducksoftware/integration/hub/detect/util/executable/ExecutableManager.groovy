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
package com.blackducksoftware.integration.hub.detect.util.executable

import javax.annotation.PostConstruct

import org.apache.commons.lang3.SystemUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.type.OperatingSystemType
import com.blackducksoftware.integration.hub.detect.util.FileFinder

@Component
class ExecutableManager {
	private final Logger logger = LoggerFactory.getLogger(ExecutableManager.class)

	@Autowired
	FileFinder fileFinder

	OperatingSystemType currentOs

	@PostConstruct
	void init() {
		if (SystemUtils.IS_OS_LINUX) {
			currentOs = OperatingSystemType.LINUX
		} else if (SystemUtils.IS_OS_MAC) {
			currentOs = OperatingSystemType.MAC
		} else if (SystemUtils.IS_OS_WINDOWS) {
			currentOs = OperatingSystemType.WINDOWS
		}

		if (!currentOs) {
			logger.warn("Your operating system is not supported. Linux will be assumed.")
			currentOs = OperatingSystemType.LINUX
		} else {
			logger.info("You seem to be running in a ${currentOs} operating system.")
		}
	}

	String getPathOfExecutable(ExecutableType executableType) {
		File executableFile = getExecutable(executableType)

		null == executableFile ? null : executableFile.absolutePath
	}

	File getExecutable(ExecutableType executableType) {
		String executable = executableType.getExecutable(currentOs)
		File executableFile = findExecutableFile(executable)

		executableFile
	}

	String getPathOfExecutable(String path, ExecutableType executableType) {
		File executableFile = getExecutable(path, executableType)

		null == executableFile ? null : executableFile.absolutePath
	}

	File getExecutable(String path, ExecutableType executableType) {
		String executable = executableType.getExecutable(currentOs)
		File executableFile = findExecutableFile(path, executable)

		executableFile
	}

	private File findExecutableFile(final String executable) {
		String systemPath = System.getenv("PATH")
		return findExecutableFile(systemPath, executable)
	}

	private File findExecutableFile(final String path, String executable) {
		for (String pathPiece : path.split(File.pathSeparator)) {
			File foundFile = fileFinder.findFile(pathPiece, executable)
			if (foundFile && foundFile.canExecute()) {
				return foundFile
			}
		}
		null
	}
}
