package com.blackducksoftware.integration.hub.detect.bomtool.npm

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.google.gson.Gson
import com.google.gson.stream.JsonReader

@Component
class NpmNodeModulesDependencyFinder {
	private static final String NODE_MODULES = 'node_modules'
	private static final String PACKAGE_JSON = 'package.json'

	@Autowired
	Gson gson

	public DependencyNode generateDependencyNode(String nodeProjectDirectory) {
		def packageJsonFile = new File(nodeProjectDirectory + '/' + PACKAGE_JSON)
		def nodeModulesFile = new File(nodeProjectDirectory + '/' + NODE_MODULES)

		PackageNode pNode

		if(packageJsonFile.exists()) {
			pNode = deserializeJsonToPackageNode(packageJsonFile)
		}

		if(nodeModulesFile.exists() && nodeModulesFile.isDirectory()) {
			if(pNode) {
				return createNodeFromPackageNode(pNode, nodeModulesFile)
			} else {
				return createNodeFromNodeModules(nodeModulesFile)
			}
		} else if(pNode) {
			//We have only the package.json and no modules actually installed
		}
	}

	/*
	 * The best case scenario for parsing npm without npm installed (package.json and node_modules).
	 * Note: May need to check for circular dependencies, it looks as if node allows it
	 */
	public DependencyNode createNodeFromPackageNode(PackageNode pNode, File nodeModulesFile) {
		DependencyNode node = createDependencyNode(pNode)

		for(String filename : pNode.dependencies.keySet()) {
			def moduleFile = new File(nodeModulesFile.getAbsolutePath() + '/' + filename + '/' + PACKAGE_JSON)

			node.children.add(createNodeFromPackageNode(deserializeJsonToPackageNode(moduleFile), nodeModulesFile))
		}

		node
	}

	/*
	 * A slower check through the node_modules directory that doesn't require a starting package.json for the tree
	 */
	public DependencyNode createNodeFromNodeModules(File nodeModulesFile) {
		def pNode = new PackageNode();
		pNode.name = 'NULL'
		pNode.version = new Date().format('MM.dd.yy')

		final DependencyNode node = createDependencyNode(pNode)

		/*
		 * I think I can use this here in java, but doesn't look to work with groovy
		 * https://stackoverflow.com/questions/20001427/double-colon-operator-in-java-8/20001866
		 */
		for(String filename : nodeModulesFile.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		}))
		{
			def moduleFile = new File(nodeModulesFile.getAbsolutePath() + '/' + filename + '/' + PACKAGE_JSON)

			DependencyNode tempNode = createNodeFromPackageNode(deserializeJsonToPackageNode(moduleFile), nodeModulesFile)

			node.children.add(tempNode)
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
		gson.fromJson(new JsonReader(new FileReader(depOut)), PackageNode.class)
	}
}
