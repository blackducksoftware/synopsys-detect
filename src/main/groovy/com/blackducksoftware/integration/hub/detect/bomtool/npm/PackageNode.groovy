package com.blackducksoftware.integration.hub.detect.bomtool.npm

class PackageNode {
	String name
	String version
	Map<String, String> dependencies
	Map<String, String> optionalDependencies
}
