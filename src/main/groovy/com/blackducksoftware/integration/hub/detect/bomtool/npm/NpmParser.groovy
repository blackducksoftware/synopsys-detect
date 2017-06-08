package com.blackducksoftware.integration.hub.detect.bomtool.npm

import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode

@Component
class NpmParser {
	
	@Autowired
	NpmCliDependencyFinder npmCliParser
	
	@Autowired
	NpmNodeModulesDependencyFinder npmNodeModulesReader
	
	public DependencyNode retrieveDependencyNode(String nodeProjectDirectory) {
		retrieveDependencyNode(nodeProjectDirectory, null)
	}
	
	public DependencyNode retrieveDependencyNode(String nodeProjectDirectory, String npmExePath) {
		if(npmExePath) {
			return npmCliParser.generateDependencyNode(nodeProjectDirectory, npmExePath)
		} else {
			return npmNodeModulesReader.generateDependencyNode(nodeProjectDirectory)
		}
	}

}