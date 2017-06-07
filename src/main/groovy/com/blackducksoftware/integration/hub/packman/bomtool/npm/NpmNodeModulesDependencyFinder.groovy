package com.blackducksoftware.integration.hub.packman.bomtool.npm

import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeBuilder
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.model.enumeration.ReportFormatEnum
import com.blackducksoftware.integration.hub.packman.util.NameVersionNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge

import com.google.gson.Gson
import com.google.gson.stream.JsonReader

import java.io.File

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

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
			//parse just the package.json since node_modules doesn't exist. Try and get some data for customer
		}
	}
	
	/*
	 * The best case scenario for parsing npm (package.json and node_modules).
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
		for(String filename : nodeModulesFile.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		}))
		{
			def moduleFile = new File(nodeModulesFile.getAbsolutePath() + '/' + filename + '/' + PACKAGE_JSON)
			
			DependencyNode tempNode = createNodeFromPackageNode(deserializeJsonToPackageNode(moduleFile), nodeModulesFile)
			
			//if(!checkIfDependencyNodeExists(node, tempNode)) {
				node.children.add(tempNode)
			//}
		}
		
		node
	}
	
//	private boolean checkIfDependencyNodeExists(DependencyNode originalNode, DependencyNode comparedNode) {
//		if(!originalNode.children) {
//			return originalNode.equals(comparedNode)
//		}
//		
//		def compare = originalNode.equals(comparedNode)
//		if(compare) {
//			return true
//		}
//		
//		for(DependencyNode node : originalNode.children) {
//			compare = (node.equals(comparedNode) || checkIfDependencyNodeExists(node, comparedNode))
//		}
//		
//		compare
//	}
	
	private DependencyNode createDependencyNode(PackageNode pNode) {
		def name = pNode.name
		def version = pNode.version
		def externalId = new NameVersionExternalId(Forge.NPM, name, version)
		
		def node = new DependencyNode(name, version, externalId)
	}
	
	public PackageNode deserializeJsonToPackageNode(File depOut) {
		gson.fromJson(new JsonReader(new FileReader(depOut)), PackageNode.class)
	}
	
	static main(args) {
		def nmParser = new NpmNodeModulesDependencyFinder()
		nmParser.setGson(new Gson())
		def node = nmParser.generateDependencyNode('/Users/bmandel/Documents/NodeJS')
		println(node.toString())
	}
	
}
