package com.blackducksoftware.integration.hub.packman.bomtool

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.bomtool.npm.NpmParser
import com.blackducksoftware.integration.hub.packman.type.BomToolType
import com.blackducksoftware.integration.hub.packman.type.ExecutableType

class NpmBomTool extends BomTool {
	private NpmParser npmParser = new NpmParser()
	private static final String packageJson = 'package.json'

	private String npmExe
	private List<String> npmPaths = []

	@Override
	public BomToolType getBomToolType() {
		BomToolType.NPM
	}

	@Override
	public boolean isBomToolApplicable() {
		npmExe = getExecutablePath()
		npmPaths = sourcePathSearcher.findSourcePathsContainingFilenamePattern(packageJson)

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

	/*
	 * Not yet defined
	 */
	private String getExecutablePath() {
		if(!packmanProperties.npmPath) {
			return executableManager.getPathOfExecutable(ExecutableType.NPM)
		}
		packmanProperties.npmPath
	}

}
