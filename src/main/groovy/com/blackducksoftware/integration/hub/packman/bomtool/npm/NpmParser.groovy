package com.blackducksoftware.integration.hub.packman.bomtool.npm

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import org.springframework.stereotype.Component

@Component
class NpmParser {
	
	public DependencyNode retrieveDependencyNode(String nodeProjectDirectory) {
		retrieveDependencyNode(nodeProjectDirectory, null)
	}
	
	public DependencyNode retrieveDependencyNode(String nodeProjectDirectory, String npmExePath) {
		if(npmExePath) {
			def npmCliParser = new NpmCliDependencyFinder()
			return npmCliParser.generateDependencyNode(nodeProjectDirectory, npmExePath)
		} else {
			def npmNodeModulesReader = new NpmNodeModulesDependencyFinder()
			return npmNodeModulesReader.generateDependencyNode(nodeProjectDirectory)
		}
	}

}