package com.blackducksoftware.integration.hub.detect.bomtool

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmParser
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.type.ExecutableType

@Component
class NpmBomTool extends BomTool {

	@Autowired
	NpmParser npmParser

	private static final String PACKAGE_JSON = 'package.json'
	private String npmExe
	private List<String> npmPaths = []

	@Override
	public BomToolType getBomToolType() {
		BomToolType.NPM
	}

	@Override
	public boolean isBomToolApplicable() {
		npmExe = getExecutablePath()
		npmPaths = sourcePathSearcher.findSourcePathsContainingFilenamePattern(PACKAGE_JSON)

		npmExe && npmPaths
	}

	@Override
	public List<DependencyNode> extractDependencyNodes() {
		List<DependencyNode> nodes = []

		npmPaths.each {
			nodes.add(npmParser.retrieveDependencyNode(it, npmExe))
		}

		nodes
	}

	private String getExecutablePath() {
		if(!detectProperties.npmPath) {
			return executableManager.getPathOfExecutable(ExecutableType.NPM)
		}
		detectProperties.npmPath
	}

}
